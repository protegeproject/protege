package org.protege.editor.owl.ui.view;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.FilteringOWLOntologyChangeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * Date: 29-Jan-2007<br><br>
 */
public class OWLObjectPropertyCharacteristicsViewComponent extends AbstractOWLObjectPropertyViewComponent {

    private static final Logger logger = Logger.getLogger(OWLObjectPropertyCharacteristicsViewComponent.class);


    private JCheckBox functionalCB;

    private JCheckBox inverseFunctionalCB;

    private JCheckBox transitiveCB;

    private JCheckBox symmetricCB;

    private JCheckBox antiSymmetricCB;

    private JCheckBox reflexiveCB;

    private JCheckBox irreflexiveCB;

    private List<JCheckBox> checkBoxes;

    private OWLOntologyChangeListener listener;

    private Map<JCheckBox, PropertyCharacteristicSetter> map;

    private OWLObjectProperty prop;


    public void initialiseView() throws Exception {
        functionalCB = new JCheckBox("Functional");
        inverseFunctionalCB = new JCheckBox("Inverse functional");
        transitiveCB = new JCheckBox("Transitive");
        symmetricCB = new JCheckBox("Symmetric");
        antiSymmetricCB = new JCheckBox("Antisymmetric");
        reflexiveCB = new JCheckBox("Reflexive");
        irreflexiveCB = new JCheckBox("Irreflexive");

        checkBoxes = new ArrayList<JCheckBox>();
        checkBoxes.add(functionalCB);
        checkBoxes.add(inverseFunctionalCB);
        checkBoxes.add(transitiveCB);
        checkBoxes.add(symmetricCB);
        checkBoxes.add(antiSymmetricCB);
        checkBoxes.add(reflexiveCB);
        checkBoxes.add(irreflexiveCB);


        setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        box.add(functionalCB);
        box.add(Box.createVerticalStrut(7));
        box.add(inverseFunctionalCB);
        box.add(Box.createVerticalStrut(7));
        box.add(transitiveCB);
        box.add(Box.createVerticalStrut(7));
        box.add(symmetricCB);
        box.add(Box.createVerticalStrut(7));
        box.add(antiSymmetricCB);
        box.add(Box.createVerticalStrut(7));
        box.add(reflexiveCB);
        box.add(Box.createVerticalStrut(7));
        box.add(irreflexiveCB);
        add(box);

        map = new HashMap<JCheckBox, PropertyCharacteristicSetter>();
        setupSetters();

        listener = new FilteringOWLOntologyChangeListener() {
            public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
                updateView(prop);
            }


            public void visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
                updateView(prop);
            }


            public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
                updateView(prop);
            }


            public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
                updateView(prop);
            }


            public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
                updateView(prop);
            }


            public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
                updateView(prop);
            }


            public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
                updateView(prop);
            }
        };
        getOWLModelManager().addOntologyChangeListener(listener);
    }


    private void setupSetters() {
        addSetter(functionalCB, new PropertyCharacteristicSetter() {
            public OWLAxiom getAxiom() {
                return getOWLDataFactory().getOWLFunctionalObjectPropertyAxiom(getProperty());
            }
        });

        addSetter(inverseFunctionalCB, new PropertyCharacteristicSetter() {
            public OWLAxiom getAxiom() {
                return getOWLDataFactory().getOWLInverseFunctionalObjectPropertyAxiom(getProperty());
            }
        });

        addSetter(transitiveCB, new PropertyCharacteristicSetter() {
            public OWLAxiom getAxiom() {
                return getOWLDataFactory().getOWLTransitiveObjectPropertyAxiom(getProperty());
            }
        });

        addSetter(symmetricCB, new PropertyCharacteristicSetter() {
            public OWLAxiom getAxiom() {
                return getOWLDataFactory().getOWLSymmetricObjectPropertyAxiom(getProperty());
            }
        });

        addSetter(antiSymmetricCB, new PropertyCharacteristicSetter() {
            public OWLAxiom getAxiom() {
                return getOWLDataFactory().getOWLAntiSymmetricObjectPropertyAxiom(getProperty());
            }
        });

        addSetter(reflexiveCB, new PropertyCharacteristicSetter() {
            public OWLAxiom getAxiom() {
                return getOWLDataFactory().getOWLReflexiveObjectPropertyAxiom(getProperty());
            }
        });

        addSetter(irreflexiveCB, new PropertyCharacteristicSetter() {
            public OWLAxiom getAxiom() {
                return getOWLDataFactory().getOWLIrreflexiveObjectPropertyAxiom(getProperty());
            }
        });
    }


    private void addSetter(final JCheckBox checkBox, final PropertyCharacteristicSetter setter) {
        map.put(checkBox, setter);
        checkBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (checkBox.isSelected()) {
                    OWLOntology ont = getOWLModelManager().getActiveOntology();
                    OWLAxiom ax = setter.getAxiom();
                    getOWLModelManager().applyChange(new AddAxiom(ont, ax));
                }
                else {
                    List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
                    OWLAxiom ax = setter.getAxiom();
                    for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
                        changes.add(new RemoveAxiom(ont, ax));
                    }
                    getOWLModelManager().applyChanges(changes);
                }
            }
        });
    }


    private OWLObjectProperty getProperty() {
        return prop;
    }


    public void disposeView() {
        getOWLModelManager().removeOntologyChangeListener(listener);
    }


    private void enableAll() {
        for (JCheckBox cb : checkBoxes) {
            cb.setEnabled(true);
        }
    }


    private void clearAll() {
        for (JCheckBox cb : checkBoxes) {
            cb.setSelected(false);
        }
    }


    protected OWLObjectProperty updateView(OWLObjectProperty property) {
        prop = property;
        clearAll();
        enableAll();
        // We only require one axiom to specify that a property has a specific characteristic
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            if (ont.getFunctionalObjectPropertyAxiom(property) != null) {
                functionalCB.setSelected(true);
                if (!getOWLModelManager().isMutable(ont)) {
                    functionalCB.setEnabled(false);
                }
            }
            if (ont.getInverseFunctionalObjectPropertyAxiom(property) != null) {
                inverseFunctionalCB.setSelected(true);
                if (!getOWLModelManager().isMutable(ont)) {
                    inverseFunctionalCB.setEnabled(false);
                }
            }
            if (ont.getTransitiveObjectPropertyAxiom(property) != null) {
                transitiveCB.setSelected(true);
                if (!getOWLModelManager().isMutable(ont)) {
                    transitiveCB.setEnabled(false);
                }
            }
            if (ont.getSymmetricObjectPropertyAxiom(property) != null) {
                symmetricCB.setSelected(true);
                if (!getOWLModelManager().isMutable(ont)) {
                    symmetricCB.setEnabled(false);
                }
            }
            if (ont.getAntiSymmetricObjectPropertyAxiom(property) != null) {
                antiSymmetricCB.setSelected(true);
                if (!getOWLModelManager().isMutable(ont)) {
                    antiSymmetricCB.setEnabled(false);
                }
            }
            if (ont.getReflexiveObjectPropertyAxiom(property) != null) {
                reflexiveCB.setSelected(true);
                if (!getOWLModelManager().isMutable(ont)) {
                    reflexiveCB.setEnabled(false);
                }
            }
            if (ont.getIrreflexiveObjectPropertyAxiom(property) != null) {
                irreflexiveCB.setSelected(true);
                if (!getOWLModelManager().isMutable(ont)) {
                    irreflexiveCB.setEnabled(false);
                }
            }
        }

        return property;
    }


    private interface PropertyCharacteristicSetter {

        public OWLAxiom getAxiom();
    }
}
