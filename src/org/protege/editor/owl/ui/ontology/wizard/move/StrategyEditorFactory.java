package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.AssertedClassHierarchyProvider2;
import org.protege.editor.owl.model.hierarchy.OWLDataPropertyHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectPropertyHierarchyProvider;
import org.protege.editor.owl.model.selection.axioms.AnnotationAxiomsStrategy;
import org.protege.editor.owl.model.selection.axioms.AxiomSelectionStrategy;
import org.protege.editor.owl.model.selection.axioms.AxiomTypeStrategy;
import org.protege.editor.owl.model.selection.axioms.EntityReferencingAxiomsStrategy;
import org.protege.editor.owl.ui.frame.AnnotationURIList;
import org.protege.editor.owl.ui.selector.*;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;/*
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 30, 2008<br><br>
 */
public class StrategyEditorFactory {

    private OWLEditorKit eKit;

    private Map<AxiomSelectionStrategy, StrategyEditor> editorMap = new HashMap<AxiomSelectionStrategy, StrategyEditor>();


    public StrategyEditorFactory(OWLEditorKit eKit) {
        this.eKit = eKit;
    }

    public StrategyEditor getEditor(AxiomSelectionStrategy strategy, boolean fresh){
        if (fresh){
            editorMap.remove(strategy);
        }
        StrategyEditor editor = editorMap.get(strategy);
        if (editor == null){
            if (strategy instanceof EntityReferencingAxiomsStrategy){
                editor = getEntitySelector((EntityReferencingAxiomsStrategy)strategy);
            }
            else if (strategy instanceof AnnotationAxiomsStrategy){
                editor = getAnnotationSelector((AnnotationAxiomsStrategy)strategy);
            }
            else if (strategy instanceof AxiomTypeStrategy){
                editor = getTypeSelector((AxiomTypeStrategy)strategy);
            }
        }
        if (editor != null){
            editorMap.put(strategy, editor);

//            // if the selected ontologies change, dump this editor from cache so next time we can create a new one.
//            strategy.addPropertyChangeListener(new PropertyChangeListener(){
//                public void propertyChange(PropertyChangeEvent event) {
//                    if (event.getPropertyName().equals(AbstractAxiomSelectionStrategy.ONTOLOGIES_CHANGED)){
//                        editorMap.remove((AxiomSelectionStrategy)event.getSource());
//                    }
//                }
//            });
        }
        return editor;
    }

    private <O extends EntityReferencingAxiomsStrategy> StrategyEditor<O> getEntitySelector(O strategy) {
        return new StrategyEditor<O>(strategy, eKit){
            private AbstractSelectorPanel<? extends OWLEntity> selector;
            public JComponent getComponent() {
                if (selector == null){
                    Class type = getStrategy().getType();
                    final OWLOntologyManager mngr = eKit.getModelManager().getOWLOntologyManager();
                    if (type.equals(OWLClass.class)){
                        OWLObjectHierarchyProvider<OWLClass> hp = new AssertedClassHierarchyProvider2(mngr);
                        hp.setOntologies(getStrategy().getOntologies());
                        selector = new OWLClassSelectorPanel(eKit, false, hp);
                    }
                    else if (type.equals(OWLObjectProperty.class)){
                        OWLObjectHierarchyProvider<OWLObjectProperty> hp = new OWLObjectPropertyHierarchyProvider(mngr);
                        hp.setOntologies(getStrategy().getOntologies());
                        selector = new OWLObjectPropertySelectorPanel(eKit, false, hp);
                    }
                    else if (type.equals(OWLDataProperty.class)){
                        OWLObjectHierarchyProvider<OWLDataProperty> hp = new OWLDataPropertyHierarchyProvider(mngr);
                        hp.setOntologies(getStrategy().getOntologies());
                        selector = new OWLDataPropertySelectorPanel(eKit, false, hp);
                    }
                    else if (type.equals(OWLIndividual.class)){
                        selector = new OWLIndividualSelectorPanel(eKit, false, getStrategy().getOntologies(),
                                                                  ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    }

                    if (selector != null){
                        selector.setBorder(new EmptyBorder(0, 0, 0, 0));
                        selector.setOpaque(false);
                        selector.addSelectionListener(new ChangeListener(){

                            public void stateChanged(ChangeEvent event) {
                                getStrategy().setEntities(selector.getSelectedObjects());
                            }
                        });
                    }
                }
                return selector;
            }
        };
    }


    private StrategyEditor<AnnotationAxiomsStrategy> getAnnotationSelector(AnnotationAxiomsStrategy strategy) {
        return new StrategyEditor<AnnotationAxiomsStrategy>(strategy, eKit){
            private AnnotationURIList annotationURISelector;
            public JScrollPane scroller;

            public JComponent getComponent() {
                if (annotationURISelector == null){
                    annotationURISelector = new AnnotationURIList(getOWLEditorKit());
                    annotationURISelector.rebuildAnnotationURIList();
                    annotationURISelector.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    annotationURISelector.addListSelectionListener(new ListSelectionListener(){
                        public void valueChanged(ListSelectionEvent event) {
                            getStrategy().setURIs(annotationURISelector.getSelectedURIs());
                        }
                    });
                    scroller = new JScrollPane(annotationURISelector);
                }
                return scroller;
            }
        };
    }


    private StrategyEditor<AxiomTypeStrategy> getTypeSelector(AxiomTypeStrategy strategy) {
        return new StrategyEditor<AxiomTypeStrategy>(strategy, eKit){
            private JList axiomTypeSelector;
            public JScrollPane scroller;

            public JComponent getComponent() {
                if (axiomTypeSelector != null){
                    axiomTypeSelector = new JList(AxiomType.AXIOM_TYPES.toArray());
                    axiomTypeSelector.addListSelectionListener(new ListSelectionListener(){
                        public void valueChanged(ListSelectionEvent event) {
                            Set<AxiomType<? extends OWLAxiom>> selectedTypes = new HashSet<AxiomType<? extends OWLAxiom>>();
                            for (Object o : axiomTypeSelector.getSelectedValues()){
                                selectedTypes.add((AxiomType)o);
                            }
                            getStrategy().setTypes(selectedTypes);
                        }
                    });
                    scroller = new JScrollPane(axiomTypeSelector);
                }
                return scroller;
            }
        };
    }

}
