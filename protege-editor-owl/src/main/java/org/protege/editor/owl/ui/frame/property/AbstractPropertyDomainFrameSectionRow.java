package org.protege.editor.owl.ui.frame.property;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.util.OWLComponentFactory;
import org.semanticweb.owlapi.model.*;

import java.util.Arrays;
import java.util.List;

/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 16, 2008<br><br>
 */
public abstract class AbstractPropertyDomainFrameSectionRow<P extends OWLProperty, A extends OWLPropertyDomainAxiom> extends AbstractOWLFrameSectionRow<P, A, OWLClassExpression> {

    public AbstractPropertyDomainFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<P, A, OWLClassExpression> section,
                                                OWLOntology ontology, P rootObject,
                                                A axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<OWLClassExpression> getObjectEditor() {
        final OWLComponentFactory cf = getOWLEditorKit().getWorkspace().getOWLComponentFactory();
        final A ax = getAxiom();
        return cf.getOWLClassDescriptionEditor(ax.getDomain(), ax.getAxiomType());
    }


    public List<? extends OWLObject> getManipulatableObjects() {
        return Arrays.asList(getAxiom().getDomain());
    }
}

