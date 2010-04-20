package org.protege.editor.owl.model.library.folder;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

public class OwlXmlNameAlgorithm implements Algorithm {
    boolean assumeLatest = false;

    public void setAssumeLatest(boolean assumeLatest) {
        this.assumeLatest = assumeLatest;
    }
    
    public Set<URI> getSuggestions(File f) {
        return Collections.emptySet();
    }

}
