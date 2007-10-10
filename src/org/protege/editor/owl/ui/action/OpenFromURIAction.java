package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.net.URI;

import org.protege.editor.core.ui.OpenFromURIPanel;
import org.semanticweb.owl.model.OWLRuntimeException;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Dec-2006<br><br>
 */
public class OpenFromURIAction extends ProtegeOWLAction {

    public void actionPerformed(ActionEvent e) {
        URI uri = OpenFromURIPanel.showDialog();
        if (uri != null) {
            try {
                getOWLEditorKit().handleLoadFrom(uri);
            }
            catch (Exception e1) {
                throw new OWLRuntimeException(e1);
            }
        }
    }


    public void dispose() {
    }


    public void initialise() throws Exception {
    }
}
