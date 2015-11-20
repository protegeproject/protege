package org.protege.editor.owl.ui.frame.cls;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.CollectionFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Feb-2007<br><br>
 */
public class InheritedAnonymousClassesFrameSectionRow extends AbstractOWLFrameSectionRow<OWLClass, OWLClassAxiom, OWLClassExpression> {

    public InheritedAnonymousClassesFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<OWLClass, OWLClassAxiom, OWLClassExpression> section,
                                                    OWLOntology ontology, OWLClass rootObject, OWLClassAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<OWLClassExpression> getObjectEditor() {
        if (getAxiom() instanceof OWLSubClassOfAxiom) {
            OWLClassExpression superCls = ((OWLSubClassOfAxiom) getAxiom()).getSuperClass();
            return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(superCls, AxiomType.SUBCLASS_OF);
        }
        else {
            Set<OWLClassExpression> descs = new HashSet<OWLClassExpression>(((OWLEquivalentClassesAxiom) getAxiom()).getClassExpressions());
            descs.remove(getRootObject());
            OWLClassExpression desc;
            if (descs.isEmpty()){
                // in the weird case that something is asserted equiv to itself
                desc = getRootObject();
            }
            else{
                desc = descs.iterator().next();
            }
            return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(desc, AxiomType.EQUIVALENT_CLASSES);
        }
    }


    protected OWLClassAxiom createAxiom(OWLClassExpression editedObject) {
        if (getAxiom() instanceof OWLSubClassOfAxiom) {
            return getOWLDataFactory().getOWLSubClassOfAxiom(getRoot(), editedObject);
        }
        else {
            return getOWLDataFactory().getOWLEquivalentClassesAxiom(CollectionFactory.createSet(getRoot(),
                                                                                                editedObject));
        }
    }


    public List<OWLClassExpression> getManipulatableObjects() {
        if (getAxiom() instanceof OWLSubClassOfAxiom) {
            return Arrays.asList(((OWLSubClassOfAxiom) getAxiom()).getSuperClass());
        }
        else {
            Set<OWLClassExpression> descs = new HashSet<OWLClassExpression>(((OWLEquivalentClassesAxiom) getAxiom()).getClassExpressions());
            descs.remove(getRootObject());
            if (descs.isEmpty()){
                // in the weird case that something is asserted equiv to itself
            	OWLClassExpression cls = getRootObject();
            	return Arrays.asList(cls);
            }
            return Arrays.asList(descs.iterator().next());
        }
    }


    public String getTooltip() {
        return "Inherited from " + getOWLModelManager().getRendering(getRootObject());
    }
}
