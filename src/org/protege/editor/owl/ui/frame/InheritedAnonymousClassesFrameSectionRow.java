package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.CollectionFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Feb-2007<br><br>
 */
public class InheritedAnonymousClassesFrameSectionRow extends AbstractOWLFrameSectionRow<OWLClass, OWLClassAxiom, OWLDescription> {

    public InheritedAnonymousClassesFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                    OWLOntology ontology, OWLClass rootObject, OWLClassAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<OWLDescription> getObjectEditor() {
        if (getAxiom() instanceof OWLSubClassAxiom) {
            OWLDescription superCls = ((OWLSubClassAxiom) getAxiom()).getSuperClass();
            return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(superCls);
        }
        else {
            Set<OWLDescription> descs = new HashSet<OWLDescription>(((OWLEquivalentClassesAxiom) getAxiom()).getDescriptions());
            descs.remove(getRootObject());
            return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(descs.iterator().next());
        }
    }


    protected OWLClassAxiom createAxiom(OWLDescription editedObject) {
        if (getAxiom() instanceof OWLSubClassAxiom) {
            return getOWLDataFactory().getOWLSubClassAxiom(getRoot(), editedObject);
        }
        else {
            return getOWLDataFactory().getOWLEquivalentClassesAxiom(CollectionFactory.createSet(getRoot(),
                                                                                                editedObject));
        }
    }


    public List<? extends OWLObject> getManipulatableObjects() {
        if (getAxiom() instanceof OWLSubClassAxiom) {
            return Arrays.asList(((OWLSubClassAxiom) getAxiom()).getSuperClass());
        }
        else {
            Set<OWLDescription> descs = new HashSet<OWLDescription>(((OWLEquivalentClassesAxiom) getAxiom()).getDescriptions());
            descs.remove(getRootObject());
            return Arrays.asList(descs.iterator().next());
        }
    }


    public String getTooltip() {
        return "Inherited from " + getOWLModelManager().getRendering(getRootObject());
    }
}
