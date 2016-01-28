package org.protege.editor.owl.model.search;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static final String PREFIX_LABEL = "ProtegeIndex-";

    private static SearchIndexPreferences instance;

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

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public boolean contains(String ontologyUid) {
        String value = getPreferences().getString(ontologyUid, "");
        if (value.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Set the root directory for storing the index in the file system.
     *
     * @param baseDirectory
     *          A directory location set by the user.
     */
    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = prepare(baseDirectory);
        getPreferences().putString(BASE_DIR, this.baseDirectory);
        logger.info("... base index directory set to {}", getBaseDirectory());
    }

    /**
     * Create a map between the ontology and its index directory.
     *
     * @param ontologyUid
     *          A string represents the unique ID of the ontology structure.
     * @param directoryName
     *          The directory name where the index files are stored.
     */
    public void putIndexDirectory(String ontologyUid, String directoryName) {
        String indexLocation = getBaseDirectory() + PREFIX_LABEL + directoryName;
        getPreferences().putString(ontologyUid, indexLocation);
    }

    /**
     * Get the index directory location based on the ontology signature. The method
     * will make sure too if the location still uses the same root base directory.
     *
     * @param ontologyUid
     *          A string represents the unique ID of the ontology structure.
     * @return An optional directory location.
     */
    public Optional<String> getIndexDirectory(String ontologyUid) {
        String location = getPreferences().getString(ontologyUid, "");
        
        /*
         * Make sure the index directory location has the same root directory.
         */
        if (!location.startsWith(getBaseDirectory())) {
            location = "";
        }
        return Optional.of(location);
    }

    /**
     * Update the ontology signature key. This method only update the key while
     * maintaining the same value of index directory location.
     *
     * @param oldOntologyUid
     *          A string represents the old ontology unique ID.
     * @param newOntologyUid
     *          A new unique ID to replace.
     * @return Returns <code>true</code> if the update succeeds, or <code>false</code>
     *          otherwise.
     */
    public boolean updateIndexDirectory(String oldOntologyUid, String newOntologyUid) {
        Optional<String> indexDirectory = getIndexDirectory(oldOntologyUid);
        if (indexDirectory.isPresent()) {
            putIndexDirectory(newOntologyUid, indexDirectory.get());
            return true;
        }
        return false;
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
