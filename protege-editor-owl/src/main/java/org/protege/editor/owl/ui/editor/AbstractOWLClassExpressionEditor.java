package org.protege.editor.owl.ui.editor;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.AxiomType;
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
 * Date: Feb 26, 2009<br><br>
 */
public abstract class AbstractOWLClassExpressionEditor implements OWLClassExpressionEditor {

    private OWLEditorKit eKit;

    private String label;

    private AxiomType type = null;


    public final void setup(String id, String name, OWLEditorKit eKit) {
        this.eKit = eKit;
        this.label = name;
    }


    @SuppressWarnings("unchecked")
	public final void setAxiomType(AxiomType type) {
        this.type = type;
    }


    public final String getEditorName() {
        return label;
    }


    protected final OWLEditorKit getOWLEditorKit(){
        return eKit;
    }


    protected final AxiomType getAxiomType(){
        return type;
    }
}
