package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLObjectPropertyDomainFrameSection extends AbstractOWLFrameSection<OWLObjectProperty, OWLObjectPropertyDomainAxiom, OWLDescription> {

    public static final String LABEL = "Domains (intersection)";

    Set<OWLDescription> addedDomains = new HashSet<OWLDescription>();


    public OWLObjectPropertyDomainFrameSection(OWLEditorKit owlEditorKit, OWLFrame<? extends OWLObjectProperty> frame) {
        super(owlEditorKit, LABEL, frame);
    }


    protected void clear() {
        addedDomains.clear();
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {

        for (OWLObjectPropertyDomainAxiom ax : ontology.getObjectPropertyDomainAxioms(getRootObject())) {
            addRow(new OWLObjectPropertyDomainFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
            addedDomains.add(ax.getDomain());
        }
//        // Inferred stuff
//        try {
//            Set<OWLDescription> domains = new HashSet<OWLDescription>();
//            for(Set<OWLDescription> domainsSet : getOWLModelManager().getReasoner().getDomains(
//                    getRootObject())) {
//                domains.addAll(domainsSet);
//            }
//            for (OWLDescription desc : domains) {
//                if (!addedDomains.contains(desc)) {
//                    addRow(new OWLObjectPropertyDomainFrameSectionRow(getOWLEditorKit(),
//                                                                      this,
//                                                                      null,
//                                                                      getRootObject(),
//                                                                      getOWLDataFactory().getOWLObjectPropertyDomainAxiom(
//                                                                              getRootObject(),
//                                                                              desc)));
//                    addedDomains.add(desc);
//                }
//            }
//        }
//        catch (OWLReasonerException e) {
//            throw new OWLRuntimeException(e);
//        }
    }


    protected OWLObjectPropertyDomainAxiom createAxiom(OWLDescription object) {
        return getOWLDataFactory().getOWLObjectPropertyDomainAxiom(getRootObject(), object);
    }


    public OWLFrameSectionRowObjectEditor<OWLDescription> getObjectEditor() {
        return new OWLClassDescriptionEditor(getOWLEditorKit(), null);
    }


    public boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLDescription)) {
                return false;
            }
        }
        return true;
    }


    public boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLObject obj : objects) {
            if (obj instanceof OWLDescription) {
                OWLDescription desc = (OWLDescription) obj;
                OWLAxiom ax = getOWLDataFactory().getOWLObjectPropertyDomainAxiom(getRootObject(), desc);
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            }
            else {
                return false;
            }
        }
        getOWLModelManager().applyChanges(changes);
        return true;
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLObjectProperty, OWLObjectPropertyDomainAxiom, OWLDescription>> getRowComparator() {
        return null;
    }


    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        if (axiom.getProperty().equals(getRootObject())) {
            reset();
        }
    }
}
