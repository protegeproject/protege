package org.protege.editor.owl.ui.hierarchy.creation;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.core.util.Recommendation;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.EntityType;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 6 Sep 16
 */
public class CreateSubHierarchyWizard extends Wizard {

    private TabIndentedHierarchyPanel tabIndentedHierarchyPanel;

    private Optional<MakeSiblingsDisjointPanel> makeSiblingsDisjointPanel;

    /**
     * Constructs a CreateSubHierarchyWizard.
     * @param editorKit The editor kit to be used.
     * @param entityType The type of entity hierarchy to be constructed.
     * @param makeSiblingsDisjoint An optional recommendation to make sibling entities disjoint.  If present then
     *                             a panel will be displayed asking users whether they want to make siblings disjoint.
     *                             If absent then the panel asking users whether they want to make siblings disjoint
     *                             will not be displayed.
     */
    public CreateSubHierarchyWizard(@Nonnull OWLEditorKit editorKit,
                                    @Nonnull EntityType<?> entityType,
                                    @Nonnull Optional<Recommendation> makeSiblingsDisjoint) {
        super(ProtegeManager.getInstance().getFrame(editorKit.getWorkspace()));
        tabIndentedHierarchyPanel = new TabIndentedHierarchyPanel(editorKit) {
            @Override
            public Object getBackPanelDescriptor() {
                return null;
            }

            @Override
            public Object getNextPanelDescriptor() {
                return makeSiblingsDisjoint.map(r -> (Object) MakeSiblingsDisjointPanel.ID).orElse(FINISH);
            }
        };
        registerWizardPanel(TabIndentedHierarchyPanel.ID, tabIndentedHierarchyPanel);
        setCurrentPanel(TabIndentedHierarchyPanel.ID);
        makeSiblingsDisjointPanel = makeSiblingsDisjoint.map(
                r -> new MakeSiblingsDisjointPanel(editorKit, entityType, makeSiblingsDisjoint.get()));
        makeSiblingsDisjointPanel.ifPresent(p ->
                        registerWizardPanel(MakeSiblingsDisjointPanel.ID, p)
        );

    }

    /**
     * The entered prefix.
     * @return The entered prefix.  May be empty.
     */
    public String getPrefix() {
        return tabIndentedHierarchyPanel.getPrefix();
    }

    /**
     * Gets the entered suffix.
     * @return The entered suffix.  May be empty.
     */
    @Nonnull
    public String getSuffix() {
        return tabIndentedHierarchyPanel.getSuffix();
    }

    /**
     * Gets the tab indented hierarchy as entered into the wizard.
     * @return A String representing the entered tab indented hierarchy.
     */
    @Nonnull
    public String getHierarchy() {
        return tabIndentedHierarchyPanel.getHierarchy();
    }

    /**
     * An optional flag that indicates whether siblings should be made disjoint.
     * @return An optional.  If true then siblings should be made disjoint.
     */
    @Nonnull
    public Optional<Boolean> isMakeSiblingsDisjoint() {
        return makeSiblingsDisjointPanel.map(p -> p.isMakeSiblingsDisjoint());
    }
}
