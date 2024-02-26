package org.protege.editor.owl.model.entity;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Optional;

import org.protege.editor.owl.model.util.OboUtilities;
import org.protege.editor.owl.ui.action.SelectedOWLEntityAction;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Sep 2017
 */
public class CopyOboIdAction extends SelectedOWLEntityAction {

    @Override
    protected void actionPerformed(OWLEntity selectedEntity) {
        Optional<String> oboId = OboUtilities.getOboIdFromIri(selectedEntity.getIRI());
        oboId.ifPresent(id -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(id), null));
        if(!oboId.isPresent()) {
            Toolkit.getDefaultToolkit().beep();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(""), null);
        }
    }

    @Override
    protected void disposeAction() throws Exception {

    }
}
