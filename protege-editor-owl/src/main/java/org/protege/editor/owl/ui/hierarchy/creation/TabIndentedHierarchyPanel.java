package org.protege.editor.owl.ui.hierarchy.creation;

import java.awt.BorderLayout;

import javax.annotation.Nullable;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Jul-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class TabIndentedHierarchyPanel extends AbstractOWLWizardPanel {

    public static final String ID = "TabIndentedHierarchyPanel";

    private final TabIndentedHierachyTextPane textPane = new TabIndentedHierachyTextPane();

    private final JTextField prefixField = new JTextField(30);

    private final JTextField suffixField = new JTextField(30);


    public TabIndentedHierarchyPanel(OWLEditorKit owlEditorKit) {
        super(ID, "Enter hierarchy", owlEditorKit);
        setInstructions("Please enter one name per line.  You can use tabs to indent names to create a hierarchy.");

        JPanel parent = new JPanel(new BorderLayout());

        textPane.setBorder(null);
        parent.add(new JScrollPane(textPane));


        JPanel prefixSuffixHolder = new JPanel(new BorderLayout(7, 7));

        JPanel prefixPanel = new JPanel(new BorderLayout(7, 7));
        prefixPanel.add(new JLabel("Prefix"), BorderLayout.WEST);
        prefixPanel.add(prefixField);
        prefixSuffixHolder.add(prefixPanel, BorderLayout.NORTH);

        JPanel suffixPanel = new JPanel(new BorderLayout(7, 7));
        suffixPanel.add(new JLabel("Suffix"), BorderLayout.WEST);
        suffixPanel.add(suffixField);
        prefixSuffixHolder.add(suffixPanel, BorderLayout.SOUTH);
        prefixSuffixHolder.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        parent.add(prefixSuffixHolder, BorderLayout.SOUTH);

        setContent(parent);

    }


    public void displayingPanel() {
        textPane.requestFocus();
    }


    public String getHierarchy() {
        return textPane.getText();
    }


    public String getSuffix() {
        return suffixField.getText();
    }


    public String getPrefix() {
        return prefixField.getText();
    }


    public Object getNextPanelDescriptor() {
        return MakeSiblingsDisjointPanel.ID;
    }


    /**
     * Override this class to provide the Object-based identifier of the panel that the
     * user should traverse to when the Back button is pressed. Note that this method
     * is only called when the button is actually pressed, so that the panel can change
     * the previous panel's identifier dynamically at runtime if necessary. Return null if
     * the button should be disabled.
     * @return Object-based identifier
     */
    @Nullable
    public Object getBackPanelDescriptor() {
        return PickRootClassPanel.ID;
    }
}
