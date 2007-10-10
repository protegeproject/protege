package org.protege.editor.owl.ui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.List;

import org.protege.editor.owl.ui.transfer.TransferableOWLObject;
import org.protege.editor.owl.ui.view.Cuttable;
import org.protege.editor.owl.ui.view.ViewClipboard;
import org.semanticweb.owl.model.OWLObject;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 09-May-2007<br><br>
 */
public class CutAction extends FocusedComponentAction<Cuttable> {

    protected Class<Cuttable> initialiseAction() {
        return Cuttable.class;
    }


    protected boolean canPerform() {
        return getCurrentTarget().canCut();
    }


    public void actionPerformed(ActionEvent e) {
        List<OWLObject> objects = getCurrentTarget().cutObjects();
        if (objects.isEmpty()) {
            // Shouldn't really happen, but just in case
            return;
        }
        // Push the objects on to the clip board
        ViewClipboard clipboard = ViewClipboard.getInstance();
        clipboard.getClipboard().setContents(new TransferableOWLObject(getOWLModelManager(), objects), null);

        new TransferableOWLObject(getOWLModelManager(), objects);

        StringBuilder buffer = new StringBuilder();
        for (OWLObject owlObject : objects) {
            buffer.append(getOWLModelManager().getOWLObjectRenderer().render(owlObject,
                                                                             getOWLModelManager().getOWLEntityRenderer()));
            buffer.append("\n");
        }
        StringSelection stringSelection = new StringSelection(buffer.toString().trim());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }
}
