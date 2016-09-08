package org.protege.editor.owl.ui.view;

import org.protege.editor.core.ui.util.Resettable;
import org.protege.editor.core.ui.view.ViewsPane;
import org.protege.editor.core.ui.view.ViewsPaneMemento;
import org.protege.editor.core.util.HandlerRegistration;
import org.protege.editor.owl.model.selection.SelectionDriver;
import org.protege.editor.owl.model.selection.SelectionPlane;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.protege.editor.owl.ui.util.NothingSelectedPanel;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 02-Mar-2007<br><br>
 */
public class SelectedEntityCardView extends AbstractOWLViewComponent implements Resettable, SelectionPlane {

    public static final String ID = "org.protege.editor.owl.SelectedEntityView";

    private final CardLayout cardLayout = new CardLayout();

    private final JPanel cardPanel = new JPanel();

    private final List<ViewsPane> viewsPanes = new ArrayList<>();

    private static final String CLASSES_PANEL = "Classes";

    private static final String OBJECT_PROPERTIES_PANEL = "ObjectProperties";

    private static final String DATA_PROPERTIES_PANEL = "DataProperties";

    private static final String ANNOTATION_PROPERTIES_PANEL = "AnnotationProperties";

    private static final String INDIVIDUALS_PANEL = "Individual";

    private static final String DATATYPES_PANEL = "Datatypes";

    private static final String BLANK_PANEL = "Blank";


    private static final Logger logger = LoggerFactory.getLogger(SelectedEntityCardView.class);

    private JLabel entityIRILabel;

    private EntityBannerFormatter entityBannerFormatter;

    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        entityBannerFormatter = new EntityBannerFormatterImpl();
        entityIRILabel = new JLabel();
        entityIRILabel.setBorder(BorderFactory.createEmptyBorder(1, 4, 3, 0));
        add(entityIRILabel, BorderLayout.NORTH);
        add(cardPanel);
        cardPanel.setLayout(cardLayout);
        cardPanel.add(new NothingSelectedPanel(), BLANK_PANEL);
        createViewPanes(false);
        getOWLWorkspace().getOWLSelectionModel().addListener(this::processSelection);
        getView().setShowViewBar(false);
        processSelection();
    }


    private void createViewPanes(boolean reset) {
        addPane(CLASSES_PANEL,
                "/selected-entity-view-class-panel.xml",
                "org.protege.editor.owl.ui.view.selectedentityview.classes",
                reset);


        addPane(OBJECT_PROPERTIES_PANEL,
                "/selected-entity-view-objectproperty-panel.xml",
                "org.protege.editor.owl.ui.view.selectedentityview.objectproperties",
                reset);


        addPane(DATA_PROPERTIES_PANEL,
                "/selected-entity-view-dataproperty-panel.xml",
                "org.protege.editor.owl.ui.view.selectedentityview.dataproperties",
                reset);


        addPane(ANNOTATION_PROPERTIES_PANEL,
                "/selected-entity-view-annotationproperty-panel.xml",
                "org.protege.editor.owl.ui.view.selectedentityview.annotproperties",
                reset);


        addPane(INDIVIDUALS_PANEL,
                "/selected-entity-view-individual-panel.xml",
                "org.protege.editor.owl.ui.view.selectedentityview.individuals",
                reset);


        addPane(DATATYPES_PANEL,
                "/selected-entity-view-datatype-panel.xml",
                "org.protege.editor.owl.ui.view.selectedentityview.datatypes",
                reset);
    }


    private void addPane(String panelId, String configFile, String viewPaneId, boolean reset) {
        URL clsURL = getClass().getResource(configFile);
        ViewsPane pane = new ViewsPane(getOWLWorkspace(), new ViewsPaneMemento(clsURL, viewPaneId, reset));
        cardPanel.add(pane, panelId);
        viewsPanes.add(pane);
    }


    public void reset() {
        for (ViewsPane pane : viewsPanes){
            cardPanel.remove(pane);
            pane.dispose();
        }

        viewsPanes.clear();
        entityIRILabel.setText("");
        createViewPanes(true);
        validate();

        for (ViewsPane pane : viewsPanes){
            pane.saveViews();
        }
    }


    private void processSelection() {
        OWLObject selectedObject = getOWLWorkspace().getOWLSelectionModel().getSelectedObject();
        if(selectedObject == null) {
            entityIRILabel.setIcon(null);
            entityIRILabel.setText("");
            entityIRILabel.setBackground(null);
            selectPanel(BLANK_PANEL);
            return;
        }
        if(!(selectedObject instanceof OWLEntity)) {
            return;
        }
        OWLEntity selEntity = (OWLEntity) selectedObject;
        String banner = entityBannerFormatter.formatBanner(selEntity, getOWLEditorKit());
        entityIRILabel.setIcon(getOWLWorkspace().getOWLIconProvider().getIcon(selEntity));
        entityIRILabel.setText(banner);
        selEntity.accept(new OWLEntityVisitor() {
            public void visit(@Nonnull OWLClass cls) {
                selectPanel(CLASSES_PANEL);
            }


            public void visit(@Nonnull OWLObjectProperty property) {
                selectPanel(OBJECT_PROPERTIES_PANEL);
            }


            public void visit(@Nonnull OWLDataProperty property) {
                selectPanel(DATA_PROPERTIES_PANEL);
            }


            public void visit(@Nonnull OWLAnnotationProperty property) {
                selectPanel(ANNOTATION_PROPERTIES_PANEL);
            }


            public void visit(@Nonnull OWLNamedIndividual individual) {
                selectPanel(INDIVIDUALS_PANEL);
            }


            public void visit(@Nonnull OWLDatatype dataType) {
                selectPanel(DATATYPES_PANEL);
            }
        });

    }


    private void selectPanel(String name) {
        cardLayout.show(cardPanel, name);
    }


    protected void disposeOWLView() {
        for (ViewsPane pane : viewsPanes){
            pane.saveViews();
            pane.dispose();
        }
    }

    @Override
    public HandlerRegistration registerSelectionDriver(SelectionDriver driver) {
        return () -> {};
    }

    @Override
    public void transmitSelection(SelectionDriver driver, OWLObject selection) {
        // Since we display the current selection we don't initiate selection changes.  If a user drops a navigation
        // driving view on to this card view then it's probably an error.
        logger.debug("[SelectedEntityCardView] Ignoring request to transmit selection");
    }
}
