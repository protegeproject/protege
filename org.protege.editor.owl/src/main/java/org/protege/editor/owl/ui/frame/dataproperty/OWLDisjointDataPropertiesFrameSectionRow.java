package org.protege.editor.owl.ui.frame.dataproperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataPropertySetEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDisjointDataPropertiesFrameSectionRow extends AbstractOWLFrameSectionRow<OWLDataProperty, OWLDisjointDataPropertiesAxiom, Set<OWLDataProperty>> {

    public OWLDisjointDataPropertiesFrameSectionRow(OWLEditorKit owlEditorKit, 
    												OWLFrameSection<OWLDataProperty, OWLDisjointDataPropertiesAxiom, Set<OWLDataProperty>> section,
                                                    OWLOntology ontology, OWLDataProperty rootObject,
                                                    OWLDisjointDataPropertiesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLDisjointDataPropertiesAxiom createAxiom(Set<OWLDataProperty> editedObject) {
        Set<OWLDataProperty> props = new HashSet<OWLDataProperty>();
        props.add(getRootObject());
        props.addAll(editedObject);
        return getOWLDataFactory().getOWLDisjointDataPropertiesAxiom(props);
    }


    protected OWLObjectEditor<Set<OWLDataProperty>> getObjectEditor() {
        OWLDataPropertySetEditor editor = new OWLDataPropertySetEditor(getOWLEditorKit());
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

    @Override
    public boolean checkEditorResults(OWLObjectEditor<Set<OWLDataProperty>> editor) {
    	Set<OWLDataProperty> equivalents = editor.getEditedObject();
    	return !equivalents.contains(getRootObject());
    }

    public List<OWLDataPropertyExpression> getManipulatableObjects() {
        List<OWLDataPropertyExpression> props = new ArrayList<OWLDataPropertyExpression>(getAxiom().getProperties());
        props.remove(getRootObject());
        return props;
    }
}
