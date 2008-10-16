package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSectionRow;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLObjectPropertyDomainFrameSectionRow extends AbstractPropertyDomainFrameSectionRow<OWLObjectProperty, OWLObjectPropertyDomainAxiom> {


    public OWLObjectPropertyDomainFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                  OWLOntology ontology, OWLObjectProperty rootObject,
                                                  OWLObjectPropertyDomainAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectPropertyDomainAxiom createAxiom(OWLDescription editedObject) {
        return getOWLDataFactory().getOWLObjectPropertyDomainAxiom(getRootObject(), editedObject);
    }
}
