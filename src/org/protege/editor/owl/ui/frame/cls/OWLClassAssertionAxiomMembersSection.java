package org.protege.editor.owl.ui.frame.cls;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ListSelectionModel;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLIndividualEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 27-Jan-2007<br>
 * <br>
 */
public class OWLClassAssertionAxiomMembersSection extends AbstractOWLClassAxiomFrameSection<OWLClassAssertionAxiom, OWLNamedIndividual> {

    public static final String LABEL = "Members";

    private Set<OWLNamedIndividual> added = new HashSet<OWLNamedIndividual>();


    public OWLClassAssertionAxiomMembersSection(OWLEditorKit editorKit, OWLFrame<? extends OWLClass> frame) {
		super(editorKit, LABEL, "Type assertion", frame);
	}


    protected void clear() {
		added.clear();
	}


    protected void addAxiom(OWLClassAssertionAxiom ax, OWLOntology ont) {
        addRow(new OWLClassAssertionAxiomMembersSectionRow(getOWLEditorKit(), this, ont, getRootObject(), ax));
        if (!ax.getIndividual().isAnonymous()){
            added.add(ax.getIndividual().asNamedIndividual());
        }
    }


    protected Set<OWLClassAssertionAxiom> getClassAxioms(OWLClassExpression descr, OWLOntology ont) {
        if (!descr.isAnonymous()){
            return ont.getClassAssertionAxioms(descr.asOWLClass());
        }
        else{
            Set<OWLClassAssertionAxiom> axioms = new HashSet<OWLClassAssertionAxiom>();
            for (OWLClassAssertionAxiom ax : ont.getAxioms(AxiomType.CLASS_ASSERTION)){
                if (ax.getClassExpression().equals(descr)){
                    axioms.add(ax);
                }
            }
            return axioms;
        }
    }


    protected void refillInferred() {
        final OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
        NodeSet<OWLNamedIndividual> instances = getOWLModelManager().getReasoner().getInstances(getRootObject(), false);
        if (instances != null) {
        	for (OWLIndividual ind : instances.getFlattened()) {
        		if (!ind.isAnonymous() && !added.contains(ind.asNamedIndividual())) {
        			addRow(new OWLClassAssertionAxiomMembersSectionRow(getOWLEditorKit(),
        					this,
        					null,
        					getRootObject(),
        					df.getOWLClassAssertionAxiom(getRootObject(), ind)));
        			added.add(ind.asNamedIndividual());
        		}
        	}
        }
    }


    protected OWLClassAssertionAxiom createAxiom(OWLNamedIndividual individual) {
		return getOWLDataFactory().getOWLClassAssertionAxiom(getRootObject(), individual);
	}


    public OWLObjectEditor<OWLNamedIndividual> getObjectEditor() {
		return new OWLIndividualEditor(getOWLEditorKit(), ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}


    public boolean canAcceptDrop(List<OWLObject> objects) {
		for (OWLObject obj : objects) {
			if (!(obj instanceof OWLIndividual)) {
				return false;
			}
		}
		return true;
	}


	public boolean dropObjects(List<OWLObject> objects) {
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		for (OWLObject obj : objects) {
			if (obj instanceof OWLIndividual) {
				OWLIndividual ind = (OWLIndividual) obj;
				OWLAxiom ax = getOWLDataFactory().getOWLClassAssertionAxiom(getRootObject(), ind);
				changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
			}
		}
		getOWLModelManager().applyChanges(changes);
		return true;
	}

    
    /**
	 * Obtains a comparator which can be used to sort the rows in this section.
	 * 
	 * @return A comparator if to sort the rows in this section, or
	 *         <code>null</code> if the rows shouldn't be sorted.
	 */
	public Comparator<OWLFrameSectionRow<OWLClassExpression, OWLClassAssertionAxiom, OWLNamedIndividual>> getRowComparator() {
		return new Comparator<OWLFrameSectionRow<OWLClassExpression, OWLClassAssertionAxiom, OWLNamedIndividual>>() {
			public int compare(OWLFrameSectionRow<OWLClassExpression, OWLClassAssertionAxiom, OWLNamedIndividual> o1,
					           OWLFrameSectionRow<OWLClassExpression, OWLClassAssertionAxiom, OWLNamedIndividual> o2) {
                final String s1 = getOWLModelManager().getRendering(o1.getAxiom().getIndividual());
                final String s2 = getOWLModelManager().getRendering(o2.getAxiom().getIndividual());
                return s1.compareToIgnoreCase(s2);
			}
		};
	}


    public void visit(OWLClassAssertionAxiom axiom) {
		if (axiom.getClassExpression().equals(this.getRootObject())) {
			this.reset();
		}
	}
}
