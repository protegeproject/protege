package org.protege.editor.owl.ui.library;

import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.protege.xmlcatalog.XMLCatalog;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 24-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class EditActiveOntologyLibraryAction extends ProtegeOWLAction {
    private static final long serialVersionUID = -4297673435512878237L;


    public void actionPerformed(ActionEvent e) {
		try {
			OntologyCatalogManager catalogManager = getOWLModelManager().getOntologyCatalogManager();
			XMLCatalog activeCatalog = catalogManager.getActiveCatalog();
			if (activeCatalog == null) {
                return;
            }
			File catalogFile = OntologyCatalogManager.getCatalogFile(activeCatalog);
			if (!catalogFile.exists()) {
                return;
            }
			OntologyLibraryPanel.showDialog(getOWLEditorKit(), catalogFile);
		} catch (IOException ex) {
			LoggerFactory.getLogger(EditActiveOntologyLibraryAction.class)
					.error("An error occurred whilst attempting to edit the active ontology library: {}", ex);
		}

	}


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
