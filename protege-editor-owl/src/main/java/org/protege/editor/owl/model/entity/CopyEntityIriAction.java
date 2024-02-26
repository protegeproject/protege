package org.protege.editor.owl.model.entity;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.protege.editor.owl.ui.action.SelectedOWLEntityAction;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Sep 2017
 */
public class CopyEntityIriAction extends SelectedOWLEntityAction {

    @Override
    protected void actionPerformed(OWLEntity selectedEntity) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(selectedEntity.getIRI().toString()), null);
    }

    @Override
    protected void disposeAction() throws Exception {

    }
}
