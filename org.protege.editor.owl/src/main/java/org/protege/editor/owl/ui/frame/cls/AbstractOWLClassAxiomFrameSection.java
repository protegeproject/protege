package org.protege.editor.owl.ui.frame.cls;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.classexpression.anonymouscls.AnonymousDefinedClassManager;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;
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
 * Date: Nov 28, 2008<br><br>
 */
public abstract class AbstractOWLClassAxiomFrameSection<A extends OWLAxiom, E> extends AbstractOWLFrameSection<OWLClassExpression, A, E> {


    protected AbstractOWLClassAxiomFrameSection(OWLEditorKit editorKit, String label, String rowLabel, OWLFrame<? extends OWLClassExpression> owlFrame) {
        super(editorKit, label, rowLabel, owlFrame);
    }


    protected AbstractOWLClassAxiomFrameSection(OWLEditorKit editorKit, String label, OWLFrame<? extends OWLClassExpression> owlFrame) {
        super(editorKit, label, owlFrame);
    }


    public final OWLClassExpression getRootObject() {
        final OWLClassExpression cls = super.getRootObject();
        if (cls != null){
            final AnonymousDefinedClassManager ADCManager = getOWLModelManager().get(AnonymousDefinedClassManager.ID);

            if (ADCManager != null && ADCManager.isAnonymous(cls.asOWLClass())){
                return ADCManager.getExpression(cls.asOWLClass());
            }
        }
        return cls;
    }


    protected final void refill(OWLOntology ontology) {
        for (A ax : getClassAxioms(getRootObject(), ontology)){
            addAxiom(ax, ontology);
        }
    }


    protected abstract void addAxiom(A ax, OWLOntology ont);


    protected abstract Set<A> getClassAxioms(OWLClassExpression descr, OWLOntology ont);
}
