package org.protege.editor.owl.ui.frame.cls;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.anonymouscls.AnonymousDefinedClassManager;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClassExpression;
import org.semanticweb.owl.model.OWLOntology;

import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
