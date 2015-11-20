package org.protege.editor.owl.ui.selector;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 5, 2008<br><br>
 */
public abstract class AbstractHierarchySelectorPanel<O extends OWLEntity> extends AbstractSelectorPanel<O> {

    private OWLObjectHierarchyProvider<O> hp;


    public AbstractHierarchySelectorPanel(OWLEditorKit editorKit, boolean editable, OWLObjectHierarchyProvider<O> hp) {
        super(editorKit, editable, false);
        this.hp = hp;
        createUI();
    }


    public OWLObjectHierarchyProvider<O> getHierarchyProvider() {
        return hp;
    }
}
