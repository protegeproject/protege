package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.net.URL;

import javax.swing.JPanel;

import org.protege.editor.core.ui.view.ViewsPane;
import org.protege.editor.core.ui.view.ViewsPaneMemento;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 02-Mar-2007<br><br>
 */
public class SelectedEntityCardView extends AbstractOWLViewComponent {

    public static final String ID = "org.protege.editor.owl.SelectedEntityView";

    private CardLayout cardLayout = new CardLayout();

    private JPanel cardPanel;

    private ViewsPane classesViewsPane;

    private ViewsPane objectPropertiesViewsPane;

    private ViewsPane dataPropertiesViewsPane;

    private ViewsPane individualViewsPane;


    private static final String CLASSES_PANEL = "Classes";

    private static final String BLANK_PANEL = "Blank";

    private static final String OBJECT_PROPERTIES_PANEL = "ObjectProperties";

    private static final String DATA_PROPERTIES_PANEL = "DataProperties";

    private static final String INDIVIDUALS_PANEL = "Individual";


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        cardPanel = new JPanel();
        add(cardPanel);
        cardPanel.setLayout(cardLayout);
        cardPanel.add(new JPanel(), BLANK_PANEL);
        URL clsURL = getClass().getResource("/selected-entity-view-class-panel.xml");
        classesViewsPane = new ViewsPane(getOWLWorkspace(),
                                         new ViewsPaneMemento(clsURL,
                                                              "org.protege.editor.owl.ui.view.selectedentityview.classes"));
        cardPanel.add(classesViewsPane, CLASSES_PANEL);
        URL objPropURL = getClass().getResource("/selected-entity-view-objectproperty-panel.xml");
        objectPropertiesViewsPane = new ViewsPane(getOWLWorkspace(),
                                                  new ViewsPaneMemento(objPropURL,
                                                                       "org.protege.editor.owl.ui.view.selectedentityview.objectproperties"));
        cardPanel.add(objectPropertiesViewsPane, OBJECT_PROPERTIES_PANEL);
        URL dataPropURL = getClass().getResource("/selected-entity-view-dataproperty-panel.xml");
        dataPropertiesViewsPane = new ViewsPane(getOWLWorkspace(),
                                                new ViewsPaneMemento(dataPropURL,
                                                                     "org.protege.editor.owl.ui.view.selectedentityview.dataproperties"));
        cardPanel.add(dataPropertiesViewsPane, DATA_PROPERTIES_PANEL);

        URL indURL = getClass().getResource("/selected-entity-view-individual-panel.xml");
        individualViewsPane = new ViewsPane(getOWLWorkspace(),
                                            new ViewsPaneMemento(indURL,
                                                                 "org.protege.editor.owl.ui.view.selectedentityview.individuals"));
        cardPanel.add(individualViewsPane, INDIVIDUALS_PANEL);
        getOWLWorkspace().getOWLSelectionModel().addListener(new OWLSelectionModelListener() {
            public void selectionChanged() throws Exception {
                processSelection();
            }
        });
//        getView().setShowViewBar(false);
        processSelection();
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


                public void visit(OWLIndividual individual) {
                    selectPanel(INDIVIDUALS_PANEL);
                }


                public void visit(OWLDataType dataType) {
                }
            });
        }
    }


    private void selectPanel(String name) {
        cardLayout.show(cardPanel, name);
    }


    protected void disposeOWLView() {
        classesViewsPane.saveViews();
        classesViewsPane.dispose();
        objectPropertiesViewsPane.saveViews();
        objectPropertiesViewsPane.dispose();
        dataPropertiesViewsPane.saveViews();
        dataPropertiesViewsPane.dispose();
        individualViewsPane.saveViews();
        individualViewsPane.dispose();
    }
}
