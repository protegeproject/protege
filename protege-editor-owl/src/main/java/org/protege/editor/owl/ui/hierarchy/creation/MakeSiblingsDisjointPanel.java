package org.protege.editor.owl.ui.hierarchy.creation;

import org.protege.editor.core.ui.wizard.WizardPanel;
import org.protege.editor.core.util.Recommendation;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.semanticweb.owlapi.model.EntityType;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 18-Jul-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 * A panel that can be used in a Wizard to ask whether sibling entities should be disjoint.
 */
public class MakeSiblingsDisjointPanel extends AbstractOWLWizardPanel {

    public static final String ID = "MakeSiblingsDisjointPanel";

    private final JCheckBox checkBox;

    private final EntityType<?> entityType;

    private final Recommendation recommendation;

    /**
     * Constructs a WizardPanel that will ask the user whether they want to make siblings disjoint.
     * @param owlEditorKit The relevant editor kit.
     * @param entityType The type of sibling.
     * @param recommendation Whether or not it is recommended that siblings be disjoint.
     */
    public MakeSiblingsDisjointPanel(OWLEditorKit owlEditorKit, EntityType<?> entityType, Recommendation recommendation) {
        super(ID, String.format("Make sibling %s disjoint?", entityType.getPluralPrintName().toLowerCase()), owlEditorKit);
        this.checkBox = new JCheckBox(String.format("Do you want to make sibling %s disjoint? (%s)",
                entityType.getPluralPrintName().toLowerCase(),
                recommendation.getPrintName()),
                recommendation == Recommendation.RECOMMENDED);
        checkBox.setBackground(null);
        this.entityType = entityType;
        this.recommendation = recommendation;
        setInstructions(String.format("Make sibling %s disjoint (%s)",
                entityType.getPluralPrintName().toLowerCase(),
                recommendation.getPrintName()));
        JPanel parent = new JPanel(new BorderLayout());
        parent.add(checkBox, BorderLayout.NORTH);
        setContent(parent);
    }

    public boolean isMakeSiblingsDisjoint() {
        return checkBox.isSelected();
    }

    /**
     * Use {@code #isMakeSiblingsDisjoint}
     * @return true if siblings should be made disjoint, otherwise false.
     */
    @Deprecated
    public boolean isMakeSiblingClassesDisjoint() {
        return checkBox.isSelected();
    }


    public void displayingPanel() {
        checkBox.requestFocus();
    }


    public Object getNextPanelDescriptor() {
        return WizardPanel.FINISH;
    }


    /**
     * Override this class to provide the Object-based identifier of the panel that the
     * user should traverse to when the Back button is pressed. Note that this method
     * is only called when the button is actually pressed, so that the panel can change
     * the previous panel's identifier dynamically at runtime if necessary. Return null if
     * the button should be disabled.
     * @return Object-based identifier
     */
    public Object getBackPanelDescriptor() {
        return TabIndentedHierarchyPanel.ID;
    }

}
