package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.plugin.ProtegePlugin;
import org.semanticweb.owlapi.model.AxiomType;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Feb 26, 2009<br><br>
 */
public interface OWLClassExpressionEditorPlugin extends ProtegePlugin<OWLClassExpressionEditor> {

    public static final String ID = "ui_editor_description";

    @SuppressWarnings("unchecked")
	boolean isSuitableFor(AxiomType type);

    String getIndex();
}
