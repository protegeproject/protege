package org.protege.desktop;

import org.junit.Assume;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
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
    private static final Duration SHUTDOWN_WINDOW = Duration.ofSeconds(35);
    private static final String[] STARTUP_MARKERS = {
            "The OSGi framework has been started",
            "Creating and setting up empty (default) editor kit"
    };
    private static final String SHUTDOWN_BANNER = "----------------------- Shutting down Protege -----------------------";
    private static final String CLEANUP_MARKER = "Cleaning up temporary directories";
    private static final String AUTO_UPDATE_MARKER = "Auto-update ";

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
        File testHome = createLauncherTestHome();
        File closeSignalFile = new File(testHome, "close-window.signal");
        processBuilder.environment().put("HOME", testHome.getAbsolutePath());
        processBuilder.environment().put("USERPROFILE", testHome.getAbsolutePath());
        processBuilder.environment().put("CMD_OPTIONS", createGuiTestAgentOptions(closeSignalFile));
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

        boolean startupObserved = false;
        boolean stopped = false;
        try {
            startupObserved = waitForStartupMarker(process, output, STARTUP_WINDOW);
            if (startupObserved) {
                Files.write(closeSignalFile.toPath(), Arrays.asList("ready"), StandardCharsets.UTF_8);
                stopped = process.waitFor(SHUTDOWN_WINDOW.getSeconds(), TimeUnit.SECONDS);
            }
        } finally {
            if (process.isAlive()) {
                stopProcessTree(process);
            }
            reader.join(5_000);
            deleteRecursively(testHome);
        }

        String processOutput = output.toString();
        assertTrue(label + " did not reach startup marker within " + STARTUP_WINDOW.getSeconds()
                        + "s. Output:\n\n" + processOutput,
                startupObserved);
        assertTrue(label + " did not stop after its window was closed within " + SHUTDOWN_WINDOW.getSeconds()
                        + "s. Output:\n\n" + processOutput,
                stopped);
        assertEquals(label + " did not exit cleanly after its window was closed.\n\n" + processOutput,
                0, process.exitValue());
        assertTrue(label + " did not observe the Protege window via the GUI test agent.\n\n" + processOutput,
                processOutput.contains(ProtegeGuiTestAgent.WINDOW_READY_MESSAGE));
        assertTrue(label + " did not request the Protege window close via the GUI test agent.\n\n" + processOutput,
                processOutput.contains(ProtegeGuiTestAgent.CLOSE_REQUESTED_MESSAGE));
        assertTrue(label + " did not run the normal shutdown sequence.\n\n" + processOutput,
                processOutput.contains(SHUTDOWN_BANNER));
        assertTrue(label + " did not run the normal cleanup.\n\n" + processOutput,
                processOutput.contains(CLEANUP_MARKER));
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

    private static File createLauncherTestHome() throws IOException {
        File testHome = Files.createTempDirectory("protege-launcher-home").toFile();
        File confDir = new File(new File(testHome, ".Protege"), "conf");
        assertTrue("Could not create launcher test configuration directory: " + confDir.getAbsolutePath(),
                confDir.mkdirs());
        return testHome;
    }

    private static String createGuiTestAgentOptions(File closeSignalFile) throws IOException {
        File agentJar = createGuiTestAgentJar();
        return "-D" + ProtegeGuiTestAgent.READY_SIGNAL_FILE_PROPERTY + "=" + closeSignalFile.getAbsolutePath()
                + " -D" + ProtegeGuiTestAgent.CLOSE_TIMEOUT_SECONDS_PROPERTY + "=" + SHUTDOWN_WINDOW.getSeconds()
                + " -javaagent:" + agentJar.getAbsolutePath();
    }

    private static File createGuiTestAgentJar() throws IOException {
        File agentJar = new File("target/gui-test-agent/protege-gui-test-agent.jar").getAbsoluteFile();
        File parent = agentJar.getParentFile();
        assertTrue("Could not create GUI test agent directory: " + parent.getAbsolutePath(),
                parent.isDirectory() || parent.mkdirs());

        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.putValue("Premain-Class", ProtegeGuiTestAgent.class.getName());

        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(agentJar.toPath()), manifest)) {
            for (Class<?> agentClass : ProtegeGuiTestAgent.agentClasses()) {
                addClass(out, agentClass);
            }
        }
        return agentJar;
    }

    private static void addClass(JarOutputStream out, Class<?> type) throws IOException {
        String path = type.getName().replace('.', '/') + ".class";
        JarEntry entry = new JarEntry(path);
        out.putNextEntry(entry);
        try (InputStream in = type.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IOException("Could not find compiled test class: " + path);
            }
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
        out.closeEntry();
    }

    private static boolean containsStartupMarker(String output) {
        for (String marker : STARTUP_MARKERS) {
            if (!output.contains(marker)) {
                return false;
            }
        }
        return output.contains(AUTO_UPDATE_MARKER);
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

    private static void deleteRecursively(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteRecursively(child);
                }
            }
        }
        if (!file.delete()) {
            file.deleteOnExit();
        }
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
