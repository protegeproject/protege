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

    private String indexLocation;

    public IndexDirectory(String indexLocation) {
        this.indexLocation = indexLocation;
    }

    public Directory getPhysicalDirectory() throws IOException {
        return FSDirectory.open(Paths.get(indexLocation));
    }

    public String getLocation() {
        return indexLocation;
    }
}