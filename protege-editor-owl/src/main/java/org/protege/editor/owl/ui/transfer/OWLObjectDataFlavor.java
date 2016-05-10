package org.protege.editor.owl.ui.transfer;

import java.awt.datatransfer.DataFlavor;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 04-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLObjectDataFlavor extends DataFlavor {

    public static final OWLObjectDataFlavor OWL_OBJECT_DATA_FLAVOR = getOWLObjectDataFlavor();


    private OWLObjectDataFlavor() throws ClassNotFoundException {
        super(DataFlavor.javaJVMLocalObjectMimeType, "OWLObject");
    }


    private static OWLObjectDataFlavor getOWLObjectDataFlavor() {
        try {
            return new OWLObjectDataFlavor();
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }
}
