package org.protege.editor.owl.ui.frame.property;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.*;

import java.util.*;

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
 * Date: Oct 16, 2008<br><br>
 */
public abstract class AbstractPropertyDomainFrameSection<P extends OWLProperty, A extends OWLPropertyDomainAxiom>  extends AbstractOWLFrameSection<P, A, OWLDescription> {

    public static final String LABEL = "Domains (intersection)";

    Set<OWLDescription> addedDomains = new HashSet<OWLDescription>();


    public AbstractPropertyDomainFrameSection(OWLEditorKit editorKit, OWLFrame<P> frame) {
        super(editorKit, LABEL, "Domain", frame);
    }


    public OWLFrameSectionRowObjectEditor<OWLDescription> getObjectEditor() {
        return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(null);        
    }


    public final boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLDescription)) {
                return false;
            }
        }
        return true;
    }


    public final boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLObject obj : objects) {
            if (obj instanceof OWLDescription) {
                OWLDescription desc = (OWLDescription) obj;
                OWLAxiom ax = createAxiom(desc);
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            }
            else {
                return false;
            }
        }
        getOWLModelManager().applyChanges(changes);
        return true;
    }


    protected abstract AbstractPropertyDomainFrameSectionRow<P, A> createFrameSectionRow(A domainAxiom, OWLOntology ontology);


    protected abstract Set<A> getAxioms(OWLOntology ontology);


    protected abstract Set<Set<OWLDescription>> getInferredDomains() throws OWLReasonerException;


    protected void clear() {
        addedDomains.clear();
    }


    protected final void refill(OWLOntology ontology) {
        for (A ax : getAxioms(ontology)) {
            addRow(createFrameSectionRow(ax, ontology));
            addedDomains.add(ax.getDomain());
        }
    }


    protected final void refillInferred() {
        try {
            for (Set<OWLDescription> domains : getInferredDomains()) {
                for (OWLDescription domain : domains){
                    if (!addedDomains.contains(domain)) {
                        addRow(createFrameSectionRow(createAxiom(domain), null));
                        addedDomains.add(domain);
                    }
                }
            }
        }
        catch (OWLReasonerException e) {
            e.printStackTrace();
        }
    }


    public Comparator<OWLFrameSectionRow<P, A, OWLDescription>> getRowComparator() {
        return null;
    }
}
