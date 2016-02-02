package org.protege.editor.owl.ui.frame.cls;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.editor.OWLIndividualEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;

import javax.swing.*;
import java.util.*;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 27-Jan-2007<br>
 * <br>
 */
public class OWLClassAssertionAxiomMembersSection extends AbstractOWLClassAxiomFrameSection<OWLClassAssertionAxiom, OWLNamedIndividual> {

    public static final String LABEL = "Instances";

    public static final boolean SHOW_DIRECT_INSTANCES = true;

    private Set<OWLNamedIndividual> added = new HashSet<>();


    public OWLClassAssertionAxiomMembersSection(OWLEditorKit editorKit, OWLFrame<? extends OWLClass> frame) {
		super(editorKit, LABEL, "Type assertion", frame);
	}


    protected void clear() {
		added.clear();
	}


    protected void addAxiom(OWLClassAssertionAxiom ax, OWLOntology ont) {
        addRow(new OWLClassAssertionAxiomMembersSectionRow(getOWLEditorKit(), this, ont, getRootObject(), ax));
        if (!ax.getIndividual().isAnonymous()){
            added.add(ax.getIndividual().asOWLNamedIndividual());
        }
    }


    protected Set<OWLClassAssertionAxiom> getClassAxioms(OWLClassExpression descr, OWLOntology ont) {
        if (!descr.isAnonymous()){
            return ont.getClassAssertionAxioms(descr.asOWLClass());
        }
        else{
            Set<OWLClassAssertionAxiom> axioms = new HashSet<>();
            for (OWLClassAssertionAxiom ax : ont.getAxioms(AxiomType.CLASS_ASSERTION)){
                if (ax.getClassExpression().equals(descr)){
                    axioms.add(ax);
                }
            }
            return axioms;
        }
    }


    protected void refillInferred() {
        getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERED_CLASS_MEMBERS, () -> {
            if (!getOWLModelManager().getReasoner().isConsistent()) {
                return;
            }
            final OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
            NodeSet<OWLNamedIndividual> instances = getOWLModelManager().getReasoner().getInstances(getRootObject(), SHOW_DIRECT_INSTANCES);
            if (instances != null) {
                for (OWLIndividual ind : instances.getFlattened()) {
                    if (!ind.isAnonymous() && !added.contains(ind.asOWLNamedIndividual())) {
                        addRow(new OWLClassAssertionAxiomMembersSectionRow(getOWLEditorKit(),
                                                                           OWLClassAssertionAxiomMembersSection.this,
                                                                           null,
                                                                           getRootObject(),
                                                                           df.getOWLClassAssertionAxiom(getRootObject(), ind)));
                        added.add(ind.asOWLNamedIndividual());
                    }
                }
            }
        });
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
		List<OWLOntologyChange> changes = new ArrayList<>();
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
		return (o1, o2) -> {
final String s1 = getOWLModelManager().getRendering(o1.getAxiom().getIndividual());
final String s2 = getOWLModelManager().getRendering(o2.getAxiom().getIndividual());
return s1.compareToIgnoreCase(s2);
        };
	}
	
	@Override
	protected boolean isResettingChange(OWLOntologyChange change) {
		return change.isAxiomChange() && 
				change.getAxiom() instanceof OWLClassAssertionAxiom &&
				((OWLClassAssertionAxiom) change.getAxiom()).getClassExpression().equals(getRootObject());
	}
}
