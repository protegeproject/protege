package org.protege.editor.owl.ui.util;

import static javax.swing.JOptionPane.WARNING_MESSAGE;

import java.awt.Window;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.protege.editor.owl.model.util.LowMemoryNotificationView;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-12
 */
public class LowMemoryNotificationViewImpl implements LowMemoryNotificationView {

    private static final String TITLE = "Low Memory";

    private static final String MSG = "<html><body><b>The memory available to Protégé is running low<br><br>Free memory: %d MB</b>&nbsp;&nbsp;&nbsp;&nbsp;(Memory limit: %d MB)<br><br>This may cause Protégé to freeze and become unresponsive</body></html>";

    public static void main(String[] args) {
        LowMemoryNotificationViewImpl i = new LowMemoryNotificationViewImpl();
        i.displayLowMemoryNotification();
    }

    /**
     * Display a low memory notification.  This method will only be called
     * in the Event Dispatch Thread.
     */
    @Override
    public void displayLowMemoryNotification() {
        Window parent = Stream
                .of(Window.getOwnerlessWindows())
                .filter(w -> w instanceof JFrame)
                .findFirst()
                .orElse(null);
        Runtime runtime = Runtime.getRuntime();
        long availableMemory = runtime.maxMemory() - runtime.totalMemory();
        long availableMemoryInMb = availableMemory / 1024 / 1024;
        long maxMemory = runtime.maxMemory();
        long maxMemoryInMb = maxMemory / 1024 / 1024;
        String formattedMsg = String.format(MSG, availableMemoryInMb, maxMemoryInMb);
        JOptionPane.showMessageDialog(parent, formattedMsg, TITLE, WARNING_MESSAGE);
    }
}
