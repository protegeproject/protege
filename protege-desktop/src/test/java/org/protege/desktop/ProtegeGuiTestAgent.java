package org.protege.desktop;

import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.instrument.Instrumentation;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Test-only Java agent used by launcher integration tests. It runs inside the
 * launched GUI process so the test can close the real Swing window without
 * adding test hooks to production launcher code.
 */
public class ProtegeGuiTestAgent {

    static final String READY_SIGNAL_FILE_PROPERTY = "org.protege.desktop.guiTest.readySignalFile";

    static final String CLOSE_TIMEOUT_SECONDS_PROPERTY = "org.protege.desktop.guiTest.closeTimeoutSeconds";

    static final String WINDOW_READY_MESSAGE = "GUI test agent observed Protege window ready";

    static final String CLOSE_REQUESTED_MESSAGE = "GUI test agent requested Protege window close";

    private static final Duration DEFAULT_CLOSE_TIMEOUT = Duration.ofSeconds(60);

    private static final Duration GUI_SETTLE_TIME = Duration.ofSeconds(1);

    private ProtegeGuiTestAgent() {
    }

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        premain(agentArgs);
    }

    public static void premain(String agentArgs) {
        Thread thread = new Thread(ProtegeGuiTestAgent::runGuiClose, "Protege GUI test agent");
        thread.setDaemon(true);
        thread.start();
    }

    static List<Class<?>> agentClasses() {
        return Arrays.asList(
                ProtegeGuiTestAgent.class
        );
    }

    private static void runGuiClose() {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("GUI test agent cannot close windows in a headless environment");
            return;
        }

        Duration timeout = closeTimeout();
        File readySignalFile = readySignalFile();
        if (readySignalFile == null || !waitForReadySignal(readySignalFile, timeout)) {
            return;
        }

        try {
            Frame frame = waitForProtegeFrame(timeout);
            if (frame == null) {
                System.out.println("GUI test agent timed out waiting for the Protege window");
                return;
            }
            waitForIdle();
            sleep(GUI_SETTLE_TIME);
            waitForIdle();
            closeVisibleDialogs();
            System.out.println(WINDOW_READY_MESSAGE + ": " + describe(frame));
            EventQueue.invokeAndWait(() ->
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));
            System.out.println(CLOSE_REQUESTED_MESSAGE + ": " + describe(frame));
            waitForIdle();
            closeVisibleDialogs();
        } catch (Throwable t) {
            t.printStackTrace(System.out);
        }
    }

    private static File readySignalFile() {
        String path = System.getProperty(READY_SIGNAL_FILE_PROPERTY);
        if (path == null || path.trim().isEmpty()) {
            System.out.println("GUI test agent did not receive a ready signal file");
            return null;
        }
        return new File(path);
    }

    private static boolean waitForReadySignal(File readySignalFile, Duration timeout) {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            if (readySignalFile.isFile()) {
                return true;
            }
            sleep(Duration.ofMillis(200));
        }
        System.out.println("GUI test agent timed out waiting for " + readySignalFile.getAbsolutePath());
        return false;
    }

    private static Frame waitForProtegeFrame(Duration timeout) {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            Frame frame = findProtegeFrame();
            if (frame != null) {
                return frame;
            }
            sleep(Duration.ofMillis(200));
        }
        return findProtegeFrame();
    }

    private static Frame findProtegeFrame() {
        for (Window window : Window.getWindows()) {
            if (window instanceof Frame && window.isShowing() && isProtegeFrame((Frame) window)) {
                return (Frame) window;
            }
        }
        return null;
    }

    private static boolean isProtegeFrame(Frame frame) {
        String title = normalize(frame.getTitle());
        String className = normalize(frame.getClass().getName());
        return title.contains("protege")
                || title.contains("protégé")
                || className.contains("protege");
    }

    private static void closeVisibleDialogs() throws Exception {
        for (Window window : Window.getWindows()) {
            if (!(window instanceof Dialog) || !window.isShowing()) {
                continue;
            }
            Dialog dialog = (Dialog) window;
            System.out.println("GUI test agent closing dialog: " + describe(dialog));
            EventQueue.invokeAndWait(() ->
                    dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING)));
            waitForIdle();
        }
    }

    private static Duration closeTimeout() {
        long seconds = Long.getLong(CLOSE_TIMEOUT_SECONDS_PROPERTY, DEFAULT_CLOSE_TIMEOUT.getSeconds());
        return Duration.ofSeconds(Math.max(1, seconds));
    }

    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void waitForIdle() throws Exception {
        EventQueue.invokeAndWait(() -> {
        });
    }

    private static String describe(Window window) {
        if (window instanceof Frame) {
            String title = ((Frame) window).getTitle();
            if (title != null && !title.isEmpty()) {
                return title;
            }
        }
        if (window instanceof Dialog) {
            String title = ((Dialog) window).getTitle();
            if (title != null && !title.isEmpty()) {
                return title;
            }
        }
        return window.getClass().getName();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase().replace('\u00e9', 'e');
    }
}
