package org.protege.editor.owl.ui.preferences;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.model.user.Orcid;
import org.protege.editor.owl.model.user.OrcidPreferencesManager;
import org.protege.editor.owl.model.user.UserNamePreferencesManager;
import org.protege.editor.owl.model.user.UserPreferences;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 27/01/16
 */
public class UserPreferencesPanel extends OWLPreferencesPanel {

    private final AugmentedJTextField userNameField = new AugmentedJTextField(40, "Enter user name");

    private final AugmentedJTextField orcidField = new AugmentedJTextField(22, "e.g. 0000-0002-1825-0097");

    private final JRadioButton useLoggedInUserNameButton = new JRadioButton("<html><body>Use system logged in user name (<b>" + System.getProperty("user.name") + "</b>)</body></html>");

    private final JRadioButton userSuppliedUserNameButton = new JRadioButton("Use supplied user name:");

    @Override
    public void initialise() throws Exception {
        layoutUserInterface();
        updateFromPreferences();
    }

    private void updateFromPreferences() {
        Preferences preferences = UserPreferences.get();

        UserNamePreferencesManager userNameMan = new UserNamePreferencesManager(preferences);
        if(userNameMan.getUserName().isPresent()) {
            userSuppliedUserNameButton.setSelected(true);
            userNameField.setText(userNameMan.getUserName().get());
        }
        else {
            useLoggedInUserNameButton.setSelected(true);
            userNameField.setText("");
            userNameField.setEnabled(false);
        }

        OrcidPreferencesManager orcidMan = new OrcidPreferencesManager(preferences);
        if (orcidMan.getOrcid().isPresent()) {
            orcidField.setText(orcidMan.getOrcid().get().getValue());
        }
        else {
            orcidField.setText("");
        }
    }

    private void layoutUserInterface() {
        setLayout(new BorderLayout());
        PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        add(panel, BorderLayout.NORTH);

        panel.addGroup("User name");
        panel.addGroupComponent(useLoggedInUserNameButton);
        panel.addGroupComponent(userSuppliedUserNameButton);

        ButtonGroup userNameButtonGroup = new ButtonGroup();
        userNameButtonGroup.add(useLoggedInUserNameButton);
        userNameButtonGroup.add(userSuppliedUserNameButton);
        panel.addVerticalPadding();
        panel.addIndentedGroupComponent(userNameField);

        userSuppliedUserNameButton.addActionListener(e -> userNameField.setEnabled(true));
        useLoggedInUserNameButton.addActionListener(e -> userNameField.setEnabled(false));


        panel.addSeparator();
        panel.addGroup("ORCID");
        panel.addGroupComponent(orcidField);

        orcidField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateOrcidField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateOrcidField();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateOrcidField();
            }
        });
    }

    private void updateOrcidField() {
            Optional<Orcid> enteredOrcid = getEnteredOrcid();
            if(enteredOrcid.isPresent() || orcidField.getText().isEmpty()) {
                orcidField.clearErrorMessage();
            }
            else {
                orcidField.setErrorMessage("Invalid ORCID");
            }
    }


    @Override
    public void applyChanges() {
        Preferences preferences = UserPreferences.get();

        UserNamePreferencesManager userNameMan = new UserNamePreferencesManager(preferences);
        if(userSuppliedUserNameButton.isSelected() && !getEnteredUserName().isEmpty()) {
            userNameMan.setUserName(getEnteredUserName());
        }
        else {
            userNameMan.clearUserName();
        }

        OrcidPreferencesManager orcidMan = new OrcidPreferencesManager(preferences);
        Optional<Orcid> enteredOrcid = getEnteredOrcid();
        if (enteredOrcid.isPresent()) {
            orcidMan.setOrcid(enteredOrcid.get());
        }
        else {
            orcidMan.clearOrcid();
        }
    }

    private Optional<Orcid> getEnteredOrcid() {
        try {
            return Optional.of(new Orcid(orcidField.getText().trim()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String getEnteredUserName() {
        return userNameField.getText().trim();
    }

    @Override
    public void dispose() throws Exception {

    }
}
