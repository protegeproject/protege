package org.protege.editor.core.ui.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.DateFormat;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 25, 2008<br><br>
 */
public class TimestampOutputAction extends ProtegeAction {

    /**
     * 
     */
    private static final long serialVersionUID = -6250513189027502206L;

    private final static Logger LOGGER = LoggerFactory.getLogger(TimestampOutputAction.class);

    public void actionPerformed(ActionEvent event) {
    	    createTimeStamp(getWorkspace());
    }
    
    public static void createTimeStamp(Component parent) {
        String message = JOptionPane.showInputDialog(parent,
                                                     "<html><body>Please enter a message to label your timestamp <br>(or leave blank for no message)</body><html>",
                                                     "Timestamp", JOptionPane.PLAIN_MESSAGE);

        long now = System.currentTimeMillis();
        String timestamp = DateFormat.getDateTimeInstance().format(now);

        if (message != null){

            LOGGER.info("\n\n\n\n");
            LOGGER.info("------------------------------------------");
            LOGGER.info(timestamp + ": " + message);
            LOGGER.info("------------------------------------------");
            LOGGER.info("\n\n");
        }
    }


    public void initialise() throws Exception {
        // do nothing
    }


    public void dispose() throws Exception {
        // do nothing
    }
}
