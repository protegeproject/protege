package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.renderer.DeleteEntityIcon;
import org.protege.editor.owl.ui.renderer.OWLClassIcon;
import org.protege.editor.owl.ui.renderer.OWLEntityIcon;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;
import sun.dc.pr.PRError;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 29-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DeleteClassAction extends AbstractDeleteEntityAction<OWLClass> {
	private static final long serialVersionUID = 5053506621554708546L;
	
	private OWLEntitySetProvider<OWLClass> clsSetProvider;
	private OWLClass thing;

	public DeleteClassAction(OWLEditorKit owlEditorKit, OWLEntitySetProvider<OWLClass> clsSetProvider) {
		super("Delete selected classes",
				new DeleteEntityIcon(new OWLClassIcon(OWLClassIcon.Type.PRIMITIVE, OWLEntityIcon.FillType.HOLLOW)),
				owlEditorKit,
				owlEditorKit.getModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider(), clsSetProvider);
		this.clsSetProvider = clsSetProvider;
		thing = owlEditorKit.getModelManager().getOWLDataFactory().getOWLThing();
	}

	@Override
	public void updateState() {
		super.updateState();
		setEnabled(!clsSetProvider.getEntities().contains(thing)); // perhaps this should be generalized?
	}

    protected String getPluralDescription() {
        return "classes";
    }


    protected String getResultsViewId() {
        return "OWLClassUsageView";
    }

}
