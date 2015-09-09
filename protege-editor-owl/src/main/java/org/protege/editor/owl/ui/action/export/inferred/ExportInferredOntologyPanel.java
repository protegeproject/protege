package org.protege.editor.owl.ui.action.export.inferred;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;

import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDataPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 09-Aug-2007<br><br>
 */
public class ExportInferredOntologyPanel extends JPanel {
	private static final long serialVersionUID = 2756557147241841849L;

	private Map<JCheckBox, InferredAxiomGenerator<? extends OWLAxiom>> map;

    private Box checkBoxBox;


    public static <A extends OWLAxiom> Set<InferenceType> getInferenceType(InferredAxiomGenerator<A> generator) {
    	if (generator instanceof MonitoredInferredAxiomGenerator) {
    		generator = ((MonitoredInferredAxiomGenerator<A>) generator).getDelegate();
    	}
        if (generator instanceof InferredSubClassAxiomGenerator
            || generator instanceof InferredEquivalentClassAxiomGenerator) {
            return EnumSet.of(InferenceType.CLASS_HIERARCHY);
        }
        else if (generator instanceof InferredSubObjectPropertyAxiomGenerator
                 || generator instanceof InferredEquivalentObjectPropertyAxiomGenerator
                 || generator instanceof InferredInverseObjectPropertiesAxiomGenerator){
            return EnumSet.of(InferenceType.OBJECT_PROPERTY_HIERARCHY);
        }
        else if (generator instanceof InferredSubDataPropertyAxiomGenerator
                 || generator instanceof InferredEquivalentDataPropertiesAxiomGenerator) {
            return EnumSet.of(InferenceType.DATA_PROPERTY_HIERARCHY);
        }
        else if (generator instanceof InferredClassAssertionAxiomGenerator) {
            return EnumSet.of(InferenceType.CLASS_ASSERTIONS);
        }
        else if (generator instanceof InferredPropertyAssertionGenerator) {
            return EnumSet.of(InferenceType.OBJECT_PROPERTY_ASSERTIONS, InferenceType.DATA_PROPERTY_ASSERTIONS);
        }
        else if (generator instanceof InferredDisjointClassesAxiomGenerator) {
            return EnumSet.of(InferenceType.DISJOINT_CLASSES);
        }
        else return EnumSet.noneOf(InferenceType.class);
    }


    public ExportInferredOntologyPanel() {
        map = new HashMap<JCheckBox, InferredAxiomGenerator<? extends OWLAxiom>>();
        checkBoxBox = new Box(BoxLayout.Y_AXIS);
        setLayout(new BorderLayout(7, 7));
        add(checkBoxBox);
        addCheckBox(new InferredSubClassAxiomGenerator(), true, false);
        addCheckBox(new InferredEquivalentClassAxiomGenerator(), true, false);
        addCheckBox(new InferredSubObjectPropertyAxiomGenerator(), true, false);
        addCheckBox(new InferredSubDataPropertyAxiomGenerator(), true, false);
        addCheckBox(new InferredEquivalentObjectPropertyAxiomGenerator(), true, false);
        addCheckBox(new InferredEquivalentDataPropertiesAxiomGenerator(), true, false);
        addCheckBox(new InferredObjectPropertyCharacteristicAxiomGenerator(), false, false);
        addCheckBox(new InferredDataPropertyCharacteristicAxiomGenerator(), false, false);
        addCheckBox(new InferredInverseObjectPropertiesAxiomGenerator(), false, false);
        addCheckBox(new InferredClassAssertionAxiomGenerator(), false, false);
        addCheckBox(new InferredPropertyAssertionGenerator(), false, true);
        addCheckBox(new InferredDisjointClassesAxiomGenerator(), false, true);
    }


    private void addCheckBox(final InferredAxiomGenerator<? extends OWLAxiom> gen, boolean selected, boolean expensive) {
        String label = gen.getLabel();
        final JCheckBox box = new JCheckBox(label, selected);
        if(expensive) {
            box.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(box.isSelected()) {
                        JOptionPane.showMessageDialog(ExportInferredOntologyPanel.this, "<html><body>Warning: Exporting <b>" + gen.getLabel() + "</b> may take a long time.</body></html>",  "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });
        }
        checkBoxBox.add(box);
        checkBoxBox.add(Box.createVerticalStrut(4));
        map.put(box, gen);
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<InferredAxiomGenerator<? extends OWLAxiom>> getInferredAxiomGenerators() {
        List<InferredAxiomGenerator<? extends OWLAxiom>> result = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
        for (JCheckBox checkBox : map.keySet()) {
            if (checkBox.isSelected()) {
                result.add(new MonitoredInferredAxiomGenerator(map.get(checkBox)));
            }
        }
        return result;
    }
}
