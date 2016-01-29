package org.protege.editor.owl.model.search;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 14/12/2015
 */
public class SearchIndexPreferences {

    private final Logger logger = LoggerFactory.getLogger(SearchIndexPreferences.class);

    public static final String PREFERENCES_KEY = "SearchIndexPreferences";

    public static final String BASE_DIR = "BASE_DIR";

    public static final String PROTEGE_DIR = ".Protege";

    public static final String INDEX_DIR = "lucene";

    private static SearchIndexPreferences instance;

    private static String fsSeparator = System.getProperty("file.separator");

    private String baseDirectory = "";

    private SearchIndexPreferences() {
        // NO-OP
    }

    public static synchronized SearchIndexPreferences getInstance() {
        if (instance == null) {
            instance = new SearchIndexPreferences();
            instance.restore();
        }
        return instance;
    }

    private void restore() {
        Preferences p = getPreferences();
        String defaultBaseDirectory = prepare(System.getProperty("java.io.tmpdir"));
        baseDirectory = p.getString(BASE_DIR, defaultBaseDirectory);
    }

    private Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_KEY);
    }

    public void useTempDirectoryAsBaseDirectory() {
        String tmpDir = prepare(System.getProperty("java.io.tmpdir"));
        setBaseDirectory(tmpDir);
    }

    public void useHomeDirectoryAsBaseDirectory() {
        String homeDir = prepare(System.getProperty("user.home"));
        String indexHomeDir = homeDir + PROTEGE_DIR + fsSeparator + INDEX_DIR + fsSeparator;
        setBaseDirectory(indexHomeDir);
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    /**
     * Set the root directory for storing the index in the file system.
     *
     * @param baseDirectory
     *          A directory location set by the user.
     */
    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
        getPreferences().putString(BASE_DIR, baseDirectory);
        logger.info("... base index directory set to {}", baseDirectory);
    }

    /**
     * Create a map between the ontology and its index directory location.
     *
     * @param ontology
     *          A string represents the unique ID of the ontology structure.
     * @param indexLocation
     *          The directory location where the index files are stored.
     */
    public void registerIndexLocation(String ontologyVersion, String indexLocation) {
        getPreferences().putString(ontologyVersion, indexLocation);
    }

    /**
     * Get the index directory location based on the ontology signature. The method
     * will make sure too if the location still uses the same root base directory.
     *
     * @param ontology
     *          A string represents the unique ID of the ontology structure.
     * @return An optional directory location.
     */
    public Optional<String> getIndexLocation(String ontologyVersion) {
        String location = getPreferences().getString(ontologyVersion, null);
        if (!location.isEmpty()) {
            /*
             * Make sure the index directory is at the current root directory setting.
             */
            if (!location.startsWith(getBaseDirectory())) {
                location = null; // reset if the base directory has changed
            }
            /*
             * Make sure the index directory still exists.
             */
            if (!new File(location).exists()) {
                location = null; // reset if the index directory has been removed.
            }
        }
        return Optional.ofNullable(location);
    }

    /**
     * Make sure the path directory contains file separator at the end of the string
     *
     * @param directory
     *          input path directory
     * @return path directory with file separator at the end.
     */
    private String prepare(String directory) {
        String systemFileSeparator = System.getProperty("file.separator");
        if (!directory.endsWith(systemFileSeparator)) {
            directory += systemFileSeparator;
        }
        return directory;
    }
}
