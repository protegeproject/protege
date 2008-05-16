package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.CollectionFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDisjointDataPropertiesFrameSectionRow extends AbstractOWLFrameSectionRow<OWLDataProperty, OWLDisjointDataPropertiesAxiom, OWLDataProperty> {

    private OWLFrameSection section;

    public OWLDisjointDataPropertiesFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                    OWLOntology ontology, OWLDataProperty rootObject,
                                                    OWLDisjointDataPropertiesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
        this.section = section;
    }


    protected OWLDisjointDataPropertiesAxiom createAxiom(OWLDataProperty editedObject) {
        return getOWLDataFactory().getOWLDisjointDataPropertiesAxiom(CollectionFactory.createSet(getRoot(),
                editedObject));
    }


    protected OWLFrameSectionRowObjectEditor<OWLDataProperty> getObjectEditor() {
        OWLDataPropertyEditor editor = (OWLDataPropertyEditor) section.getEditor();
        final Set<OWLDataPropertyExpression> disjoints =
                new HashSet<OWLDataPropertyExpression>(getAxiom().getProperties());
        disjoints.remove(getRootObject());
        if (disjoints.size() == 1){
            OWLDataPropertyExpression p = disjoints.iterator().next();
            if (!p.isAnonymous()){
                editor.setEditedObject(p.asOWLDataProperty());
            }
        }
        return editor;
    }


    public List<? extends OWLObject> getManipulatableObjects() {
        List<OWLDataPropertyExpression> props = new ArrayList<OWLDataPropertyExpression>(getAxiom().getProperties());
        props.remove(getRootObject());
        return props;
    }
}
