package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.cls.AbstractOWLClassAxiomFrameSection;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import java.util.*;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 27-Jan-2007<br>
 * <br>
 */
public class OWLClassAssertionAxiomIndividualSection extends AbstractOWLClassAxiomFrameSection<OWLClassAssertionAxiom, OWLIndividual> {

    public static final String LABEL = "Members";
	private Set<OWLIndividual> added = new HashSet<OWLIndividual>();

	public OWLClassAssertionAxiomIndividualSection(OWLEditorKit editorKit,
			OWLFrame<? extends OWLClass> frame) {
		super(editorKit, LABEL, "Type assertion", frame);
	}

	@Override
	protected void clear() {
		this.added.clear();
	}


    protected void addAxiom(OWLClassAssertionAxiom ax, OWLOntology ont) {
        addRow(new OWLClassAssertionAxiomIndividualSectionRow(this.getOWLEditorKit(), this, ont, this.getRootObject(), ax));
        added.add(ax.getIndividual());
    }


    protected Set<OWLClassAssertionAxiom> getClassAxioms(OWLDescription descr, OWLOntology ont) {
        if (!descr.isAnonymous()){
            return ont.getClassAssertionAxioms(descr.asOWLClass());
        }
        else{
            Set<OWLClassAssertionAxiom> axioms = new HashSet<OWLClassAssertionAxiom>();
            for (OWLClassAssertionAxiom ax : ont.getAxioms(AxiomType.CLASS_ASSERTION)){
                if (ax.getDescription().equals(descr)){
                    axioms.add(ax);
                }
            }
            return axioms;
        }
    }


    @Override
	protected void refillInferred() {
		try {
			for (OWLIndividual ind : this.getOWLModelManager().getReasoner()
					.getIndividuals(this.getRootObject(), false)) {
				if (!this.added.contains(ind)) {
					this.addRow(new OWLClassAssertionAxiomIndividualSectionRow(
							this.getOWLEditorKit(), this, null, this
									.getRootObject(), this.getOWLModelManager()
									.getOWLDataFactory()
									.getOWLClassAssertionAxiom(ind,
											this.getRootObject())));
					this.added.add(ind);
				}
			}
		} catch (OWLReasonerException e) {
			throw new OWLRuntimeException(e);
		}
	}

	@Override
	protected OWLClassAssertionAxiom createAxiom(OWLIndividual object) {
		return this.getOWLDataFactory().getOWLClassAssertionAxiom(object,
				this.getRootObject());
	}

	@Override
	public OWLFrameSectionRowObjectEditor<OWLIndividual> getObjectEditor() {
		return new OWLIndividualEditor(this.getOWLEditorKit(),
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	@Override
	public boolean canAcceptDrop(List<OWLObject> objects) {
		for (OWLObject obj : objects) {
			if (!(obj instanceof OWLIndividual)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean dropObjects(List<OWLObject> objects) {
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		for (OWLObject obj : objects) {
			if (obj instanceof OWLIndividual) {
				OWLIndividual ind = (OWLIndividual) obj;
				OWLAxiom ax = this.getOWLDataFactory()
						.getOWLClassAssertionAxiom(ind, this.getRootObject());
				changes.add(new AddAxiom(this.getOWLModelManager()
						.getActiveOntology(), ax));
			}
		}
		this.getOWLModelManager().applyChanges(changes);
		return true;
	}

	/**
	 * Obtains a comparator which can be used to sort the rows in this section.
	 * 
	 * @return A comparator if to sort the rows in this section, or
	 *         <code>null</code> if the rows shouldn't be sorted.
	 */
	public Comparator<OWLFrameSectionRow<OWLDescription, OWLClassAssertionAxiom, OWLIndividual>> getRowComparator() {
		return new Comparator<OWLFrameSectionRow<OWLDescription, OWLClassAssertionAxiom, OWLIndividual>>() {
			public int compare(
					OWLFrameSectionRow<OWLDescription, OWLClassAssertionAxiom, OWLIndividual> o1,
					OWLFrameSectionRow<OWLDescription, OWLClassAssertionAxiom, OWLIndividual> o2) {
				return OWLClassAssertionAxiomIndividualSection.this
						.getOWLModelManager().getRendering(
								o1.getAxiom().getIndividual())
						.compareToIgnoreCase(
								OWLClassAssertionAxiomIndividualSection.this
										.getOWLModelManager().getRendering(
												o2.getAxiom().getIndividual()));
			}
		};
	}

	@Override
	public void visit(OWLClassAssertionAxiom axiom) {
		if (axiom.getDescription().equals(this.getRootObject())) {
			this.reset();
		}
	}
}
