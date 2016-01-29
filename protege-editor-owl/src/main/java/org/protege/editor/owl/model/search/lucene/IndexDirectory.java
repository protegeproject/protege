package org.protege.editor.owl.model.search.lucene;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 29/01/2016
 */
public class IndexDirectory {

    private boolean createNew = false;

    private String indexLocation;

    /* No external instantiation */
    private IndexDirectory(boolean createNew, String indexLocation) {
        this.createNew = createNew;
        this.indexLocation = indexLocation;
    }

    public static IndexDirectory create(String indexLocation) {
        return new IndexDirectory(true, indexLocation);
    }

    public static IndexDirectory load(String indexLocation) {
        return new IndexDirectory(false, indexLocation);
    }

    public Directory getPhysicalDirectory() throws IOException {
        return FSDirectory.open(Paths.get(indexLocation));
    }

    public String getLocation() {
        return indexLocation;
    }

    public boolean exists() {
        return !createNew;
    }
}