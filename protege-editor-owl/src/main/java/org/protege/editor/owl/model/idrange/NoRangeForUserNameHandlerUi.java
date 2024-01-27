package org.protege.editor.owl.model.idrange;


import java.awt.Frame;
import java.util.Arrays;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.swing.JOptionPane;

import org.protege.editor.core.ui.util.JOptionPaneEx;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-25
 */
public class NoRangeForUserNameHandlerUi implements NoRangeForUserNameHandler {

    private static final String UPDATE = "Use Selected User";

    private static final String CANCEL = "Cancel";

    @Override
    public void handleNoRangeForUserName(@Nonnull String userName,
                                         @Nonnull IdRangesPolicy policy,
                                         @Nonnull Consumer<? super UserIdRange> action) {
        Frame owner = Arrays.stream(Frame.getFrames()).findFirst().orElse(null);
        NoRangeFoundForUserNamePanel userNamePanel = new NoRangeFoundForUserNamePanel();
        userNamePanel.setUserName(userName);
        userNamePanel.setIdRangesPolicy(policy);

        Object[] options = {UPDATE, CANCEL};
        int ret = JOptionPaneEx.showConfirmDialog(owner,
                                                  "No Id Range Found For Current User",
                                                  userNamePanel,
                                                  JOptionPane.PLAIN_MESSAGE,
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  userNamePanel, options,
                                                  UPDATE);
        if(ret != 0) {
            return;
        }
        userNamePanel.getSelectedRange().ifPresent(action);
    }
}
