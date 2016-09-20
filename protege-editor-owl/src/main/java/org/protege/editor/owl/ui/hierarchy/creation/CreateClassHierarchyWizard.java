package org.protege.editor.owl.ui.hierarchy.creation;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.core.util.Recommendation;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLClass;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Jul-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class CreateClassHierarchyWizard extends Wizard {

    private PickRootClassPanel pickRootClassPanel;

    private TabIndentedHierarchyPanel tabIndentedHierarchyPanel;

    private MakeSiblingsDisjointPanel makeSiblingClassesDisjointPanel;


    public CreateClassHierarchyWizard(OWLEditorKit owlEditorKit) {
        super(ProtegeManager.getInstance().getFrame(owlEditorKit.getWorkspace()));
        setTitle("Create Class Hierarchy");
        pickRootClassPanel = new PickRootClassPanel(owlEditorKit);
        registerWizardPanel(PickRootClassPanel.ID, pickRootClassPanel);
        tabIndentedHierarchyPanel = new TabIndentedHierarchyPanel(owlEditorKit);
        registerWizardPanel(TabIndentedHierarchyPanel.ID, tabIndentedHierarchyPanel);
        makeSiblingClassesDisjointPanel = new MakeSiblingsDisjointPanel(owlEditorKit, EntityType.CLASS, Recommendation.RECOMMENDED);
        registerWizardPanel(MakeSiblingsDisjointPanel.ID, makeSiblingClassesDisjointPanel);
        setCurrentPanel(PickRootClassPanel.ID);
    }


    public OWLClass getRootClass() {
        return pickRootClassPanel.getRootClass();
    }


    public String getHierarchy() {
        return tabIndentedHierarchyPanel.getHierarchy();
    }


    public String getSuffix() {
        return tabIndentedHierarchyPanel.getSuffix();
    }


    public String getPrefix() {
        return tabIndentedHierarchyPanel.getPrefix();
    }


    public boolean isMakeSiblingClassesDisjoint() {
        return makeSiblingClassesDisjointPanel.isMakeSiblingsDisjoint();
    }


    public void dispose() {
        pickRootClassPanel.dispose();
        super.dispose();
    }
}
