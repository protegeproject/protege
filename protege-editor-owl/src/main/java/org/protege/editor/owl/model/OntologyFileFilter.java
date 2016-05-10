package org.protege.editor.owl.model;

import java.io.File;
import java.io.FileFilter;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Aug-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * A file filter that accepts probable ontology files.
 */
public class OntologyFileFilter implements FileFilter {

    public boolean accept(File pathname) {
        String name = pathname.getName();
        return name.endsWith(".owl") | name.endsWith(".rdf") | name.endsWith(".xml");
    }
}
