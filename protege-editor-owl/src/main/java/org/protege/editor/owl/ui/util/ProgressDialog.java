package org.protege.editor.owl.ui.util;

import javax.swing.*;
import java.awt.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 11 May 16
 */
public class ProgressDialog {

    private final JDialog dlg = new JDialog((JFrame) null, "", true);

    private final ProgressView view = new ProgressViewImpl();

    /**
     * Constructs a ProgressDialog.  A ProgressDialog contains a message and an optional sub-message.
     */
    public ProgressDialog() {
        dlg.setUndecorated(true);
        dlg.getContentPane().setLayout(new BorderLayout());
        dlg.getContentPane().add(view.asJComponent(), BorderLayout.NORTH);
    }

    /**
     * Sets the visibility of the progress dialog.  Note that the progress dialog is modal - it will block the
     * event dispatch thread when it is shown.
     * The dialog will be packed and positioned before it is made visible.
     * Note that this method may be called from a thread other than the event dispatch thread.  The implementation
     * will check to see whether the calling thread is the event dispatch thread or not and, if necessary, will
     * use SwingUtilities.invoke later.
     * @param visible true if the dialog should be made visible, or false if the dialog should be hidden.
     */
    public void setVisible(boolean visible) {
        Runnable r = () -> {
            if (visible) {
                dlg.pack();
                Dimension prefSize = dlg.getPreferredSize();
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Dimension screenSize = toolkit.getScreenSize();
                dlg.setLocation(
                        (screenSize.width - prefSize.width) / 2,
                        (screenSize.height - prefSize.height) / 2);
            }
            dlg.setVisible(visible);
        };

        if(SwingUtilities.isEventDispatchThread()) {
            r.run();
        }
        else {
            SwingUtilities.invokeLater(r);
        }
    }

    /**
     * Sets the message on the progress dialog
     * Note that this method may be called from a thread other than the event dispatch thread.  The implementation
     * will check to see whether the calling thread is the event dispatch thread or not and, if necessary, will
     * use SwingUtilities.invoke later.
     * @param message  The message.  Not {@code null}.
     */
    public void setMessage(String message) {
        checkNotNull(message);
        Runnable r = () -> view.setMessage(message);
        if(SwingUtilities.isEventDispatchThread()) {
            r.run();
        }
        else {
            SwingUtilities.invokeLater(r);
        }
    }

    /**
     * Sets the sub-message of the progress dialog.
     * Note that this method may be called from a thread other than the event dispatch thread.  The implementation
     * will check to see whether the calling thread is the event dispatch thread or not and, if necessary, will
     * use SwingUtilities.invoke later.
     * @param subMessage  The sub-message.  Not {@code null}.
     */
    public void setSubMessage(String subMessage) {
        checkNotNull(subMessage);
        Runnable r = () -> view.setSubMessage(subMessage);
        if(SwingUtilities.isEventDispatchThread()) {
            r.run();
        }
        else {
            SwingUtilities.invokeLater(r);
        }
    }

    /**
     * Clears the sub-message of the progress dialog.
     * Note that this method may be called from a thread other than the event dispatch thread.  The implementation
     * will check to see whether the calling thread is the event dispatch thread or not and, if necessary, will
     * use SwingUtilities.invoke later.
     */
    public void clearSubMessage() {
        Runnable r = view::clearSubMessage;
        if(SwingUtilities.isEventDispatchThread()) {
            r.run();
        }
        else {
            SwingUtilities.invokeLater(r);
        }
    }

}
