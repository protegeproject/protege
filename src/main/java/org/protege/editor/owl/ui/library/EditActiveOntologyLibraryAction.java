package org.protege.editor.owl.ui.library;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collections;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.protege.xmlcatalog.XMLCatalog;


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
    	}
    	catch (Exception ex) {
    		ProtegeApplication.getErrorLog().logError(ex);
    	}
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
