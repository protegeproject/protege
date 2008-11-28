package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public class OWLDisjointClassesAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLDescription, OWLDisjointClassesAxiom, Set<OWLDescription>> {


    public OWLDisjointClassesAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                  OWLOntology ontology, OWLDescription rootObject,
                                                  OWLDisjointClassesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<Set<OWLDescription>> getObjectEditor() {
        return new OWLClassDescriptionSetEditor(getOWLEditorKit(), getManipulatableObjects());
    }


    protected OWLDisjointClassesAxiom createAxiom(Set<OWLDescription> editedObject) {
        editedObject.add(getRootObject());
        return getOWLDataFactory().getOWLDisjointClassesAxiom(editedObject);
    }


    /**
     * Gets a list of objects contained in this row.
     */
    public List<OWLDescription> getManipulatableObjects() {
        Set<OWLDescription> disjointClasses = new HashSet<OWLDescription>(getAxiom().getDescriptions());
        disjointClasses.remove(getRootObject());
        return new ArrayList<OWLDescription>(disjointClasses);
    }
}
