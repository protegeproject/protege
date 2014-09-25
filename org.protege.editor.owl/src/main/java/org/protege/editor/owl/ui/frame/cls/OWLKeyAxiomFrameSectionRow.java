package org.protege.editor.owl.ui.frame.cls;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLPropertySetEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 4, 2009<br><br>
 */
public class OWLKeyAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLClass, OWLHasKeyAxiom, Set<OWLPropertyExpression>> {

    public OWLKeyAxiomFrameSectionRow(OWLEditorKit owlEditorKit, 
    								  OWLFrameSection<OWLClass, OWLHasKeyAxiom, Set<OWLPropertyExpression>> section,
                                      OWLOntology ontology, OWLClass rootObject,
                                      OWLHasKeyAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLHasKeyAxiom createAxiom(Set<OWLPropertyExpression> properties) {
    	/*
    	 * Degenericized to be compatible with changing OWLAPI interfaces
    	 */
    	return getOWLDataFactory().getOWLHasKeyAxiom(getRootObject(), (Set) properties);
    }


    protected OWLPropertySetEditor getObjectEditor() {
        final OWLPropertySetEditor editor = new OWLPropertySetEditor(getOWLEditorKit());
    	/*
    	 * Degenericized to be compatible with changing OWLAPI interfaces
    	 */
        editor.setEditedObject((Set) getAxiom().getPropertyExpressions());
        return editor;
    }


    public List<OWLPropertyExpression> getManipulatableObjects() {
        return new ArrayList<OWLPropertyExpression>(getAxiom().getPropertyExpressions());
    }}
