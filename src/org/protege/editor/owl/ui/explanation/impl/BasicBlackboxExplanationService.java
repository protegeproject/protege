package org.protege.editor.owl.ui.explanation.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.protege.editor.owl.ui.explanation.ExplanationResult;
import org.protege.editor.owl.ui.explanation.ExplanationService;
import org.protege.editor.owl.ui.frame.AxiomListFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.debugging.DebuggerClassExpressionGenerator;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;

/**
 * A very basic explanation service that will provide basic explanation until Matthew's 
 * explanation service is ready.
 * 
 * @author tredmond
 *
 */
public class BasicBlackboxExplanationService extends ExplanationService {
	
	public void initialise() throws Exception {
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

	public ExplanationResult explain(OWLAxiom axiom) {
		OWLOntology activeOntology = getOWLModelManager().getActiveOntology();
		OWLReasonerFactory rFactory = getOWLModelManager().getOWLReasonerManager().getCurrentReasonerFactory().getReasonerFactory();
		OWLReasoner reasoner = getOWLModelManager().getOWLReasonerManager().getCurrentReasoner();
		BlackBoxExplanation explain  = new BlackBoxExplanation(activeOntology, rFactory, reasoner);
		Set<OWLAxiom> axioms = explain.getExplanation(getClassExpression(axiom));

        AxiomListFrame frame = new AxiomListFrame(getOWLEditorKit());
        frame.setRootObject(axioms);
        final OWLFrameList<Set<OWLAxiom>> frameList = new OWLFrameList<Set<OWLAxiom>>(getOWLEditorKit(), frame);
        frameList.setPreferredSize(new Dimension(800, 600));
        frameList.refreshComponent();
        ExplanationResult result = new ExplanationResult() {
			
			@Override
			public void dispose() {
				frameList.dispose();
			}
		};
		result.setLayout(new BorderLayout());
        result.add(new JScrollPane(frameList), BorderLayout.CENTER);
        return result;
	}
	
	private OWLClassExpression getClassExpression(OWLAxiom axiom) {
		/*
		 * there is no clear method...
		 */
		DebuggerClassExpressionGenerator classExpressionVisitor = new DebuggerClassExpressionGenerator(getOWLModelManager().getOWLDataFactory());
		axiom.accept(classExpressionVisitor);
		return classExpressionVisitor.getDebuggerClassExpression();
	}

}
