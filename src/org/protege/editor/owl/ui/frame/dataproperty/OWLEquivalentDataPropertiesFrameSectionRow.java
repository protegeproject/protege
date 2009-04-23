package org.protege.editor.owl.ui.frame.dataproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.editor.OWLDataPropertyEditor;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
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
public class OWLEquivalentDataPropertiesFrameSectionRow extends AbstractOWLFrameSectionRow<OWLDataProperty, OWLEquivalentDataPropertiesAxiom, OWLDataProperty> {

    private OWLFrameSection section;

    public OWLEquivalentDataPropertiesFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                      OWLOntology ontology, OWLDataProperty rootObject,
                                                      OWLEquivalentDataPropertiesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
        this.section = section;
    }


    protected OWLEquivalentDataPropertiesAxiom createAxiom(OWLDataProperty editedObject) {
        return getOWLDataFactory().getOWLEquivalentDataPropertiesAxiom(CollectionFactory.createSet(getRoot(),
                editedObject));
    }


    protected OWLFrameSectionRowObjectEditor<OWLDataProperty> getObjectEditor() {
        final OWLDataPropertyEditor editor = (OWLDataPropertyEditor) section.getEditor();
        final Set<OWLDataPropertyExpression> equivs =
                new HashSet<OWLDataPropertyExpression>(getAxiom().getProperties());
        equivs.remove(getRootObject());
        if (equivs.size() == 1){
            final OWLDataPropertyExpression p = equivs.iterator().next();
            if (!p.isAnonymous()){
                editor.setEditedObject(p.asOWLDataProperty());
            }
        }
        return editor;
    }


    public List<? extends OWLObject> getManipulatableObjects() {
        List<OWLDataPropertyExpression> props = new ArrayList<OWLDataPropertyExpression>(getAxiom().getProperties());
        props.remove(getRoot());
        return props;
    }
}
