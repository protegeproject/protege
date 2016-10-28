package org.protege.editor.owl.ui.editor;

import org.protege.editor.owl.model.hierarchy.AssertedClassSubHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.FilteredOWLObjectPropertyHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectPropertyHierarchyProvider;
import org.protege.editor.owl.ui.selector.AbstractHierarchySelectorPanel;
import org.protege.editor.owl.ui.selector.AbstractSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 23, 2008<br><br>
 */
public class OWLObjectRestrictionCreatorPanel extends AbstractRestrictionCreatorPanel<OWLObjectProperty, OWLClass> {

    private RestrictionCreator<OWLObjectProperty, OWLClass> some;
    private RestrictionCreator<OWLObjectProperty, OWLClass> only;

    private CardinalityRestrictionCreator<OWLObjectProperty, OWLClass> min;

    private CardinalityRestrictionCreator<OWLObjectProperty, OWLClass> exactly;

    private CardinalityRestrictionCreator<OWLObjectProperty, OWLClass> max;


    protected List<RestrictionCreator<OWLObjectProperty, OWLClass>> createTypes() {
        List<RestrictionCreator<OWLObjectProperty, OWLClass>> types = new ArrayList<>();

        types.add(some = new RestrictionCreator<OWLObjectProperty, OWLClass>("Some (existential)") {
            public void createRestrictions(Set<OWLObjectProperty> properties, Set<OWLClass> fillers,
                                           Set<OWLClassExpression> result) {
                for (OWLObjectProperty prop : properties) {
                    for (OWLClass filler : fillers) {
                        result.add(getDataFactory().getOWLObjectSomeValuesFrom(prop, filler));
                    }
                }
            }
        });
        types.add(only = new RestrictionCreator<OWLObjectProperty, OWLClass>("Only (universal)") {
            public void createRestrictions(Set<OWLObjectProperty> properties, Set<OWLClass> fillers,
                                           Set<OWLClassExpression> result) {
                for (OWLObjectProperty prop : properties) {
                    if (fillers.isEmpty()) {
                        return;
                    }
                    OWLClassExpression filler;
                    if (fillers.size() > 1) {
                        filler = getDataFactory().getOWLObjectUnionOf(fillers);
                    }
                    else {
                        filler = fillers.iterator().next();
                    }
                    result.add(getDataFactory().getOWLObjectAllValuesFrom(prop, filler));
                }
            }
        });
        types.add(min = new CardinalityRestrictionCreator<OWLObjectProperty, OWLClass>("Min (min cardinality)") {
            public OWLClassExpression createRestriction(OWLObjectProperty prop, OWLClass filler, int card) {
                return getDataFactory().getOWLObjectMinCardinality(card, prop, filler);
            }
        });
        types.add(exactly = new CardinalityRestrictionCreator<OWLObjectProperty, OWLClass>("Exactly (exact cardinality)") {
            public OWLClassExpression createRestriction(OWLObjectProperty prop, OWLClass filler, int card) {
                return getDataFactory().getOWLObjectExactCardinality(card, prop, filler);
            }
        });
        types.add(max = new CardinalityRestrictionCreator<OWLObjectProperty, OWLClass>("Max (max cardinality)") {
            public OWLClassExpression createRestriction(OWLObjectProperty prop, OWLClass filler, int card) {
                return getDataFactory().getOWLObjectMaxCardinality(card, prop, filler);
            }
        });

        return types;
    }


    protected AbstractSelectorPanel<OWLClass> createFillerSelectorPanel() {
    	AssertedClassSubHierarchyProvider ac = 
    			new AssertedClassSubHierarchyProvider(getOWLEditorKit().getOWLModelManager().getOWLOntologyManager());
    	ac.setOntologies(getOWLEditorKit().getModelManager().getActiveOntologies());
        return new OWLClassSelectorPanel(getOWLEditorKit(), false, ac);
    }


