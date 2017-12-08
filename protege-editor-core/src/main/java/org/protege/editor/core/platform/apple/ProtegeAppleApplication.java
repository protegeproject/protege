package org.protege.editor.core.platform.apple;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.ui.about.AboutPanel;
import org.protege.editor.core.ui.preferences.PreferencesDialogPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.io.File;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 19, 2008<br><br>
 */
public class ProtegeAppleApplication {

    private static final Logger logger = LoggerFactory.getLogger(ProtegeAppleApplication.class);

    private EditorKit eKit;

    private static ProtegeAppleApplication instance;


    public static ProtegeAppleApplication getInstance(){
        if (instance == null){
            instance = new ProtegeAppleApplication();
        }
        return instance;
    }


	private ProtegeAppleApplication() {
		Desktop application = Desktop.getDesktop();
		application.setPreferencesHandler(event -> handlePreferencesRequest());
		application.setAboutHandler(event -> handleAboutRequest());
		application.setOpenFileHandler(event -> {
			File file = event.getFiles().get(0);
			try {
				editFile(file.getAbsolutePath());
			} catch (Exception e) {
				logger.error("invalid file: {}", file);
			}
		});
		application.setQuitHandler((event, response) -> handleQuitRequest());
	}


    public void setEditorKit(EditorKit eKit){
        this.eKit = eKit;
    }
    
    protected void editFile(String fileName) throws Exception {    	    
        ProtegeManager.getInstance().getApplication().editURI(new File(fileName).toURI());
    }


    protected boolean handlePreferencesRequest() {
        if (eKit != null){
            PreferencesDialogPanel.showPreferencesDialog(null, eKit);
            return true;
        }
        return false;
    }


    protected boolean handleAboutRequest() {
        AboutPanel.showDialog();
        return true;
    }


    protected boolean handleQuitRequest() {
        return ProtegeApplication.handleQuit();
    }
}
