package org.protege.editor.owl.ui.frame;

import java.util.Comparator;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLSameIndividualsAxiomFrameSection extends AbstractOWLFrameSection<OWLIndividual, OWLSameIndividualsAxiom, Set<OWLIndividual>> {

    public static final String LABEL = "Same individuals";


    public OWLSameIndividualsAxiomFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLIndividual> frame) {
        super(editorKit, LABEL, frame);
    }


    protected void clear() {
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLSameIndividualsAxiom ax : ontology.getSameIndividualAxioms(getRootObject())) {
            addRow(new OWLSameIndividualsAxiomFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
        }
    }


    public void visit(OWLSameIndividualsAxiom axiom) {
        if (axiom.getIndividuals().contains(getRootObject())) {
            reset();
        }
    }


    protected OWLSameIndividualsAxiom createAxiom(Set<OWLIndividual> object) {
        object.add(getRootObject());
        return getOWLDataFactory().getOWLSameIndividualsAxiom(object);
    }


    public OWLFrameSectionRowObjectEditor<Set<OWLIndividual>> getObjectEditor() {
        return new OWLIndividualSetEditor(getOWLEditorKit());
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLIndividual, OWLSameIndividualsAxiom, Set<OWLIndividual>>> getRowComparator() {
        return null;
    }
}
