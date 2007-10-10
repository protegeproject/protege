package org.protege.editor.owl.ui.frame;

import java.util.ArrayList;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.CollectionFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLEquivalentObjectPropertiesAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLObjectProperty, OWLEquivalentObjectPropertiesAxiom, OWLObjectProperty> {

    public OWLEquivalentObjectPropertiesAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                             OWLOntology ontology, OWLObjectProperty rootObject,
                                                             OWLEquivalentObjectPropertiesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<OWLObjectProperty> getObjectEditor() {
        return null;
    }


    protected OWLEquivalentObjectPropertiesAxiom createAxiom(OWLObjectProperty editedObject) {
        return getOWLDataFactory().getOWLEquivalentObjectPropertiesAxiom(CollectionFactory.createSet(getRoot(),
                                                                                                     editedObject));
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List<? extends OWLObject> getManipulatableObjects() {
        List<OWLObjectPropertyExpression> props = new ArrayList<OWLObjectPropertyExpression>(getAxiom().getProperties());
        props.remove(getRoot());
        return props;
    }
}
