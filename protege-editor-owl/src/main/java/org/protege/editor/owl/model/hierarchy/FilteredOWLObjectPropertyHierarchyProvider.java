package org.protege.editor.owl.model.hierarchy;

import java.util.HashSet;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

public class FilteredOWLObjectPropertyHierarchyProvider extends OWLObjectPropertyHierarchyProvider implements OWLSelectionModelListener {
	
	private OWLEditorKit okit = null;
	private OWLClass selectedClass = null;

	public FilteredOWLObjectPropertyHierarchyProvider(OWLOntologyManager owlOntologyManager, OWLEditorKit k) {
		super(owlOntologyManager);
		okit = k;
		okit.getWorkspace().getOWLSelectionModel().addListener(this);
	}
	
	public Set<? extends OWLObjectProperty> getReferencedProperties(OWLOntology ont) {
		Set<OWLObjectProperty> props = ont.getObjectPropertiesInSignature();
		Set<OWLObjectProperty> result = new HashSet<OWLObjectProperty>();
		for (OWLObjectProperty p : props) {
			if (isSubClassOfPropDomain(ont, p, selectedClass)) {
				//System.out.println(p +" works for " + selectedClass);
				result.add(p);
			}			
		}
		return result;
    }

	@Override
	public void selectionChanged() throws Exception {
		selectedClass = okit.getWorkspace().getOWLSelectionModel().getLastSelectedClass();
		rebuildRoots();
		fireNodeChanged(getRoot());
	}
	
	public boolean isSubClassOfPropDomain(OWLOntology ont, OWLObjectProperty p, OWLClass cls) {
		if (cls == null) {
			return true;
		}
		OWLClass dom = findDomain(ont, p);
		
		if (dom != null) {
			return isSubClass(ont, cls, dom);			
		} else {
			//System.out.println("Property has no domain: " + p);
		}
		
		return true;
	}
	
	private boolean isSubClass(OWLOntology ont, OWLClass sub, OWLClass sup) {
				
		CheckSubsumptionRelation checker = new CheckSubsumptionRelation(ont, sup);
		
		Set<OWLSubClassOfAxiom> subaxs = ont.getSubClassAxiomsForSubClass(sub);
		for (OWLSubClassOfAxiom s : subaxs) {
			s.getSuperClass().accept(checker);
			if (checker.isSub()) {
				return true;
			}
		}
		
		Set<OWLEquivalentClassesAxiom> eqs = ont.getEquivalentClassesAxioms(sub);
		for (OWLEquivalentClassesAxiom eq : eqs) {
			for (OWLClassExpression exp : eq.getClassExpressions()) {
				if (!exp.isAnonymous() && exp.asOWLClass().equals(sub)) {
				} else {
					exp.accept(checker);
					if (checker.isSub()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public OWLClass findDomain(OWLOntology ont, OWLObjectProperty p) {
		Set<OWLObjectPropertyDomainAxiom> doms = ont.getObjectPropertyDomainAxioms(p);
		if (doms.isEmpty()) {
			Set<OWLSubObjectPropertyOfAxiom> subs = ont.getObjectSubPropertyAxiomsForSubProperty(p);
			for (OWLSubObjectPropertyOfAxiom ax : subs) {
				OWLClass ocls = findDomain(ont, ax.getSuperProperty().asOWLObjectProperty());
				if (ocls != null) {
					return ocls;
				}
			}
			return null;
		}
		for (OWLObjectPropertyDomainAxiom dax : doms) {			
			return dax.getDomain().asOWLClass();
			
		}
		return null;		
	}
	
	private class CheckSubsumptionRelation implements OWLClassExpressionVisitor {
		
		private boolean isSub = false;
		
		public boolean isSub() { return isSub; }
		
		private OWLClass sup = null;
		
		private OWLOntology ont = null;
		
		public CheckSubsumptionRelation(OWLOntology o, OWLClass c) {
			sup = c;
			ont = o;
		}

		@Override
		public void visit(OWLClass ce) {
			if (ce.equals(sup)) {
				isSub = true;
			} else {
				isSub = isSubClass(ont, ce, sup);
			}
		}

		@Override
		public void visit(OWLObjectIntersectionOf ce) {
			Set<OWLClassExpression> cjs = ce.asConjunctSet();
			for (OWLClassExpression exp : cjs) {
				exp.accept(this);
			}
		}

		@Override
		public void visit(OWLObjectUnionOf ce) {
			Set<OWLClassExpression> cjs = ce.asDisjunctSet();
			for (OWLClassExpression exp : cjs) {
				exp.accept(this);
			}
		}

		@Override
		public void visit(OWLObjectComplementOf ce) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLObjectSomeValuesFrom ce) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLObjectAllValuesFrom ce) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLObjectHasValue ce) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLObjectMinCardinality ce) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLObjectExactCardinality ce) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLObjectMaxCardinality ce) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLObjectHasSelf ce) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLObjectOneOf ce) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDataSomeValuesFrom ce) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDataAllValuesFrom ce) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDataHasValue ce) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDataMinCardinality ce) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDataExactCardinality ce) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDataMaxCardinality ce) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
