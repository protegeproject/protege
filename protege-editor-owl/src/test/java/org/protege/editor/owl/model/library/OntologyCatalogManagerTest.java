package org.protege.editor.owl.model.library;

import org.junit.Before;
import org.junit.Test;
import org.protege.editor.owl.model.library.folder.FolderGroupManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * When the user answers "Resolve missing import?" by picking a file, that choice
 * must reach the catalog on disk, so the next open of the folder finds the file
 * without asking again. This must also hold for a folder opened for the first
 * time, whose catalog was created in this very session.
 */
public class OntologyCatalogManagerTest {

    private static final File SOURCE_DIR = new File("src/test/resources/ontologies");
    private static final File TEST_ROOT = new File("target/catalog-manager.test");

    @Before
    public void cleanTestRoot() throws IOException {
        if (TEST_ROOT.exists()) {
            try (Stream<Path> paths = Files.walk(TEST_ROOT.toPath())) {
                for (Path p : paths.sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
                    Files.delete(p);
                }
            }
        }
        assertTrue("Could not create " + TEST_ROOT, TEST_ROOT.mkdirs());
    }

    @Test
    public void userImportResolutionIsSavedForAFolderOpenedForTheFirstTime() throws IOException {
        File folder = new File(TEST_ROOT, "first-open");
        folder.mkdirs();
        File elsewhere = new File(TEST_ROOT, "elsewhere");
        elsewhere.mkdirs();
        File localCopy = new File(elsewhere, "imported.owl");
        Files.copy(new File(SOURCE_DIR, "amino-acid.owl").toPath(), localCopy.toPath(),
                   StandardCopyOption.REPLACE_EXISTING);
        URI missingImport = URI.create("http://import-test.invalid/resolved-by-hand");

        OntologyCatalogManager firstSession = newManager();
        firstSession.addFolder(folder);                       // no catalog existed: one is created now
        firstSession.addUserImportResolution(missingImport, localCopy);

        OntologyCatalogManager secondSession = newManager();
        secondSession.addFolder(folder);
        assertEquals("The mapping picked in the dialog must survive to the next open",
                     Optional.of(localCopy.toURI()), secondSession.getRedirectForUri(missingImport));
    }

    private static OntologyCatalogManager newManager() {
        return new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
    }
}
