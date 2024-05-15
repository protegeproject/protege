package org.protege.editor.core.platform.apple;

import org.junit.Before;
import org.junit.Test;

import javax.swing.*;

import java.awt.*;
import java.util.Collections;

import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 22 Aug 16
 */
public class MacUIUtil_TestCase {

    private Window parent;

    @Before
    public void setUp() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        parent = new JFrame();
    }


    @Test
    public void shouldAcceptNullExtensionsArg() {
        try {
            showDialog(() -> MacUIUtil.saveFile(parent, "Title", null, "Initial name"));

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldAcceptNullInitialName() {
        try {
            showDialog(() -> MacUIUtil.saveFile(parent, "Title", Collections.emptySet(), null));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldAcceptNullTitle() {
        try {
            showDialog(() -> MacUIUtil.saveFile(parent, null, Collections.emptySet(), "Initial name"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    private void showDialog(Runnable r) {
        SwingUtilities.invokeLater(() -> {
            try {
                r.run();
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });
        waitForFileDialog();
    }

    private void waitForFileDialog() {
        try {
            // Time out after 5 seconds
            for(int i = 0; i < 10; i++) {
                Window[] windows = Window.getWindows();
                if (windows != null && windows.length > 0) {
                    for(Window w : windows) {
                        if(w instanceof FileDialog) {
                            // Give the file dialog a chance to be shown
                            Thread.sleep(500);
                            return;
                        }
                    }
                }
                Thread.sleep(500);
            }
            fail("Dialog not shown");
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }
}
