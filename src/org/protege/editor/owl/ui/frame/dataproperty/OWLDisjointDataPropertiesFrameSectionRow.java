package org.protege.editor.owl.ui.frame.dataproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.editor.OWLDataPropertySetEditor;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.*;

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
public class OWLDisjointDataPropertiesFrameSectionRow extends AbstractOWLFrameSectionRow<OWLDataProperty, OWLDisjointDataPropertiesAxiom, Set<OWLDataProperty>> {

    private OWLFrameSection section;

    public OWLDisjointDataPropertiesFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                    OWLOntology ontology, OWLDataProperty rootObject,
                                                    OWLDisjointDataPropertiesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
        this.section = section;
    }


    protected OWLDisjointDataPropertiesAxiom createAxiom(Set<OWLDataProperty> editedObject) {
        Set<OWLDataProperty> props = new HashSet<OWLDataProperty>();
        props.add(getRootObject());
        props.addAll(editedObject);
        return getOWLDataFactory().getOWLDisjointDataPropertiesAxiom(props);
    }


    protected OWLFrameSectionRowObjectEditor<Set<OWLDataProperty>> getObjectEditor() {
        OWLDataPropertySetEditor editor = (OWLDataPropertySetEditor) section.getEditor();
        final Set<OWLDataPropertyExpression> disjoints = getAxiom().getProperties();
        final Set<OWLDataProperty> namedDisjoints = new HashSet<OWLDataProperty>();
        for (OWLDataPropertyExpression p : disjoints){
            if (!p.isAnonymous()){
                namedDisjoints.add(p.asOWLDataProperty());
            }
        }
        namedDisjoints.remove(getRootObject());
        editor.setEditedObject(namedDisjoints);
        // @@TODO handle property expressions
        return editor;
    }


    public List<? extends OWLObject> getManipulatableObjects() {
        List<OWLDataPropertyExpression> props = new ArrayList<OWLDataPropertyExpression>(getAxiom().getProperties());
        props.remove(getRootObject());
        return props;
    }
}
