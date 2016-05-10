package org.protege.editor.core;

import java.io.File;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FileManager {

    public static final String DATA_FOLDER_NAME = "Data";

    public static final String ONTOLOGY_LIBRARY_FOLDER = "StandardOntologies";

    public static final String VIEW_CONFIGURATIONS_FOLDER = "ViewConfigurations";


    public static File getDataFolder() {
        File f = new File(DATA_FOLDER_NAME);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }


    public static File getViewConfigurationsFolder() {
        File f = new File(getDataFolder(), VIEW_CONFIGURATIONS_FOLDER);
        f.mkdirs();
        return f;
    }


    public static File getOntologyLibraryFolder() {
        File f = new File(ONTOLOGY_LIBRARY_FOLDER);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }
}
