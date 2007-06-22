package org.protege.editor.owl.ui.framelist;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
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
 * Date: 23-Feb-2007<br><br>
 */
public abstract class OWLFrameListPopupMenuAction<R extends OWLObject> extends AbstractAction {

    private OWLFrameList2<R> frameList;

    private OWLEditorKit owlEditorKit;


    protected OWLFrameListPopupMenuAction() {
        putValue(AbstractAction.NAME, getName());
    }


    protected void setup(OWLEditorKit owlEditorKit, OWLFrameList2<R> list) {
        this.owlEditorKit = owlEditorKit;
        this.frameList = list;
    }


    protected abstract String getName();


    protected abstract void initialise() throws Exception;


    protected abstract void dispose() throws Exception;


    protected OWLFrameList2<R> getFrameList() {
        return frameList;
    }


    protected R getRootObject() {
        return frameList.getRootObject();
    }


    protected OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }


    protected OWLModelManager getOWLModelManager() {
        return owlEditorKit.getOWLModelManager();
    }


    protected OWLDataFactory getOWLDataFactory() {
        return getOWLModelManager().getOWLDataFactory();
    }


    protected List<OWLFrameSectionRow<R, ? extends OWLAxiom, ? extends Object>> getSelectedRows() {
        List<OWLFrameSectionRow<R, ? extends OWLAxiom, ? extends Object>> rows = new ArrayList<OWLFrameSectionRow<R, ? extends OWLAxiom, ? extends Object>>();
        for (Object selVal : getFrameList().getSelectedValues()) {
            if (selVal instanceof OWLFrameSectionRow) {
                rows.add((OWLFrameSectionRow<R, ? extends OWLAxiom, ? extends Object>) selVal);
            }
        }
        return rows;
    }


    /**
     * This will be called when the selection in the list changes
     * in order for the action to enable/disable itself depending
     * upon what is selected.
     */
    protected abstract void updateState();
}
