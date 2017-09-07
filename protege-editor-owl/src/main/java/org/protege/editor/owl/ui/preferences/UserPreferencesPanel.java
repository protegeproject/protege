package org.protege.editor.owl.ui.preferences;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.model.git.GitRepositoryManager;
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

    private static final String GIT_USER_NAME_CHECKBOX_BASE_TEXT = "Use Git user name when available";

    private final AugmentedJTextField userNameField = new AugmentedJTextField(40, "Enter user name");

    private final AugmentedJTextField orcidField = new AugmentedJTextField(22, "e.g. 0000-0002-1825-0097");

    private final JRadioButton useLoggedInUserNameButton = new JRadioButton("<html><body>Use system logged in user name (<b>" + System.getProperty("user.name") + "</b>)</body></html>");

    private final JRadioButton userSuppliedUserNameButton = new JRadioButton("Use supplied user name:");

    private final JCheckBox useGitUserName = new JCheckBox(GIT_USER_NAME_CHECKBOX_BASE_TEXT);

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
        useGitUserName.setSelected(userNameMan.isUseGitUserNameIfAvailable());

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
        panel.closeCurrentButtonRun();
        panel.addVerticalPadding();
        panel.addIndentedGroupComponent(userNameField);

        userSuppliedUserNameButton.addActionListener(e -> userNameField.setEnabled(true));
        useLoggedInUserNameButton.addActionListener(e -> userNameField.setEnabled(false));

        panel.addVerticalPadding();

        panel.addGroupComponent(useGitUserName);
        GitRepositoryManager.get(getOWLModelManager()).getUserName().ifPresent(gitUserName -> {
            String checkBoxLabel = String.format("<html><body>%s (currently <b>%s</b>)</body></html>",
                                                 GIT_USER_NAME_CHECKBOX_BASE_TEXT,
                                                 gitUserName);
            useGitUserName.setText(checkBoxLabel);
        });
        panel.addHelpText("<html><body>When editing ontologies that are under Git version control, checking this option will cause Protégé to use<br>" +
                                  "the user name that is provided by Git. If the Git user name is not available then the option selected above<br>" +
                                  "will be used.<br>" +
                                  "</body></html>");

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
        userNameMan.setUseGitUserNameIfAvailable(useGitUserName.isSelected());
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
