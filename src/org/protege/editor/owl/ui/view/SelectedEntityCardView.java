package org.protege.editor.owl.ui.view;

import org.protege.editor.core.ui.view.ViewsPane;
import org.protege.editor.core.ui.view.ViewsPaneMemento;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
