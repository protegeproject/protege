package org.protege.editor.owl.ui.hierarchy.creation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class TabIndentedHierarchyPanel extends AbstractOWLWizardPanel {

    public static final String ID = "TabIndentedHierarchyPanel";

    private TabIndentedHierachyTextPane textPane;

    private JTextField prefixField;

    private JTextField suffixField;


    public TabIndentedHierarchyPanel(OWLEditorKit owlEditorKit) {
        super(ID, "Enter hierarchy", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please enter the hierarchy that you want to create.  You should use tabs to indent names.");
        parent.setLayout(new BorderLayout());
        prefixField = new JTextField(30);
        suffixField = new JTextField(30);
        JPanel holder = new JPanel(new BorderLayout(7, 7));
//        LayoutManager lm = new PreferencesPanelLayoutManager(holder);
//        holder.setLayout(lm);
        JPanel prefixPanel = new JPanel(new BorderLayout(7, 7));
        prefixPanel.add(new JLabel("Prefix"), BorderLayout.WEST);
        prefixPanel.add(prefixField);
        holder.add(prefixPanel, BorderLayout.NORTH);

        JPanel suffixPanel = new JPanel(new BorderLayout(7, 7));
        suffixPanel.add(new JLabel("Suffix"), BorderLayout.WEST);
        suffixPanel.add(suffixField);
        holder.add(suffixPanel, BorderLayout.SOUTH);
        holder.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        parent.add(holder, BorderLayout.NORTH);
        textPane = new TabIndentedHierachyTextPane();
        parent.add(new JScrollPane(textPane));
    }


    public void displayingPanel() {
        prefixField.requestFocus();
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
        return MakeSiblingClassesDisjointPanel.ID;
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
        return PickRootClassPanel.ID;
    }
}
