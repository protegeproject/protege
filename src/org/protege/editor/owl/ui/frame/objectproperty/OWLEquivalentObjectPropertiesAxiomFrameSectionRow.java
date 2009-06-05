package org.protege.editor.owl.ui.frame.objectproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
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
 * Date: 29-Jan-2007<br><br>
 */
public class OWLEquivalentObjectPropertiesAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLObjectProperty, OWLEquivalentObjectPropertiesAxiom, OWLObjectProperty> {

    public OWLEquivalentObjectPropertiesAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                             OWLOntology ontology, OWLObjectProperty rootObject,
                                                             OWLEquivalentObjectPropertiesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<OWLObjectProperty> getObjectEditor() {
        final OWLObjectPropertyEditor editor = new OWLObjectPropertyEditor(getOWLEditorKit());
        final Set<OWLObjectPropertyExpression> equivs =
                new HashSet<OWLObjectPropertyExpression>(getAxiom().getProperties());
        equivs.remove(getRootObject());
        if (equivs.size() == 1){
            final OWLObjectPropertyExpression p = equivs.iterator().next();
            if (!p.isAnonymous()){
                editor.setEditedObject(p.asOWLObjectProperty());
           }
        }
        return editor;    }


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
