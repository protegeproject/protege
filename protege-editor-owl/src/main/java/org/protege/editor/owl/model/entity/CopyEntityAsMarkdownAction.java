package org.protege.editor.owl.model.entity;

import org.protege.editor.owl.ui.action.SelectedOWLEntityAction;
import org.semanticweb.owlapi.model.OWLEntity;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29 Sep 2017
 */
public class CopyEntityAsMarkdownAction extends SelectedOWLEntityAction {

    @Override
    protected void actionPerformed(OWLEntity selectedEntity) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        MarkdownRenderer renderer = new MarkdownRenderer(getOWLModelManager());
        String rendering = renderer.renderMarkdown(selectedEntity);
        clipboard.setContents(new StringSelection(rendering), null);
    }

    @Override
    protected void disposeAction() throws Exception {

    }
}
