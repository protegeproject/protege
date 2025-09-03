package org.protege.editor.owl.ui.prefix;

import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-06-18
 */
public class PrefixMappingEditorViewImpl extends JPanel implements PrefixMappingEditorView {

    private final JTextField prefixNameField = new JTextField(30);

    private final JTextField prefixField = new JTextField(30);

    public PrefixMappingEditorViewImpl() {
        setLayout(new BorderLayout());
        PreferencesLayoutPanel layout = new PreferencesLayoutPanel();
        layout.setUseVerticalLabelling(true);
        layout.addGroup("Prefix name");
        layout.addGroupComponent(prefixNameField);
        layout.addHelpText("<html><body>Prefix names are usually short and should end with a colon.<br>Examples are <b>schema:</b>, <b>go:</b> and <b>vehicle:</b></body></html>");
        layout.addVerticalPadding();
        layout.addGroup("Prefix");
        layout.addGroupComponent(prefixField);
        layout.addHelpText("Prefixes should not contain spaces and usually end with a slash (/) or hash (#)");

        add(layout, BorderLayout.NORTH);
    }

    @Override
    public PrefixMappingEditorView asJComponent() {
        return this;
    }

    @Override
    public void clear() {
        prefixNameField.setText("");
        prefixField.setText("");
    }

    @Override
    public void setPrefixName(@Nonnull String prefixName) {
        prefixNameField.setText(prefixName);
    }

    @Nonnull
    @Override
    public String getPrefixName() {
        return prefixNameField.getText().trim();
    }

    @Override
    public void setPrefix(@Nonnull String prefix) {
        prefixField.setText(prefix);
    }

    @Override
    public String getPrefix() {
        return prefixField.getText().trim();
    }

    @Override
    public void setPrefixNameChangedHandler(@Nonnull PrefixNameChangedHandler handler) {
        prefixNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handler.handlePrefixNameChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handler.handlePrefixNameChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    @Override
    public void setPrefixChangedHandler(@Nonnull PrefixChangedHandler handler) {
        prefixField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handler.handlePrefixChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handler.handlePrefixChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    public static void main(String[] args) {
        PrefixMappingEditorViewImpl view = new PrefixMappingEditorViewImpl();
        JOptionPane.showConfirmDialog(null, view);
    }

}
