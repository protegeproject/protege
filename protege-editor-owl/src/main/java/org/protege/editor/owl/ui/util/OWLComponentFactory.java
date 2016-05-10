package org.protege.editor.owl.ui.util;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.ui.editor.OWLClassDescriptionEditor;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLDataPropertySelectorPanel;
import org.protege.editor.owl.ui.selector.OWLIndividualSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClassExpression;
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
public interface OWLComponentFactory extends Disposable {


    OWLClassDescriptionEditor getOWLClassDescriptionEditor(OWLClassExpression expr);

    @SuppressWarnings("unchecked")
    OWLClassDescriptionEditor getOWLClassDescriptionEditor(OWLClassExpression expr, AxiomType type);

    OWLClassSelectorPanel getOWLClassSelectorPanel();

    OWLObjectPropertySelectorPanel getOWLObjectPropertySelectorPanel();

    OWLDataPropertySelectorPanel getOWLDataPropertySelectorPanel();

    OWLIndividualSelectorPanel getOWLIndividualSelectorPanel();


    void dispose();
}
