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
        String defaultBaseDirectory = getUserHomeDirectory();
        baseDirectory = getPreferences().getString(BASE_DIR, defaultBaseDirectory);
    }

    private Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_KEY);
    }

    /**
     * Set the base directory to tmp directory following the local setting.
     */
    public void useTempDirectoryAsBaseDirectory() {
        String tmpDir = getTempDirectory();
        setBaseDirectory(tmpDir);
    }

    /**
     * Set the base directory to user home directory following the local setting.
     */
    public void useUserHomeDirectoryAsBaseDirectory() {
        String homeDir = getUserHomeDirectory();
        setBaseDirectory(homeDir);
    }

    /**
     * Get the <code>BASE_DIR</code> value.
     *
     * @return The base directory location.
     */
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
        this.baseDirectory = prepare(baseDirectory);
        getPreferences().putString(BASE_DIR, baseDirectory);
        logger.info("... base index directory set to {}", baseDirectory);
    }

    /**
     * Construct a full index path location given the directory name. The base directory
     * will be concatenated to the index directory name. 
     *
     * @param indexDirectoryName
     *          The index directory name
     * @return A full path location of the index directory.
     */
    public String createIndexLocation(String indexDirectoryName) {
        return getBaseDirectory() + fsSeparator + indexDirectoryName;
    }

    /**
     * Create a map between the ontology version and its index directory location.
     *
     * @param ontologyVersion
     *          A string represents the unique versioning ID of the ontology.
     * @param indexLocation
     *          The directory location where the index files are stored.
     */
    public void registerIndexLocation(String ontologyVersion, String indexLocation) {
        getPreferences().putString(ontologyVersion, prepare(indexLocation));
    }

    /**
     * Remove index location by its ontology version string.
     *
     * @param ontologyVersion
     *          A string represents the unique versioning ID of the ontology.
     */
    public void clearIndexLocation(String ontologyVersion) {
        getPreferences().putString(ontologyVersion, null);
    }

    /**
     * Get the index directory location based on the ontology version. The method
     * will return no location if users recently changed the <code>BASE_DIR</code>
     * location or the directory was recently removed (i.e., path is invalid)
     *
     * @param ontologyVersion
     *          A string represents the unique versioning ID of the ontology.
     * @return The directory location.
     */
    public Optional<String> getIndexLocation(String ontologyVersion) {
        Optional<String> location = getPreferenceValue(ontologyVersion);
        if (location.isPresent()) {
            /*
             * If the location is found in the preference (cached), we must check it
             * against the file system.
             */
            String cachedLocation = location.get();
            /*
             * Make sure the index directory is at the current root directory setting.
             */
            if (!cachedLocation.startsWith(getBaseDirectory())) {
                location = Optional.empty(); // reset if the base directory has changed
            }
            /*
             * Make sure the index directory still exists.
             */
            if (!new File(cachedLocation).exists()) {
                location = Optional.empty(); // reset if the index directory has been removed.
            }
        }
        return location;
    }

    /**
     * Clear all ontology versions and the associated index location from the preference.
     * Note that no physical files or folders are deleted from the file system.
     */
    public void clear() {
        Preferences p = getPreferences();
        p.clear();
        p.putString(BASE_DIR, getBaseDirectory());
    }

    /*
     * Private utility methods
     */

    private String getTempDirectory() {
        return prepare(System.getProperty("java.io.tmpdir"));
    }

    private String getUserHomeDirectory() {
        String homeDir = prepare(System.getProperty("user.home"));
        return homeDir + fsSeparator + PROTEGE_DIR + fsSeparator + INDEX_DIR;
    }

    private Optional<String> getPreferenceValue(String key) {
        return Optional.ofNullable(getPreferences().getString(key, null));
    }

    /**
     * Make sure the path directory contains no file separator at the end of the string
     *
     * @param directory
     *          input path directory
     * @return clean path directory.
     */
    private static String prepare(String directory) {
        String systemFileSeparator = System.getProperty("file.separator");
        if (directory.endsWith(systemFileSeparator)) {
            directory = directory.substring(0, directory.length()-1);
        }
        return directory;
    }
}
