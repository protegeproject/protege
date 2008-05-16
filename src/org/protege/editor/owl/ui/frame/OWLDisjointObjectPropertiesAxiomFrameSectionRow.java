package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;

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
public class OWLDisjointObjectPropertiesAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLObjectProperty, OWLDisjointObjectPropertiesAxiom, OWLObjectProperty> {

    private OWLFrameSection section;

    public OWLDisjointObjectPropertiesAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                           OWLOntology ontology, OWLObjectProperty rootObject,
                                                           OWLDisjointObjectPropertiesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);

        this.section = section;
    }


    protected OWLFrameSectionRowObjectEditor<OWLObjectProperty> getObjectEditor() {
        OWLObjectPropertyEditor editor = (OWLObjectPropertyEditor) section.getEditor();
        final Set<OWLObjectPropertyExpression> disjoints =
                new HashSet<OWLObjectPropertyExpression>(getAxiom().getProperties());
        disjoints.remove(getRootObject());
        if (disjoints.size() == 1){
            OWLObjectPropertyExpression p = disjoints.iterator().next();
            if (!p.isAnonymous()){
                editor.setEditedObject(p.asOWLObjectProperty());
            }
        }
        return editor;
    }


    protected OWLDisjointObjectPropertiesAxiom createAxiom(OWLObjectProperty editedObject) {
        Set<OWLObjectProperty> props = new HashSet<OWLObjectProperty>();
        props.add(getRootObject());
        props.add(editedObject);
        return getOWLDataFactory().getOWLDisjointObjectPropertiesAxiom(props);
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List getManipulatableObjects() {
        List<OWLObjectPropertyExpression> props = new ArrayList<OWLObjectPropertyExpression>(getAxiom().getProperties());
        props.remove(getRoot());
        return props;
    }
}

