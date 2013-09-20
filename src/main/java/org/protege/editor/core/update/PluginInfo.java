package org.protege.editor.core.update;


import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.protege.editor.core.plugin.PluginUtilities;

import java.net.URL;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Jun-2007<br><br>
 * <p/>
 * Encapsulates information about a new version of a plugin.
 */
public class PluginInfo {

    private Bundle b;

    private Version availableVersion;

    private URL downloadURL;

    private URL readmeURI;

    private String author;

    private String license;

    private String id;

    private String label;


    public PluginInfo(String id, Version availableVersion, URL downloadURL) {
        this.id = id;
        this.availableVersion = availableVersion;
        this.downloadURL = downloadURL;
    }


    public void setPluginDescriptor(Bundle b) {
        this.b = b;
    }


    public void setReadmeURI(URL readmeURI) {
        this.readmeURI = readmeURI;
    }


    public void setAuthor(String author) {
        this.author = author;
    }


    public void setLicense(String license) {
        this.license = license;
    }


    public Version getAvailableVersion() {
        return availableVersion;
    }


    public Version getCurrentVersion() {
        return PluginUtilities.getBundleVersion(b);
    }


    public URL getDownloadURL() {
        return downloadURL;
    }


    public Bundle getPluginDescriptor() {
        return b;
    }


    public URL getReadmeURI() {
        return readmeURI;
    }


    public String getAuthor() {
        return author;
    }


    public String getLicense() {
        return license;
    }


    public String getId() {
        return id;
    }


    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets hold of the label.
     * @return The label.  Not {@code null}.
     */
    public String getLabel() {
        if (label == null){
            return id;
        }
        return label;
    }
    
    public String toString() {
        return "<PluginInfo: " + id + ">";
    }
}
