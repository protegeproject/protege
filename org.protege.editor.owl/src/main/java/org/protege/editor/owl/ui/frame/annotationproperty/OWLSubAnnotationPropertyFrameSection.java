package org.protege.editor.owl.ui.frame.annotationproperty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLAnnotationPropertyEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 4, 2009<br><br>
 */
public class OWLSubAnnotationPropertyFrameSection extends AbstractOWLFrameSection<OWLAnnotationProperty, OWLSubAnnotationPropertyOfAxiom, OWLAnnotationProperty> {

    public static final String LABEL = "Superproperties";

    private Set<OWLAnnotationProperty> added = new HashSet<OWLAnnotationProperty>();

    private OWLAnnotationPropertyEditor editor;


    public OWLSubAnnotationPropertyFrameSection(OWLEditorKit editorKit, OWLFrame<OWLAnnotationProperty> frame) {
        super(editorKit, LABEL, "Superproperties", frame);
    }


    protected OWLSubAnnotationPropertyOfAxiom createAxiom(OWLAnnotationProperty superProp) {
        return getOWLDataFactory().getOWLSubAnnotationPropertyOfAxiom(getRootObject(), superProp);
    }


    protected Set<OWLSubAnnotationPropertyOfAxiom> getAxioms(OWLOntology ontology) {
        return ontology.getSubAnnotationPropertyOfAxioms(getRootObject());
    }


    public OWLObjectEditor<OWLAnnotationProperty> getObjectEditor() {
        if (editor == null){
            editor = new OWLAnnotationPropertyEditor(getOWLEditorKit());
        }
        return editor;
    }


    public final boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLAnnotationProperty)) {
                return false;
            }
        }
        return true;
    }


    public final boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLObject obj : objects) {
            if (obj instanceof OWLAnnotationProperty) {
                OWLAnnotationProperty property = (OWLAnnotationProperty) obj;
                OWLAxiom ax = createAxiom(property);
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
        added.clear();
    }


    protected final void refill(OWLOntology ontology) {
        for (OWLSubAnnotationPropertyOfAxiom ax : getAxioms(ontology)) {
            addRow(new OWLSubAnnotationPropertyFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
            added.add(ax.getSuperProperty());
        }
    }


    protected final void refillInferred() {
        // Could do some trivial manual inference to aid the user
    }


    public Comparator<OWLFrameSectionRow<OWLAnnotationProperty, OWLSubAnnotationPropertyOfAxiom, OWLAnnotationProperty>> getRowComparator() {
        return null;
    }
    
    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	if (!change.isAxiomChange()) {
    		return false;
    	}
    	OWLAxiom axiom = change.getAxiom();
    	if (axiom instanceof OWLSubAnnotationPropertyOfAxiom) {
    		return ((OWLSubAnnotationPropertyOfAxiom) axiom).getSubProperty().equals(getRootObject());
    	}
    	return false;
    }

}
