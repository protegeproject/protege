package org.protege.editor.owl.model.entity;

import org.protege.editor.owl.ui.action.SelectedOWLEntityAction;
import org.semanticweb.owlapi.model.OWLEntity;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

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
