package org.protege.editor.owl.ui.lang;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;

import org.protege.editor.owl.model.lang.LangCode;
import org.protege.editor.owl.model.lang.LangCodeRegistry;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-03-15
 */
public class LangCodeEditor implements ComboBoxEditor {

    @Nonnull
    private final LangCodeRegistry langCodeRegistry;

    private final JTextField textField = new JTextField();


    public LangCodeEditor(@Nonnull LangCodeRegistry langCodeRegistry) {
        this.langCodeRegistry = checkNotNull(langCodeRegistry);
    }


    private String getText() {
        return textField.getText().trim().toLowerCase();
    }

    /**
     * Return the component that should be added to the tree hierarchy for
     * this editor
     */
    @Override
    public Component getEditorComponent() {
        return textField;
    }

    /**
     * Set the item that should be edited. Cancel any editing if necessary
     *
     * @param anObject
     **/
    @Override
    public void setItem(Object anObject) {
        if(anObject == null) {
            textField.setText("");
        }
        else if(anObject instanceof LangCode) {
            textField.setText(((LangCode) anObject).getLangCode());
        }
        else {
            textField.setText(anObject.toString());
        }
    }

    /**
     * Return the edited item
     **/
    @Override
    public Object getItem() {
        Optional<LangCode> langCode = langCodeRegistry.getLangCode(getText());
        return langCode.orElse(null);
    }

    /**
     * Ask the editor to start editing and to select everything
     **/
    @Override
    public void selectAll() {
        textField.selectAll();
    }

    /**
     * Add an ActionListener. An action event is generated when the edited item changes
     *
     * @param l
     **/
    @Override
    public void addActionListener(ActionListener l) {
        textField.addActionListener(l);
    }

    /**
     * Remove an ActionListener
     *
     * @param l
     **/
    @Override
    public void removeActionListener(ActionListener l) {
        textField.removeActionListener(l);
    }
}
