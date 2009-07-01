package org.protege.editor.owl.ui.view;

import org.protege.editor.core.ui.util.Resettable;
import org.protege.editor.core.ui.view.ViewsPane;
import org.protege.editor.core.ui.view.ViewsPaneMemento;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 02-Mar-2007<br><br>
 */
public class SelectedEntityCardView extends AbstractOWLViewComponent implements Resettable {

    public static final String ID = "org.protege.editor.owl.SelectedEntityView";

    private CardLayout cardLayout = new CardLayout();

    private JPanel cardPanel;

    private List<ViewsPane> viewsPanes = new ArrayList<ViewsPane>();

    private static final String CLASSES_PANEL = "Classes";

    private static final String OBJECT_PROPERTIES_PANEL = "ObjectProperties";

    private static final String DATA_PROPERTIES_PANEL = "DataProperties";

    private static final String ANNOTATION_PROPERTIES_PANEL = "AnnotationProperties";

    private static final String INDIVIDUALS_PANEL = "Individual";

    private static final String DATATYPES_PANEL = "Datatypes";

    private static final String BLANK_PANEL = "Blank";



    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        cardPanel = new JPanel();
        add(cardPanel);
        cardPanel.setLayout(cardLayout);
        cardPanel.add(new JPanel(), BLANK_PANEL);
        createViewPanes(false);
        getOWLWorkspace().getOWLSelectionModel().addListener(new OWLSelectionModelListener() {
            public void selectionChanged() throws Exception {
                processSelection();
            }
        });
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
                "org.protege.editor.owl.ui.view.selectedentityview.annotationproperties",
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
        createViewPanes(true);
        validate();

        for (ViewsPane pane : viewsPanes){
            pane.saveViews();
        }
    }


    private void processSelection() {
        OWLEntity entity = getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
        if (entity == null) {
            selectPanel(BLANK_PANEL);
        }
        else {
            entity.accept(new OWLEntityVisitor() {
                public void visit(OWLClass cls) {
                    selectPanel(CLASSES_PANEL);
                }


                public void visit(OWLObjectProperty property) {
                    selectPanel(OBJECT_PROPERTIES_PANEL);
                }


                public void visit(OWLDataProperty property) {
                    selectPanel(DATA_PROPERTIES_PANEL);
                }


                public void visit(OWLAnnotationProperty property) {
                    selectPanel(ANNOTATION_PROPERTIES_PANEL);
                }


                public void visit(OWLNamedIndividual individual) {
                    selectPanel(INDIVIDUALS_PANEL);
                }


                public void visit(OWLDatatype dataType) {
                    selectPanel(DATATYPES_PANEL);
                }
            });
        }
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
}
