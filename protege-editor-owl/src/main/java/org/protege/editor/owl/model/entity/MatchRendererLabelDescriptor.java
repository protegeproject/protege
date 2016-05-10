package org.protege.editor.owl.model.entity;

import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.semanticweb.owlapi.model.IRI;

import java.util.List;
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
public class MatchRendererLabelDescriptor implements LabelDescriptor {

    public String getLanguage() {
        final List<IRI> iris = OWLRendererPreferences.getInstance().getAnnotationIRIs();
        if (!iris.isEmpty()){
            List<String> langs = OWLRendererPreferences.getInstance().getAnnotationLangs();
            if (!langs.isEmpty()){
                return langs.get(0);
            }
        }
        return null;
    }


    public IRI getIRI() {
        final List<IRI> iris = OWLRendererPreferences.getInstance().getAnnotationIRIs();
        if (!iris.isEmpty()){
            return iris.get(0);
        }
        return null;
    }
}
