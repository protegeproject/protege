package org.protege.editor.owl;

import java.io.Serializable;
import java.net.URI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 25-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLModelManagerDescriptor implements Serializable {

    public URI physicalURI;


    public OWLModelManagerDescriptor(URI physicalURI) {
        this.physicalURI = physicalURI;
    }


    public URI getPhysicalURI() {
        return physicalURI;
    }
}
