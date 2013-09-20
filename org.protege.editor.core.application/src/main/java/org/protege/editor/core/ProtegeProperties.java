package org.protege.editor.core;

import java.util.Properties;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 22, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ProtegeProperties extends Properties {

    /**
     * 
     */
    private static final long serialVersionUID = -225880915589492822L;

    public static final String PROTEGE_PREFIX = "org.protege.";

    public static final String CLASS_COLOR_KEY = PROTEGE_PREFIX + "classcolor";

    public static final String PROPERTY_COLOR_KEY = PROTEGE_PREFIX + "propertycolor";

    public static final String OBJECT_PROPERTY_COLOR_KEY = PROTEGE_PREFIX + "objectpropertycolor";

    public static final String DATA_PROPERTY_COLOR_KEY = PROTEGE_PREFIX + "datapropertycolor";

    public static final String INDIVIDUAL_COLOR_KEY = PROTEGE_PREFIX + "individualcolor";

    public static final String ONTOLOGY_COLOR_KEY = PROTEGE_PREFIX + "ontologycolor";

    public static final String ANNOTATION_PROPERTY_COLOR_KEY = PROTEGE_PREFIX + "annotationscolor";

    public static final String DATATYPE_COLOR_KEY = PROTEGE_PREFIX + "datatypecolor";

    public static final String CLASS_VIEW_CATEGORY = PROTEGE_PREFIX + "classcategory";

    public static final String OBJECT_PROPERTY_VIEW_CATEGORY = PROTEGE_PREFIX + "objectpropertycategory";

    public static final String DATA_PROPERTY_VIEW_CATEGORY = PROTEGE_PREFIX + "datapropertycategory";

    public static final String ANNOTATION_PROPERTY_VIEW_CATEGORY = PROTEGE_PREFIX + "annotationpropertycategory";

    public static final String INDIVIDUAL_VIEW_CATEGORY = PROTEGE_PREFIX + "individualcategory";

    public static final String DATATYPE_VIEW_CATEGORY = PROTEGE_PREFIX + "datatypecategory";

    public static final String ONTOLOGY_VIEW_CATEGORY = PROTEGE_PREFIX + "ontologycategory";

    public static final String QUERY_VIEW_CATEGORY = PROTEGE_PREFIX + "querycategory";
    
    public static final String DIFF_VIEW_CATEGORY = PROTEGE_PREFIX + "differencecategory";


    private static ProtegeProperties instance;

	public static final String PLASTIC_3D_LAF = "com.jgoodies.looks.plastic.Plastic3DLookAndFeel";

	//Default Protege L&F
	public static final String PLASTIC_LAF_NAME = "com.jgoodies.looks.plastic.PlasticLookAndFeel";


    private ProtegeProperties() {

    }


    public static synchronized ProtegeProperties getInstance() {
        if (instance == null) {
            instance = new ProtegeProperties();
        }
        return instance;
    }
}
