package org.protege.editor.owl.ui.frame.annotationproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLAnnotationPropertyRangeEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;

import java.util.*;


/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 4, 2009<br><br>
 */
public class OWLAnnotationPropertyRangeFrameSection extends AbstractOWLFrameSection<OWLAnnotationProperty, OWLAnnotationPropertyRangeAxiom, IRI> {

    public static final String LABEL = "Range (intersection)";

    private Set<IRI> addedDomains = new HashSet<>();


    public OWLAnnotationPropertyRangeFrameSection(OWLEditorKit editorKit, OWLFrame<OWLAnnotationProperty> frame) {
        super(editorKit, LABEL, "Range", frame);
    }


    protected OWLAnnotationPropertyRangeAxiom createAxiom(IRI iri) {
        return getOWLDataFactory().getOWLAnnotationPropertyRangeAxiom(getRootObject(), iri);
    }


    protected Set<OWLAnnotationPropertyRangeAxiom> getAxioms(OWLOntology ontology) {
        return ontology.getAnnotationPropertyRangeAxioms(getRootObject());
    }


    public OWLObjectEditor<IRI> getObjectEditor() {
        return new OWLAnnotationPropertyRangeEditor(getOWLEditorKit());
    }


    public final boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLClassExpression)) {
                return false;
            }
        }
        return true;
    }


    public final boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        for (OWLObject obj : objects) {
            if (obj instanceof OWLEntity) {
                OWLEntity entity = (OWLEntity) obj;
                OWLAxiom ax = createAxiom(entity.getIRI());
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            }
            else {
                return false;
            }
        }
        getOWLModelManager().applyChanges(changes);
        return true;
    }


    protected void clear() {
        addedDomains.clear();
    }


    protected final void refill(OWLOntology ontology) {
        for (OWLAnnotationPropertyRangeAxiom ax : getAxioms(ontology)) {
            addRow(new OWLAnnotationPropertyRangeFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
            addedDomains.add(ax.getRange());
        }
    }


    protected final void refillInferred() {
        // Could do some trivial manual inference to aid the user
    }


    public Comparator<OWLFrameSectionRow<OWLAnnotationProperty, OWLAnnotationPropertyRangeAxiom, IRI>> getRowComparator() {
        return null;
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	return change.isAxiomChange() &&
    			change.getAxiom() instanceof OWLAnnotationPropertyRangeAxiom &&
    			((OWLAnnotationPropertyRangeAxiom) change.getAxiom()).getProperty().equals(getRootObject());
    }
}
