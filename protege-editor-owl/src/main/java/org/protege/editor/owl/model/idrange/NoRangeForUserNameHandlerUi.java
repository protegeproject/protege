package org.protege.editor.owl.model.idrange;


import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.stream.StreamSupport;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-25
 */
public class NoRangeForUserNameHandlerUi implements NoRangeForUserNameHandler {

    @Override
    public void handleNoRangeForUserName(@Nonnull String userName,
                                         @Nonnull IdRangesPolicy policy) {
        Frame owner = Arrays.stream(Frame.getFrames()).findFirst().orElse(null);
        JOptionPane.showMessageDialog(owner,
                                      String.format("<html><body>" +
                                                            "<b>No id range found for %s</b><br><br>" +
                                                            "An id ranges file was found but it does not contain an id range for <br>" +
                                                            "the current user, <b>%s</b>.  You should either update the current user name to<br>" +
                                                            "a name specified in the id file, or you should add an id range for the current<br>" +
                                                            "user name to the id ranges file.</body><html>",
                                                    userName,
                                                    userName),
                                      "No Id Range Found For Current User",
                                      JOptionPane.WARNING_MESSAGE);
    }
}
