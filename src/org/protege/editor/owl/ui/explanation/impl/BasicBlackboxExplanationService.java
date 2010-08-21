package org.protege.editor.owl.ui.explanation.impl;

import java.awt.Component;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import org.protege.editor.owl.ui.explanation.ExplanationService;
import org.semanticweb.owlapi.debugging.DebuggerClassExpressionGenerator;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;

public class BasicBlackboxExplanationService extends ExplanationService {
	private DebuggerClassExpressionGenerator classExpressionVisitor;
	
	public void initialise() throws Exception {
		classExpressionVisitor = new DebuggerClassExpressionGenerator(getOWLModelManager().getOWLDataFactory());
	}

	public void dispose() throws Exception {
	
	}

	public boolean hasExplanation(OWLAxiom axiom) {
		OWLOntology activeOntology = getOWLModelManager().getActiveOntology();
		if (activeOntology.containsAxiom(axiom, true)) {
			return false;
		}
		return getClassExpression(axiom) != null;
	}

	public JComponent explain(OWLAxiom axiom) {
		OWLOntology activeOntology = getOWLModelManager().getActiveOntology();
		OWLReasonerFactory rFactory = getOWLModelManager().getOWLReasonerManager().getCurrentReasonerFactory().getReasonerFactory();
		OWLReasoner reasoner = getOWLModelManager().getOWLReasonerManager().getCurrentReasoner();
		BlackBoxExplanation explain  = new BlackBoxExplanation(activeOntology, rFactory, reasoner);
		Set<OWLAxiom> axioms = explain.getExplanation(getClassExpression(axiom));
		final JList list = new JList(axioms.toArray(new OWLAxiom[0]));
		list.setCellRenderer(new ListCellRenderer() {
			private JLabel rendering = new JLabel();
			public Component getListCellRendererComponent(JList list,
													      Object value, int index, boolean isSelected,
													      boolean cellHasFocus) {
				OWLAxiom axiom = (OWLAxiom) value;
				rendering.setText(getOWLModelManager().getRendering(axiom));
		        if (isSelected) {
		            rendering.setBackground(list.getSelectionBackground());
		            rendering.setForeground(list.getSelectionForeground());
		        } else {
		            rendering.setBackground(list.getBackground());
		            rendering.setForeground(list.getForeground());
		        }
				return rendering;
			}
		});
		return new JScrollPane(list);
	}
	
	private OWLClassExpression getClassExpression(OWLAxiom axiom) {
		axiom.accept(classExpressionVisitor);
		return classExpressionVisitor.getDebuggerClassExpression();
	}

}
