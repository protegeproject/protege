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
 * Launches packaged desktop entry points and fails if their output contains
 * {@code Exception} (startup regression guard). OS-specific launchers run only
 * on the matching OS.
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
    public void platformIndependentRunShShouldNotPrintException() throws Exception {
        Assume.assumeTrue("Unix shell only", !isWindows());
        File script = new File(findProtegeDistributionRoot("platform-independent"), "run.sh");
        assertScriptLauncherOutputHasNoException(script, "platform-independent run.sh");
    }

    @Test
    public void platformIndependentRunCommandShouldNotPrintException() throws Exception {
        Assume.assumeTrue("macOS only", isMacOs());
        File script = new File(findProtegeDistributionRoot("platform-independent"), "run.command");
        assertScriptLauncherOutputHasNoException(script, "platform-independent run.command");
    }

    @Test
    public void platformIndependentRunBatShouldNotPrintException() throws Exception {
        Assume.assumeTrue("Windows only", isWindows());
        File script = new File(findProtegeDistributionRoot("platform-independent"), "run.bat");
        assertScriptLauncherOutputHasNoException(script, "platform-independent run.bat");
    }

    @Test
    public void macNativeLauncherShouldNotPrintException() throws Exception {
        Assume.assumeTrue("macOS only", isMacOs());
        File launcher = new File(findProtegeDistributionRoot("mac"),
                "Prot\u00e9g\u00e9.app/Contents/MacOS/protege");
        assertNativeLauncherOutputHasNoException(launcher, "mac native launcher");
    }

    @Test
    public void linuxRunShShouldNotPrintException() throws Exception {
        Assume.assumeTrue("Linux only", isLinux());
        File script = new File(findProtegeDistributionRoot("linux"), "run.sh");
        assertScriptLauncherOutputHasNoException(script, "linux run.sh");
    }

    @Test
    public void linuxNativeLauncherShouldNotPrintException() throws Exception {
        Assume.assumeTrue("Linux only", isLinux());
        File launcher = new File(findProtegeDistributionRoot("linux"), "protege");
        assertNativeLauncherOutputHasNoException(launcher, "linux native launcher");
    }

    @Test
    public void winRunBatShouldNotPrintException() throws Exception {
        Assume.assumeTrue("Windows only", isWindows());
        File script = new File(findProtegeDistributionRoot("win"), "run.bat");
        assertScriptLauncherOutputHasNoException(script, "win run.bat");
    }

    @Test
    public void winNativeLauncherShouldNotPrintException() throws Exception {
        Assume.assumeTrue("Windows only", isWindows());
        File launcher = new File(findProtegeDistributionRoot("win"), "Protege.exe");
        assertNativeLauncherOutputHasNoException(launcher, "win native launcher");
    }

    private static void assertScriptLauncherOutputHasNoException(File script, String label) throws Exception {
        ProcessBuilder processBuilder = isWindows()
                ? new ProcessBuilder("cmd.exe", "/c", script.getAbsolutePath())
                : new ProcessBuilder("/bin/bash", script.getAbsolutePath());
        processBuilder.directory(script.getParentFile());
        assertLauncherOutputHasNoException(script, label, processBuilder, null);
    }

    private static void assertNativeLauncherOutputHasNoException(File launcher, String label) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(launcher.getAbsolutePath());
        processBuilder.directory(launcher.getParentFile());
        assertLauncherOutputHasNoException(launcher, label, processBuilder, nativeJvmConfFile(launcher));
    }

    private static void assertLauncherOutputHasNoException(File launcher,
                                                           String label,
                                                           ProcessBuilder processBuilder,
                                                           File jvmConfFile) throws Exception {
        assertTrue(label + " must exist: " + launcher.getAbsolutePath(), launcher.isFile());
        if (!isWindows()) {
            assertTrue(label + " must be executable: " + launcher.getAbsolutePath(), launcher.canExecute());
        }

        processBuilder.redirectErrorStream(true);
        File testHome = createLauncherTestHome();
        File closeSignalFile = new File(testHome, "close-window.signal");
        boolean jvmConfExisted = jvmConfFile != null && jvmConfFile.isFile();
        byte[] originalJvmConf = jvmConfExisted ? Files.readAllBytes(jvmConfFile.toPath()) : new byte[0];
        configureGuiTestAgent(processBuilder, testHome, closeSignalFile, jvmConfFile);
        Process process = null;
        Thread reader = null;
        StringBuffer output = new StringBuffer(16_384);
        String processOutput = "";
        boolean startupObserved = false;
        boolean stopped = false;
        try {
            process = processBuilder.start();
            Process launchedProcess = process;

            reader = new Thread(() -> {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(launchedProcess.getInputStream(), StandardCharsets.UTF_8))) {
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

            startupObserved = waitForStartupMarker(process, output, testHome, STARTUP_WINDOW);
            if (startupObserved) {
                Files.write(closeSignalFile.toPath(), Arrays.asList("ready"), StandardCharsets.UTF_8);
                stopped = process.waitFor(SHUTDOWN_WINDOW.getSeconds(), TimeUnit.SECONDS);
            }
        } finally {
            if (process != null && process.isAlive()) {
                stopProcessTree(process);
            }
            if (reader != null) {
                reader.join(5_000);
            }
            processOutput = combinedOutput(output, testHome);
            restoreJvmConf(jvmConfFile, jvmConfExisted, originalJvmConf);
            deleteRecursively(testHome);
        }

        assertTrue(label + " did not reach startup marker within " + STARTUP_WINDOW.getSeconds()
                        + "s. Output:\n\n" + processOutput,
                startupObserved);
        assertTrue(label + " did not stop after its window was closed within " + SHUTDOWN_WINDOW.getSeconds()
                        + "s. Output:\n\n" + processOutput,
                stopped);
        assertNotNull(process);
        assertEquals(label + " did not exit cleanly after its window was closed.\n\n" + processOutput,
                0, process.exitValue());
        assertTrue(label + " did not observe the Protege window via the GUI test agent."
                        + "\n\nOutput:\n\n" + processOutput,
                processOutput.contains(ProtegeGuiTestAgent.WINDOW_READY_MESSAGE));
        assertTrue(label + " did not request the Protege window close via the GUI test agent."
                        + "\n\nOutput:\n\n" + processOutput,
                processOutput.contains(ProtegeGuiTestAgent.CLOSE_REQUESTED_MESSAGE));
        assertTrue(label + " did not run the normal shutdown sequence.\n\n" + processOutput,
                processOutput.contains(SHUTDOWN_BANNER));
        assertTrue(label + " did not run the normal cleanup.\n\n" + processOutput,
                processOutput.contains(CLEANUP_MARKER));
        assertFalse(
                label + " output contains an exception (startup regression).\n\n" + processOutput,
                processOutput.contains("Exception"));
    }

    private static void configureGuiTestAgent(ProcessBuilder processBuilder,
                                              File testHome,
                                              File closeSignalFile,
                                              File jvmConfFile) throws IOException {
        processBuilder.environment().put("HOME", testHome.getAbsolutePath());
        processBuilder.environment().put("USERPROFILE", testHome.getAbsolutePath());
        List<String> options = createGuiTestAgentOptions(testHome, closeSignalFile);
        if (jvmConfFile == null) {
            processBuilder.environment().put("CMD_OPTIONS", String.join(" ", options));
        } else {
            writeJvmConf(jvmConfFile, options);
        }
    }

    private static File nativeJvmConfFile(File launcher) {
        File launcherDir = launcher.getParentFile();
        if (isMacOs()) {
            File contentsDir = launcherDir.getParentFile();
            return new File(new File(contentsDir, "conf"), "jvm.conf");
        }
        return new File(new File(launcherDir, "conf"), "jvm.conf");
    }

    private static boolean waitForStartupMarker(Process process,
                                                StringBuffer output,
                                                File testHome,
                                                Duration timeout)
            throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            if (containsStartupMarker(combinedOutput(output, testHome))) {
                return true;
            }
            if (!process.isAlive()) {
                return containsStartupMarker(combinedOutput(output, testHome));
            }
            Thread.sleep(200);
        }
        return containsStartupMarker(combinedOutput(output, testHome));
    }

    private static File createLauncherTestHome() throws IOException {
        File testHomesDir = new File("target/launcher-test-homes");
        assertTrue("Could not create launcher test homes directory: " + testHomesDir.getAbsolutePath(),
                testHomesDir.isDirectory() || testHomesDir.mkdirs());
        File testHome = Files.createTempDirectory(testHomesDir.toPath(), "home-").toFile();
        File confDir = new File(new File(testHome, ".Protege"), "conf");
        assertTrue("Could not create launcher test configuration directory: " + confDir.getAbsolutePath(),
                confDir.mkdirs());
        return testHome;
    }

    private static List<String> createGuiTestAgentOptions(File testHome,
                                                          File closeSignalFile) throws IOException {
        File agentJar = createGuiTestAgentJar();
        List<String> options = new ArrayList<>();
        options.add("-Duser.home=" + testHome.getAbsolutePath());
        options.add("-D" + ProtegeGuiTestAgent.READY_SIGNAL_FILE_PROPERTY + "=" + closeSignalFile.getAbsolutePath());
        options.add("-D" + ProtegeGuiTestAgent.CLOSE_TIMEOUT_SECONDS_PROPERTY + "=" + SHUTDOWN_WINDOW.getSeconds());
        options.add("-javaagent:" + agentJar.getAbsolutePath());
        return options;
    }

    private static void writeJvmConf(File confFile, List<String> options) throws IOException {
        File parent = confFile.getParentFile();
        assertTrue("Could not create launcher JVM configuration directory: " + parent.getAbsolutePath(),
                parent.isDirectory() || parent.mkdirs());
        List<String> jvmConfOptions = new ArrayList<>();
        for (String option : options) {
            jvmConfOptions.add("append=" + option);
        }
        Files.write(confFile.toPath(), jvmConfOptions, StandardCharsets.UTF_8);
    }

    private static void restoreJvmConf(File confFile, boolean existed, byte[] content) throws IOException {
        if (confFile == null) {
            return;
        }
        if (existed) {
            Files.write(confFile.toPath(), content);
        } else if (confFile.isFile() && !confFile.delete()) {
            confFile.deleteOnExit();
        }
    }

    private static String combinedOutput(StringBuffer output, File testHome) {
        String protegeLog = readProtegeLog(testHome);
        if (protegeLog.isEmpty()) {
            return output.toString();
        }
        return output.toString()
                + System.lineSeparator()
                + "----- protege.log -----"
                + System.lineSeparator()
                + protegeLog;
    }

    private static String readProtegeLog(File testHome) {
        return readFile(new File(new File(new File(testHome, ".Protege"), "logs"), "protege.log"));
    }

    private static String readFile(File file) {
        if (!file.isFile()) {
            return "";
        }
        try {
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "Could not read " + file.getAbsolutePath() + ": " + e.getMessage();
        }
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
