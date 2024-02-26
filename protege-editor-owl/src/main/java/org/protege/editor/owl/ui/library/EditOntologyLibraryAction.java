package org.protege.editor.owl.ui.library;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.core.ui.workspace.WorkspaceFrame;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.slf4j.LoggerFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 24-Aug-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class EditOntologyLibraryAction extends ProtegeOWLAction {


    public void actionPerformed(ActionEvent e) {
    	try {
			WorkspaceFrame frame = ProtegeManager.getInstance().getFrame(getOWLWorkspace());
			File catalogFile = UIUtil.openFile(
					frame,
					"Select XML Catalog File",
					"Choose the XML file that contains the catalog information",
					Collections.singleton("xml"));
    		if (catalogFile != null) {
    			OntologyLibraryPanel.showDialog(getOWLEditorKit(), catalogFile);
    		}
    	}
    	catch (IOException ex) {
			LoggerFactory.getLogger(EditOntologyLibraryAction.class)
					.error("An error occurred whilst attempting to edit the catalog file: {}", e);
    	}
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
