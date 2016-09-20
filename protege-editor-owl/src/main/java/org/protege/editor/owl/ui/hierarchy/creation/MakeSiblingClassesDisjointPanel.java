package org.protege.editor.owl.ui.hierarchy.creation;

import org.protege.editor.core.util.Recommendation;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.EntityType;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 16 Sep 16
 *
 * Use {@link MakeSiblingsDisjointPanel} instead.
 */
@Deprecated
public class MakeSiblingClassesDisjointPanel extends MakeSiblingsDisjointPanel {

    public MakeSiblingClassesDisjointPanel(OWLEditorKit owlEditorKit) {
        super(owlEditorKit, EntityType.CLASS, Recommendation.RECOMMENDED);
    }
}
