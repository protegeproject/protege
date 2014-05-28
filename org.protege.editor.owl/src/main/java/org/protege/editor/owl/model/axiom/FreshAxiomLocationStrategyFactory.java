package org.protege.editor.owl.model.axiom;

import org.protege.editor.owl.OWLEditorKit;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public interface FreshAxiomLocationStrategyFactory {

    FreshAxiomLocationStrategy getStrategy(OWLEditorKit editorKit);
}
