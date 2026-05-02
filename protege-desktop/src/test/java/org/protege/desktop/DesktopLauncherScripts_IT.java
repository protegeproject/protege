package org.protege.desktop;

import org.junit.Assume;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Launches packaged desktop entry points and fails if stdout/stderr contains
 * {@code Exception} (startup regression guard). OS-specific scripts run only on
 * the matching OS.
 */
public class DesktopLauncherScripts_IT {

    private static final Duration STARTUP_WINDOW = Duration.ofSeconds(35);
    private static final String[] STARTUP_MARKERS = {
            "The OSGi framework has been started",
            "Creating and setting up empty (default) editor kit"
    };

    @Test
    public void platformIndependentLauncherShouldNotPrintException() throws Exception {
        File distRoot = findProtegeDistributionRoot("platform-independent");
        File script = isMacOs()
                ? new File(distRoot, "run.command")
                : new File(distRoot, isWindows() ? "run.bat" : "run.sh");
        assertLauncherOutputHasNoException(script, "platform-independent launcher");
    }

    @Test
    public void macRunCommandShouldNotPrintException() throws Exception {
        Assume.assumeTrue("macOS only", isMacOs());
        File script = new File(findProtegeDistributionRoot("platform-independent"), "run.command");
        assertLauncherOutputHasNoException(script, "run.command");
    }

    @Test
    public void linuxRunShShouldNotPrintException() throws Exception {
        Assume.assumeTrue("Linux only", isLinux());
        File script = new File(findProtegeDistributionRoot("linux"), "run.sh");
        assertLauncherOutputHasNoException(script, "linux run.sh");
    }

    @Test
    public void winRunBatShouldNotPrintException() throws Exception {
        Assume.assumeTrue("Windows only", isWindows());
        File script = new File(findProtegeDistributionRoot("win"), "run.bat");
        assertLauncherOutputHasNoException(script, "win run.bat");
    }

    private static void assertLauncherOutputHasNoException(File script, String label) throws Exception {
        assertTrue(label + " must exist: " + script.getAbsolutePath(), script.isFile());
        if (!isWindows()) {
            assertTrue(label + " must be executable: " + script.getAbsolutePath(), script.canExecute());
        }

        ProcessBuilder processBuilder = isWindows()
                ? new ProcessBuilder("cmd.exe", "/c", script.getAbsolutePath())
                : new ProcessBuilder("/bin/bash", script.getAbsolutePath());
        processBuilder.directory(script.getParentFile());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        StringBuffer output = new StringBuffer(16_384);
        Thread reader = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            } catch (IOException ignored) {
                // Process may terminate while reading stream.
            }
        }, "protege-launcher-output-" + label.replace(' ', '-'));
        reader.setDaemon(true);
        reader.start();

        boolean startupObserved = waitForStartupMarker(process, output, STARTUP_WINDOW);
        stopProcessTree(process);
        reader.join(5_000);

        String processOutput = output.toString();
        assertTrue(label + " did not reach startup marker within " + STARTUP_WINDOW.getSeconds()
                        + "s. Output:\n\n" + processOutput,
                startupObserved);
        assertFalse(
                label + " output contains an exception (startup regression).\n\n" + processOutput,
                processOutput.contains("Exception"));
    }

    private static boolean waitForStartupMarker(Process process, StringBuffer output, Duration timeout)
            throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            if (containsStartupMarker(output.toString())) {
                return true;
            }
            if (!process.isAlive()) {
                return containsStartupMarker(output.toString());
            }
            Thread.sleep(200);
        }
        return containsStartupMarker(output.toString());
    }

    private static boolean containsStartupMarker(String output) {
        for (String marker : STARTUP_MARKERS) {
            if (output.contains(marker)) {
                return true;
            }
        }
        return false;
    }

    private static void stopProcessTree(Process process) throws InterruptedException {
        ProcessHandle root = process.toHandle();

        if (isWindows()) {
            try {
                new ProcessBuilder("taskkill", "/F", "/T", "/PID", String.valueOf(root.pid()))
                        .redirectErrorStream(true)
                        .start()
                        .waitFor(10, TimeUnit.SECONDS);
            } catch (IOException ignored) {
                // Fall back to normal process-handle termination below.
            }
        }

        List<ProcessHandle> descendants = new ArrayList<>();
        root.descendants().forEach(descendants::add);
        // Kill children first, then parent to avoid orphan GUI windows.
        for (ProcessHandle child : descendants) {
            child.destroyForcibly();
        }
        root.destroyForcibly();

        process.waitFor(10, TimeUnit.SECONDS);
    }

    private static File findProtegeDistributionRoot(String assemblySuffix) {
        File targetDir = new File("target");
        File[] assemblyDirs = targetDir.listFiles(file ->
                file.isDirectory()
                        && file.getName().startsWith("protege-")
                        && file.getName().endsWith("-" + assemblySuffix));
        assertNotNull("Unable to read target directory: " + targetDir.getAbsolutePath(), assemblyDirs);
        assertTrue("No *-" + assemblySuffix + " distribution in target/. Run mvn package on protege-desktop first.",
                assemblyDirs.length > 0);

        Arrays.sort(assemblyDirs, Comparator.comparing(File::getName).reversed());
        File assemblyDir = assemblyDirs[0];

        File[] protegeRoots = assemblyDir.listFiles(file ->
                file.isDirectory() && file.getName().startsWith("Protege-"));
        assertNotNull("Unable to read distribution directory: " + assemblyDir.getAbsolutePath(), protegeRoots);
        assertTrue("No Protege-* directory under " + assemblyDir.getAbsolutePath(), protegeRoots.length > 0);

        Arrays.sort(protegeRoots, Comparator.comparing(File::getName).reversed());
        return protegeRoots[0];
    }

    private static boolean isMacOs() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("mac");
    }

    private static boolean isLinux() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        return os.contains("linux") || os.contains("nix");
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
    }
}
