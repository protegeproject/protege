package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.CollectionFactory;

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
public class OWLEquivalentClassesAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLDescription, OWLEquivalentClassesAxiom, OWLDescription> {

    public OWLEquivalentClassesAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                    OWLOntology ontology, OWLDescription rootObject,
                                                    OWLEquivalentClassesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected List getObjects() {
        Set<OWLDescription> clses = new HashSet<OWLDescription>(getAxiom().getDescriptions());
        clses.remove(getRoot());
        return new ArrayList<OWLDescription>(clses);
    }


    protected OWLFrameSectionRowObjectEditor<OWLDescription> getObjectEditor() {
        Set<OWLDescription> descs = new HashSet<OWLDescription>(getAxiom().getDescriptions());
        descs.remove(getRoot());
        return new OWLClassDescriptionEditor(getOWLEditorKit(), descs.iterator().next());
    }


    protected OWLEquivalentClassesAxiom createAxiom(OWLDescription editedObject) {
        return getOWLDataFactory().getOWLEquivalentClassesAxiom(CollectionFactory.createSet(getRoot(), editedObject));
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List getManipulatableObjects() {
        return getObjects();
    }
}

