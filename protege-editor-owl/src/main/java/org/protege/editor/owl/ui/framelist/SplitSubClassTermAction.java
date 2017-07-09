package org.protege.editor.owl.ui.framelist;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.cls.OWLEquivalentClassesAxiomFrameSection;
import org.protege.editor.owl.ui.frame.cls.OWLSubClassAxiomFrameSection;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Dec-2007<br><br>
 */
public class SplitSubClassTermAction<R> extends OWLFrameListPopupMenuAction<R> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8169858029492752493L;


	protected String getName() {
        return "Split up the selected conjuncts or disjuncts";
    }


    protected void initialise() throws Exception {

    }


    protected void dispose() throws Exception {
    }


    protected void updateState() {
    	for (OWLFrameSectionRow<?, ?, ?> row : getSelectedRows()) {
    		OWLFrameSection fs = row.getFrameSection();
    		if (getSelectedRows().size() >= 1) {
    			if (fs instanceof OWLSubClassAxiomFrameSection) {
    				setEnabled(true);
    				return;
    			}
    		}
    	}
    	setEnabled(false);
    }


    public void actionPerformed(ActionEvent e) {
    	OWLClass cls = null;
    	HashSet<OWLClassExpression> cons = new HashSet<OWLClassExpression>();
    	List<OWLOntologyChange> changes = new ArrayList<>();
    	OWLOntology ont = getOWLModelManager().getActiveOntology();
    	OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
    	
    	
    	for (OWLFrameSectionRow<?, ?, ?> row : getSelectedRows()) {
    		OWLAxiom ax = row.getAxiom();
    		if (ax instanceof OWLSubClassOfAxiom) {
    			OWLSubClassOfAxiom sub_ax = (OWLSubClassOfAxiom) ax;
    			TermSplitter splitter = new TermSplitter();
                sub_ax.getSuperClass().accept(splitter);
                if (splitter.result.size() > 1) {
                    changes.add(new RemoveAxiom(ont, ax));
                    for (OWLClassExpression desc : splitter.result) {
                        assert desc != null;
                        OWLAxiom replAx = df.getOWLSubClassOfAxiom(sub_ax.getSubClass(), desc);
                        changes.add(new AddAxiom(ont, replAx));
                    }
                }
    			
    		}
    		getOWLModelManager().applyChanges(changes);


    		System.out.println("Add this row to the conjunction " + ax.toString());

    	}
    	
    	
        
    }
    
    /** The Class ConjunctSplitter. */
    private static class TermSplitter implements OWLClassExpressionVisitor {

        /** The result. */
        final Set<OWLClassExpression> result = new HashSet<>();

        /** Instantiates a new conjunct splitter. */
        TermSplitter() {}

        @Override
        public void visit(OWLClass ce) {
            result.add(ce);
        }

        @Override
        public void visit(OWLDataAllValuesFrom ce) {
            result.add(ce);
        }

        @Override
        public void visit(OWLDataExactCardinality ce) {
            result.add(ce);
        }

        @Override
        public void visit(OWLDataMaxCardinality ce) {
            result.add(ce);
        }

        @Override
        public void visit(OWLDataMinCardinality ce) {
            result.add(ce);
        }

        @Override
        public void visit(OWLDataSomeValuesFrom ce) {
            result.add(ce);
        }

        @Override
        public void visit(OWLDataHasValue ce) {
            result.add(ce);
        }

        @Override
        public void visit(OWLObjectAllValuesFrom ce) {
            result.add(ce);
        }

        @Override
        public void visit(OWLObjectComplementOf ce) {
            result.add(ce);
        }

        @Override
        public void visit(OWLObjectExactCardinality ce) {
            result.add(ce);
        }

        @Override
        public void visit(OWLObjectIntersectionOf ce) {
            for (OWLClassExpression op : ce.getOperands()) {
                op.accept(this);
            }
        }

        @Override
        public void visit(OWLObjectMaxCardinality ce) {
            result.add(ce);
        }

        @Override
        public void visit(OWLObjectMinCardinality ce) {
            result.add(ce);
        }

        @Override
        public void visit(OWLObjectOneOf ce) {
            result.add(ce);
        }

        @Override
        public void visit(OWLObjectHasSelf ce) {
            result.add(ce);
        }

        @Override
        public void visit(OWLObjectSomeValuesFrom ce) {
            result.add(ce);
        }

        @Override
        public void visit(OWLObjectUnionOf ce) {
        	for (OWLClassExpression op : ce.getOperands()) {
                op.accept(this);
            }
        }

        @Override
        public void visit(OWLObjectHasValue ce) {
            result.add(ce);
        }
    }
}
