package org.protege.editor.owl.ui.view.objectproperty;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;

import org.protege.editor.owl.model.axiom.FreshAxiomLocationPreferences;
import org.protege.editor.owl.model.axiom.FreshAxiomLocationStrategy;
import org.protege.editor.owl.model.axiom.FreshAxiomLocationStrategyFactory;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.FilteringOWLOntologyChangeListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLObjectPropertyCharacteristicsViewComponent extends AbstractOWLObjectPropertyViewComponent {

//    private static final Logger logger = Logger.getLogger(OWLObjectPropertyCharacteristicsViewComponent.class);


    /**
     * 
     */
    private static final long serialVersionUID = -1299595056337566960L;

    private JCheckBox functionalCB;

    private JCheckBox inverseFunctionalCB;

    private JCheckBox transitiveCB;

    private JCheckBox symmetricCB;

    private JCheckBox aSymmetricCB;

    private JCheckBox reflexiveCB;

    private JCheckBox irreflexiveCB;

    private List<JCheckBox> checkBoxes;

    private OWLOntologyChangeListener listener;

    private OWLObjectProperty prop;


    public void initialiseView() throws Exception {
        functionalCB = new JCheckBox("Functional");
        inverseFunctionalCB = new JCheckBox("Inverse functional");
        transitiveCB = new JCheckBox("Transitive");
        symmetricCB = new JCheckBox("Symmetric");
        aSymmetricCB = new JCheckBox("Asymmetric");
        reflexiveCB = new JCheckBox("Reflexive");
        irreflexiveCB = new JCheckBox("Irreflexive");

        checkBoxes = new ArrayList<JCheckBox>();
        checkBoxes.add(functionalCB);
        checkBoxes.add(inverseFunctionalCB);
        checkBoxes.add(transitiveCB);
        checkBoxes.add(symmetricCB);
        checkBoxes.add(aSymmetricCB);
        checkBoxes.add(reflexiveCB);
        checkBoxes.add(irreflexiveCB);


        setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        box.setOpaque(false);
        box.add(functionalCB);
        box.add(Box.createVerticalStrut(7));
        box.add(inverseFunctionalCB);
        box.add(Box.createVerticalStrut(7));
        box.add(transitiveCB);
        box.add(Box.createVerticalStrut(7));
        box.add(symmetricCB);
        box.add(Box.createVerticalStrut(7));
        box.add(aSymmetricCB);
        box.add(Box.createVerticalStrut(7));
        box.add(reflexiveCB);
        box.add(Box.createVerticalStrut(7));
        box.add(irreflexiveCB);
        add(new JScrollPane(box));

        setupSetters();

        listener = new FilteringOWLOntologyChangeListener() {
            public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
                updateView(prop);
            }


            public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
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

        addSetter(aSymmetricCB, new PropertyCharacteristicSetter() {
            public OWLAxiom getAxiom() {
                return getOWLDataFactory().getOWLAsymmetricObjectPropertyAxiom(getProperty());
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
        checkBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (getProperty() == null) {
            		return;
            	}
                if (checkBox.isSelected()) {
                    FreshAxiomLocationPreferences preferences = FreshAxiomLocationPreferences.getPreferences();
                    FreshAxiomLocationStrategyFactory strategyFactory = preferences.getFreshAxiomLocation().getStrategyFactory();
                    FreshAxiomLocationStrategy strategy = strategyFactory.getStrategy(getOWLEditorKit());
                    OWLAxiom ax = setter.getAxiom();
                    OWLOntology ont = strategy.getFreshAxiomLocation(ax, getOWLModelManager());
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


    private void setCheckBoxesEnabled(boolean enable) {
        for (JCheckBox cb : checkBoxes) {
            cb.setEnabled(enable);
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
        setCheckBoxesEnabled(property != null);
        if (property == null) {
        	return null;
        }
        // We only require one axiom to specify that a property has a specific characteristic
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            if (!ont.getFunctionalObjectPropertyAxioms(property).isEmpty()) {
                functionalCB.setSelected(true);
                if (!getOWLModelManager().isMutable(ont)) {
                    functionalCB.setEnabled(false);
                }
            }
            if (!ont.getInverseFunctionalObjectPropertyAxioms(property).isEmpty()) {
                inverseFunctionalCB.setSelected(true);
                if (!getOWLModelManager().isMutable(ont)) {
                    inverseFunctionalCB.setEnabled(false);
                }
            }
            if (!ont.getTransitiveObjectPropertyAxioms(property).isEmpty()) {
                transitiveCB.setSelected(true);
                if (!getOWLModelManager().isMutable(ont)) {
                    transitiveCB.setEnabled(false);
                }
            }
            if (!ont.getSymmetricObjectPropertyAxioms(property).isEmpty()) {
                symmetricCB.setSelected(true);
                if (!getOWLModelManager().isMutable(ont)) {
                    symmetricCB.setEnabled(false);
                }
            }
            if (!ont.getAsymmetricObjectPropertyAxioms(property).isEmpty()) {
                aSymmetricCB.setSelected(true);
                if (!getOWLModelManager().isMutable(ont)) {
                    aSymmetricCB.setEnabled(false);
                }
            }
            if (!ont.getReflexiveObjectPropertyAxioms(property).isEmpty()) {
                reflexiveCB.setSelected(true);
                if (!getOWLModelManager().isMutable(ont)) {
                    reflexiveCB.setEnabled(false);
                }
            }
            if (!ont.getIrreflexiveObjectPropertyAxioms(property).isEmpty()) {
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