    protected AbstractHierarchySelectorPanel<OWLObjectProperty> createPropertySelectorPanel() {
    	
    	OWLObjectPropertyHierarchyProvider op = new FilteredOWLObjectPropertyHierarchyProvider(getOWLEditorKit()
        		.getModelManager().getOWLOntologyManager(), getOWLEditorKit());
    	op.setOntologies(getOWLEditorKit().getModelManager().getActiveOntologies());
    	//getOWLEditorKit().getModelManager().getOWLHierarchyManager().setOWLObjectPropertyHierarchyProvider(op);
        return new OWLObjectPropertySelectorPanel(getOWLEditorKit(), false, op);
    }
    
    protected void propChosen() {
    	OWLOntology ont = getOWLEditorKit().getOWLModelManager().getActiveOntology();
    	OWLObjectProperty p = (OWLObjectProperty) propertySelectorPanel.getSelectedObject();
    	OWLClassSelectorPanel ocsp = (OWLClassSelectorPanel) fillerSelectorPanel;
    	
    	OWLClass root = findRange(ont, p);
    	
    	if (root != null) {
    		ocsp.setTreeRoot(root);
    	} else {
    		ocsp.setTreeRoot(getOWLEditorKit().getOWLModelManager().getOWLDataFactory().getOWLThing());
    		System.out.println("The object property has no range: " + p);
    	}

    	
    }
    
    private OWLClass findRange(OWLOntology ont, OWLObjectProperty p) {
    	
    	Set<OWLObjectPropertyRangeAxiom> raxs = ont.getObjectPropertyRangeAxioms(p);
    	if (raxs.isEmpty()) {
    		Set<OWLSubObjectPropertyOfAxiom> subs = ont.getObjectSubPropertyAxiomsForSubProperty(p);
			for (OWLSubObjectPropertyOfAxiom ax : subs) {
				OWLClass ocls = findRange(ont, ax.getSuperProperty().asOWLObjectProperty());
				if (ocls != null) {
					return ocls;
				}
			}
    	} else {
    		for (OWLObjectPropertyRangeAxiom ra : raxs) {
    			if (!ra.getRange().isAnonymous()) {
    				return ra.getRange().asOWLClass();   			
    			}    		
    		}
    	}
    	return null;
    	
    }


    public boolean setDescription(OWLClassExpression description) {
        if (description == null){
            return true;
        }
        final AcceptableExpressionFilter filter = new AcceptableExpressionFilter();
        description.accept(filter);
        if (filter.isAcceptable){
            setProperty(filter.p);
            setFiller(filter.f);
            setType(filter.t);
            if (filter.cardinality >= 0){
            setCardinality(filter.cardinality);
            }
            return true;
        }
        return false;
    }

    class AcceptableExpressionFilter extends OWLClassExpressionVisitorAdapter {
        private boolean isAcceptable = false;
        private OWLObjectProperty p;
        private OWLClass f;
        private RestrictionCreator<OWLObjectProperty, OWLClass> t;
        private int cardinality = -1;

        private void handleRestriction(OWLQuantifiedRestriction<OWLClassExpression> r) {
            if (!r.getProperty().isAnonymous() && !r.getFiller().isAnonymous()){
                p = (OWLObjectProperty) r.getProperty();
                f = r.getFiller().asOWLClass();
                isAcceptable = true;
            }
        }

        public void visit(OWLObjectSomeValuesFrom r) {
            t = some;
            handleRestriction(r);
        }

        public void visit(OWLObjectAllValuesFrom r) {
            t = only;
            handleRestriction(r);
        }

        public void visit(OWLObjectMinCardinality r) {
            t = min;
            cardinality = r.getCardinality();
            handleRestriction(r);
        }

        public void visit(OWLObjectExactCardinality r) {
            t = exactly;
            cardinality = r.getCardinality();
            handleRestriction(r);
        }

        public void visit(OWLObjectMaxCardinality r) {
            t = max;
            cardinality = r.getCardinality();
            handleRestriction(r);
        }
    }

}
