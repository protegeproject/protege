package org.protege.editor.owl.model.axiom;

import org.protege.editor.owl.OWLEditorKit;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public class FreshActionStrategySelector {

    private FreshAxiomLocationPreferences preferences;

    private OWLEditorKit editorKit;

    public FreshActionStrategySelector(FreshAxiomLocationPreferences preferences, OWLEditorKit editorKit) {
        this.preferences = preferences;
        this.editorKit = editorKit;
    }

    public FreshAxiomLocationStrategy getFreshAxiomLocationStrategy() {
        return preferences.getFreshAxiomLocation().getStrategyFactory().getStrategy(editorKit);
    }
}
