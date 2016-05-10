package org.protege.editor.owl;

import java.util.Properties;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 11-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ProtegeOWLProperties extends Properties {
    
    public static final String CONDITIONS_SUPERCLASSES_HEADER = "ConditionsSuperClassesHeader";

    public static final String CONDITIONS_EQUIVALENT_CLASSES_HEADER = "ConditionsEquivalentClassesHeader";


    private static ProtegeOWLProperties instance;


    private ProtegeOWLProperties() {
        put(CONDITIONS_SUPERCLASSES_HEADER, "Superclasses (Necessary criteria)");
        put(CONDITIONS_EQUIVALENT_CLASSES_HEADER, "Equivalent class (Necessary & Sufficient criteria)");
    }


    public static ProtegeOWLProperties getInstance() {
        if (instance == null) {
            instance = new ProtegeOWLProperties();
        }
        return instance;
    }


    public String getString(Object key) {
        return (String) get(key);
    }
}
