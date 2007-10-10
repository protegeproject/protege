package org.protege.editor.owl.ui.clshierarchy;

import java.util.HashSet;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.AbstractDeleteEntityAction;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.util.OWLEntitySetProvider;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 29-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DeleteClassAction extends AbstractDeleteEntityAction<OWLClass> {

    private OWLEntitySetProvider<OWLClass> clsSetProvider;


    public DeleteClassAction(OWLEditorKit owlEditorKit, OWLEntitySetProvider<OWLClass> clsSetProvider) {
        super("Delete classes", OWLIcons.getIcon("class.delete.png"), owlEditorKit);
        this.clsSetProvider = clsSetProvider;
    }


    protected Set<OWLClass> getSelectedEntities() {
        return new HashSet<OWLClass>(clsSetProvider.getEntities());
    }


    protected OWLObjectHierarchyProvider<OWLClass> getHierarchyProvider() {
        return getOWLEditorKit().getOWLModelManager().getOWLClassHierarchyProvider();
    }


    protected String getPluralDescription() {
        return "classes";
    }


    protected String getResultsViewId() {
        return "OWLClassUsageView";
    }
}
