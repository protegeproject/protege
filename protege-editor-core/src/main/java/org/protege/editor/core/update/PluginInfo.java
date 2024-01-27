package org.protege.editor.core.update;


import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.Optional;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.protege.editor.core.plugin.PluginUtilities;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Jun-2007<br><br>

 * Encapsulates information about a new version of a plugin.
 */
public class PluginInfo {

    private Bundle b;


    private final String id;

    private final Version availableVersion;

    private final URL downloadURL;

    private Optional<URL> readmeURI = Optional.empty();

    private Optional<String> author = Optional.empty();

    private Optional<String> license = Optional.empty();

    private Optional<String> label = Optional.empty();


    public PluginInfo(String id, Version availableVersion, URL downloadURL) {
        this.id = checkNotNull(id);
        this.availableVersion = checkNotNull(availableVersion);
        this.downloadURL = checkNotNull(downloadURL);
    }


    public void setPluginDescriptor(Bundle b) {
        this.b = b;
    }


    public void setReadmeURI(URL readmeURI) {
        this.readmeURI = Optional.ofNullable(readmeURI);
    }


    public void setAuthor(String author) {
        this.author = Optional.ofNullable(author);
    }


    public void setLicense(String license) {
        this.license = Optional.ofNullable(license);
    }


    public Version getAvailableVersion() {
        return availableVersion;
    }


    public Optional<Version> getCurrentVersion() {
        if(b == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(PluginUtilities.getBundleVersion(b));
    }


    public URL getDownloadURL() {
        return downloadURL;
    }


    public Bundle getPluginDescriptor() {
        return b;
    }


    public Optional<URL> getReadmeURI() {
        return readmeURI;
    }


    public Optional<String> getAuthor() {
        return author;
    }


    public Optional<String> getLicense() {
        return license;
    }


    public String getId() {
        return id;
    }


    public void setLabel(String label) {
        this.label = Optional.ofNullable(label);
    }

    /**
     * Gets hold of the label.
     * @return The label.  Not {@code null}.
     */
    public String getLabel() {
        return label.orElse(id);
    }
    
    public String toString() {
        return "<PluginInfo: " + id + ">";
    }
}
