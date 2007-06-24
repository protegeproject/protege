package org.protege.editor.owl.ui.frame;

import java.util.ArrayList;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.CollectionFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDisjointDataPropertiesFrameSectionRow extends AbstractOWLFrameSectionRow<OWLDataProperty, OWLDisjointDataPropertiesAxiom, OWLDataProperty> {

    public OWLDisjointDataPropertiesFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                    OWLOntology ontology, OWLDataProperty rootObject,
                                                    OWLDisjointDataPropertiesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLDisjointDataPropertiesAxiom createAxiom(OWLDataProperty editedObject) {
        return getOWLDataFactory().getOWLDisjointDataPropertiesAxiom(CollectionFactory.createSet(getRoot(),
                                                                                                 editedObject));
    }


    protected OWLFrameSectionRowObjectEditor<OWLDataProperty> getObjectEditor() {
        return null;
    }


    public List<? extends OWLObject> getManipulatableObjects() {
        List<OWLDataPropertyExpression> props = new ArrayList<OWLDataPropertyExpression>(getAxiom().getProperties());
        props.remove(getRootObject());
        return props;
    }
}
