package org.protege.editor.owl.ui.framelist;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Feb-2007<br><br>
 */
public abstract class OWLFrameListPopupMenuAction<R extends Object> extends AbstractAction {

    private OWLFrameList<R> frameList;

    private OWLEditorKit owlEditorKit;


    protected OWLFrameListPopupMenuAction() {
        putValue(AbstractAction.NAME, getName());
    }


    protected void setup(OWLEditorKit owlEditorKit, OWLFrameList<R> list) {
        this.owlEditorKit = owlEditorKit;
        this.frameList = list;
    }


    protected abstract String getName();


    protected abstract void initialise() throws Exception;


    protected abstract void dispose() throws Exception;


    protected OWLFrameList<R> getFrameList() {
        return frameList;
    }


    protected R getRootObject() {
        return frameList.getRootObject();
    }


    protected OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }


    protected OWLModelManager getOWLModelManager() {
        return owlEditorKit.getModelManager();
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
