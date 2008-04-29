package org.protege.editor.owl.ui.frame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ListSelectionModel;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLRuntimeException;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 27-Jan-2007<br>
 * <br>
 */
public class OWLClassAssertionAxiomIndividualSection
		extends
		AbstractOWLFrameSection<OWLClass, OWLClassAssertionAxiom, OWLIndividual> {
	public static final String LABEL = "Individuals";
	private Set<OWLIndividual> added = new HashSet<OWLIndividual>();

	public OWLClassAssertionAxiomIndividualSection(OWLEditorKit editorKit,
			OWLFrame<? extends OWLClass> frame) {
		super(editorKit, LABEL, frame);
	}

	@Override
	protected void clear() {
		this.added.clear();
	}

	/**
	 * Refills the section with rows. This method will be called by the system
	 * and should be directly called.
	 */
	@Override
	protected void refill(OWLOntology ontology) {
		for (OWLClassAssertionAxiom ax : ontology.getClassAssertionAxioms(this
				.getRootObject())) {
			this.addRow(new OWLClassAssertionAxiomIndividualSectionRow(this
					.getOWLEditorKit(), this, ontology, this.getRootObject(),
					ax));
			this.added.add(ax.getIndividual());
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
	public Comparator<OWLFrameSectionRow<OWLClass, OWLClassAssertionAxiom, OWLIndividual>> getRowComparator() {
		return new Comparator<OWLFrameSectionRow<OWLClass, OWLClassAssertionAxiom, OWLIndividual>>() {
			public int compare(
					OWLFrameSectionRow<OWLClass, OWLClassAssertionAxiom, OWLIndividual> o1,
					OWLFrameSectionRow<OWLClass, OWLClassAssertionAxiom, OWLIndividual> o2) {
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
