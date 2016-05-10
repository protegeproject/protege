package org.protege.editor.owl.model.entity;

import org.semanticweb.owlapi.model.IRI;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 28, 2008<br><br>
 */
public class CustomLabelDescriptor implements LabelDescriptor {

    public String getLanguage() {
        return EntityCreationPreferences.getNameLabelLang();
    }


    public IRI getIRI() {
        return EntityCreationPreferences.getNameLabelIRI();
    }
}
