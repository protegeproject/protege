package org.protege.editor.owl.model.entity;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import org.protege.editor.owl.ui.action.SelectedOWLEntityAction;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Sep 2017
 */
public class CopyEntityDisplayName extends SelectedOWLEntityAction {

    @Override
    protected void actionPerformed(OWLEntity selectedEntity) {
        String displayName = getOWLModelManager().getRendering(selectedEntity);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(displayName), null);
    }

    @Override
    protected void disposeAction() throws Exception {

    }
}
