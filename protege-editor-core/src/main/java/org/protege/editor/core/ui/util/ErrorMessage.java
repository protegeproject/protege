package org.protege.editor.core.ui.util;

import org.protege.editor.core.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 12/11/15
 */
public class ErrorMessage {

    public static final String VIEW_LOG = "View Log";

    public static final String OK = "OK";

    private static final Logger logger = LoggerFactory.getLogger(ErrorMessage.class);

    public static void showErrorMessage(String title, String message) {
        int ret = JOptionPane.showOptionDialog(
                null,
                "<html><body>" +
                        "<div style=\"font-weight: bold; padding-bottom: 10px;\">" +
                        message +
                        "</div>" +
                        "<div>" +
                        "Please check the protege.log file in the logs directory for details." +
                        "</div>" +
                        "</body></html>",
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                new Object[]{VIEW_LOG, OK},
                OK
        );
        if(ret != 0) {
            return;
        }
        FileUtils.showLogFile();
    }
}
