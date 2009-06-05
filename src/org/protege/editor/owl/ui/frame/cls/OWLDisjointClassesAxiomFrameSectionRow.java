package org.protege.editor.owl.ui.frame.cls;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLClassExpressionSetEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owl.model.OWLClassExpression;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public class OWLDisjointClassesAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLClassExpression, OWLDisjointClassesAxiom, Set<OWLClassExpression>> {


    public OWLDisjointClassesAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                  OWLOntology ontology, OWLClassExpression rootObject,
                                                  OWLDisjointClassesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<Set<OWLClassExpression>> getObjectEditor() {
        return new OWLClassExpressionSetEditor(getOWLEditorKit(), getManipulatableObjects());
    }


    protected OWLDisjointClassesAxiom createAxiom(Set<OWLClassExpression> editedObject) {
        editedObject.add(getRootObject());
        return getOWLDataFactory().getOWLDisjointClassesAxiom(editedObject);
    }


    /**
     * Gets a list of objects contained in this row.
     */
    public List<OWLClassExpression> getManipulatableObjects() {
        Set<OWLClassExpression> disjointClasses = new HashSet<OWLClassExpression>(getAxiom().getClassExpressions());
        disjointClasses.remove(getRootObject());
        return new ArrayList<OWLClassExpression>(disjointClasses);
    }
}
