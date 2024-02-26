package org.protege.editor.owl.ui.frame.annotationproperty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLAnnotationPropertyDomainEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 4, 2009<br><br>
 */
public class OWLAnnotationPropertyDomainFrameSection extends AbstractOWLFrameSection<OWLAnnotationProperty, OWLAnnotationPropertyDomainAxiom, IRI> {

    public static final String LABEL = "Domains (intersection)";

    private Set<IRI> addedDomains = new HashSet<>();

    private OWLAnnotationPropertyDomainEditor editor;


    public OWLAnnotationPropertyDomainFrameSection(OWLEditorKit editorKit, OWLFrame<OWLAnnotationProperty> frame) {
        super(editorKit, LABEL, "Domain", frame);
    }


    protected OWLAnnotationPropertyDomainAxiom createAxiom(IRI iri) {
        return getOWLDataFactory().getOWLAnnotationPropertyDomainAxiom(getRootObject(), iri);
    }


    protected Set<OWLAnnotationPropertyDomainAxiom> getAxioms(OWLOntology ontology) {
        return ontology.getAnnotationPropertyDomainAxioms(getRootObject());
    }


    public OWLObjectEditor<IRI> getObjectEditor() {
        if (editor == null){
            editor = new OWLAnnotationPropertyDomainEditor(getOWLEditorKit());
        }
        return editor;
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
        for (OWLAnnotationPropertyDomainAxiom ax : getAxioms(ontology)) {
            addRow(new OWLAnnotationPropertyDomainFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
            addedDomains.add(ax.getDomain());
        }
    }


    protected final void refillInferred() {
        // Could do some trivial manual inference to aid the user
    }


    public Comparator<OWLFrameSectionRow<OWLAnnotationProperty, OWLAnnotationPropertyDomainAxiom, IRI>> getRowComparator() {
        return null;
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	return change.isAxiomChange() &&
    			change.getAxiom() instanceof OWLAnnotationPropertyDomainAxiom &&
    			((OWLAnnotationPropertyDomainAxiom) change.getAxiom()).getProperty().equals(getRootObject());
    }
}
