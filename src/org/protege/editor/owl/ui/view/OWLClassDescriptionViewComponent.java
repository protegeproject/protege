package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.ui.frame.OWLClassDescriptionFrame;
import org.protege.editor.owl.ui.frame.OWLSubClassAxiomFrameSectionRow;
import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.protege.editor.owl.ui.framelist.OWLFrameListPopupMenuAction;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.CollectionFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 04-Feb-2007<br><br>
 */
public class OWLClassDescriptionViewComponent extends AbstractOWLClassViewComponent {

    private OWLFrameList2<OWLClass> list;


    public void initialiseClassView() throws Exception {
        list = new OWLFrameList2<OWLClass>(getOWLEditorKit(), new OWLClassDescriptionFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane(list);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(sp);
        list.addToPopupMenu(new ConvertSelectionToEquivalentClassAction());
    }


    /**
     * This method is called to request that the view is updated with
     * the specified class.
     * @param selectedClass The class that the view should be updated with.  Note
     *                      that this may be <code>null</code>, which indicates that the view should
     *                      be cleared
     * @return The actual class that the view is displaying after it has been updated
     *         (may be <code>null</code>)
     */
    protected OWLClass updateView(OWLClass selectedClass) {
        list.setRootObject(selectedClass);
        return selectedClass;
    }


    public void disposeView() {
        list.dispose();
    }


    private class ConvertSelectionToEquivalentClassAction extends OWLFrameListPopupMenuAction {

        protected void initialise() throws Exception {
        }


        protected void dispose() throws Exception {
        }


        protected String getName() {
            return "Convert selected rows to defined class";
        }


        protected void updateState() {
            if (list.getSelectedValue() == null) {
                setEnabled(false);
            }
            for (Object selVal : list.getSelectedValues()) {
                if (!(selVal instanceof OWLSubClassAxiomFrameSectionRow)) {
                    setEnabled(false);
                    return;
                }
            }
            setEnabled(true);
        }


        public void actionPerformed(ActionEvent e) {
            convertSelectedRowsToDefinedClass();
        }
    }


    private void convertSelectedRowsToDefinedClass() {
        Set<OWLDescription> descriptions = new HashSet<OWLDescription>();
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (Object selVal : list.getSelectedValues()) {
            if (selVal instanceof OWLSubClassAxiomFrameSectionRow) {
                OWLSubClassAxiomFrameSectionRow row = (OWLSubClassAxiomFrameSectionRow) selVal;
                changes.add(new RemoveAxiom(row.getOntology(), row.getAxiom()));
                descriptions.add(row.getAxiom().getSuperClass());
            }
        }
        OWLDescription equivalentClass;
        if (descriptions.size() == 1) {
            equivalentClass = descriptions.iterator().next();
        }
        else {
            equivalentClass = getOWLDataFactory().getOWLObjectIntersectionOf(descriptions);
        }
        Set<OWLDescription> axiomOperands = CollectionFactory.createSet(list.getRootObject(), equivalentClass);
        changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(),
                                 getOWLDataFactory().getOWLEquivalentClassesAxiom(axiomOperands)));
        getOWLModelManager().applyChanges(changes);
    }
}
