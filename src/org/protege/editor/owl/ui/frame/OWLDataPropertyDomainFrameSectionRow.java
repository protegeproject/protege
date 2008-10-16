package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSectionRow;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDataPropertyDomainFrameSectionRow extends AbstractPropertyDomainFrameSectionRow<OWLDataProperty, OWLDataPropertyDomainAxiom> {

    public OWLDataPropertyDomainFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                OWLOntology ontology, OWLDataProperty rootObject,
                                                OWLDataPropertyDomainAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLDataPropertyDomainAxiom createAxiom(OWLDescription editedObject) {
        return getOWLDataFactory().getOWLDataPropertyDomainAxiom(getRootObject(), editedObject);
    }
}
