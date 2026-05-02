package org.protege.editor.owl;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static org.junit.Assert.*;

/**
 * Integration test that validates the generated OSGi bundle manifest does not
 * import packages that are unavailable at runtime. Runs during the verify phase
 * (after the bundle JAR has been built) via the maven-failsafe-plugin.
 */
public class OsgiBundleManifest_IT {

    @Test
    public void bundleShouldNotImportCompileTimeOnlyAnnotationPackages() throws IOException {
        String jarPath = System.getProperty("bundle.jar");
        assertNotNull("System property 'bundle.jar' must be set by maven-failsafe-plugin", jarPath);

        File jarFile = new File(jarPath);
        assertTrue("Bundle JAR must exist at: " + jarPath, jarFile.exists());

        try (JarFile jar = new JarFile(jarFile)) {
            Manifest manifest = jar.getManifest();
            assertNotNull("Bundle JAR must contain a manifest", manifest);

            String importPackage = manifest.getMainAttributes().getValue("Import-Package");
            assertNotNull("Manifest must contain an Import-Package header", importPackage);

            assertFalse(
                    "Import-Package must not reference autovalue.shaded checkerframework packages. "
                            + "Current Import-Package: " + importPackage,
                    importPackage.contains("autovalue.shaded.org.checkerframework"));

            assertFalse(
                    "Import-Package must not reference com.google.errorprone.annotations.concurrent "
                            + "(compile-time-only annotation package). Current Import-Package: " + importPackage,
                    importPackage.contains("com.google.errorprone.annotations.concurrent"));
        }
    }
}
