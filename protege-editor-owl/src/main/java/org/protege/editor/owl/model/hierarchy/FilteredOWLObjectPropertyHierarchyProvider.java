package org.protege.editor.owl.model.hierarchy;

import java.util.HashSet;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
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
		if (sub.equals(sup)) {
			return true;
		}
		
		Set<OWLSubClassOfAxiom> subaxs = ont.getSubClassAxiomsForSubClass(sub);
		for (OWLSubClassOfAxiom s : subaxs) {
			if (!s.getSuperClass().isAnonymous()) {
				if (s.getSuperClass().asOWLClass().equals(sup)) {
					return true;
				} else {
					if (sub.equals(s.getSuperClass().asOWLClass())) {
						return false;
					} else {
						return isSubClass(ont, s.getSuperClass().asOWLClass(), sup);
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

}
