package org.protege.editor.owl.ui.library;

import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 24-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class EditOntologyLibraryAction extends ProtegeOWLAction {
    private static final long serialVersionUID = -4297673435512878237L;


    public void actionPerformed(ActionEvent e) {
    	try {
    		File catalogFile = UIUtil.openFile(getOWLWorkspace(), "Choose catalog file containing ontology repository information", "Choose XML Catalog", Collections.singleton("xml"));
    		if (catalogFile != null) {
    			OntologyLibraryPanel.showDialog(getOWLEditorKit(), catalogFile);
    		}
    	}
    	catch (IOException ex) {
			LoggerFactory.getLogger(EditActiveOntologyLibraryAction.class)
					.error("An error occurred whilst attempting to edit the active ontology library: {}", e);
    	}
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
