package org.protege.editor.owl.ui.ontology.authentication;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.codec.binary.Base64;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicAuthenticationDialog implements BasicAuthenticationHandler {

    public final Logger logger = LoggerFactory.getLogger(BasicAuthenticationDialog.class);

    private OWLEditorKit owlEditorKit;

//    private java.util.List<InputVerificationStatusChangedListener> listeners = new ArrayList<>();
//
//    private boolean currentlyValid = true;
    public BasicAuthenticationDialog(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        //createBasicAutenticationUI();
    }

    @Override
    public <T extends Throwable> String handleBasicAuthenticationOntology(OWLOntologyID ontologyID, URI loc, T e) throws Throwable {
        AuthenticationPanel panel = new AuthenticationPanel();
        int ret1 = new UIHelper(owlEditorKit).showValidatingDialog(
                "Basic Authentication",
                panel,
                panel.userNameField);
        if (ret1 == JOptionPane.OK_OPTION) {
            return panel.getBasicAuthenticationString();
        } else {
            return null;
        }
    }

//    public void setEnabled(boolean b) {
//        userNameField.setEnabled(b);
//        passwordField.setEditable(b);
//        super.setEnabled(b);
//    }
//
//    public void setName(String name) {
//        userNameField.setText(name);
//    }
//    void createBasicAutenticationUI() {
//        setLayout(new BorderLayout());
//        JPanel holder = new JPanel(new GridBagLayout());
//        add(holder);
//        Insets insets = new Insets(0, 0, 2, 4);
//
//        int rowIndex = 0;
//
//        JLabel usernameLabel = new JLabel("Username:");
//        holder.add(usernameLabel, new GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, insets, 0, 0));
//        usernameLabel.setFont(usernameLabel.getFont().deriveFont(Font.BOLD));
//
//        userNameField = new JTextField(30);
//        //userNameField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
////        userNameField.getDocument().addDocumentListener(new DocumentListener() {
////            public void insertUpdate(DocumentEvent e) {
////                update();
////            }
////
////            public void removeUpdate(DocumentEvent e) {
////                update();
////            }
////
////            public void changedUpdate(DocumentEvent e) {
////            }
////        });
//
//        holder.add(userNameField, new GridBagConstraints(1, rowIndex, 1, 1, 100.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, insets, 0, 0));
//
//        rowIndex++;
//        JLabel passwordLabel = new JLabel("Password:");
//        holder.add(passwordLabel, new GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, insets, 0, 0));
//        passwordLabel.setFont(passwordLabel.getFont().deriveFont(Font.BOLD));
//
//        passwordField = new JPasswordField(30);
//        holder.add(passwordField, new GridBagConstraints(1, rowIndex, 1, 1, 100.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.HORIZONTAL, insets, 0, 0));
//
////        // Create the labels and text fields.
////        JLabel userNameLabel = new JLabel("Student ID:   ", JLabel.RIGHT);
////        userNameField = new JTextField("");
////        JLabel passwordLabel = new JLabel("Password:   ", JLabel.RIGHT);
////        passwordField = new JPasswordField("");
////        authenticationPanel = new JPanel(false);
////        authenticationPanel.setLayout(new BoxLayout(authenticationPanel,
////                BoxLayout.X_AXIS));
////        JPanel namePanel = new JPanel(false);
////        namePanel.setLayout(new GridLayout(0, 1));
////        namePanel.add(userNameLabel);
////        namePanel.add(passwordLabel);
////        JPanel fieldPanel = new JPanel(false);
////        fieldPanel.setLayout(new GridLayout(0, 1));
////        fieldPanel.add(userNameField);
////        fieldPanel.add(passwordField);
////        authenticationPanel.add(namePanel);
////        authenticationPanel.add(fieldPanel);
//        holder.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//    }
    public String showDialog() {
        AuthenticationPanel panel = new AuthenticationPanel();
//        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        int ret1 = new UIHelper(owlEditorKit).showValidatingDialog(
                "Basic Authentication",
                panel,
                panel.userNameField);

//        int ret = JOptionPaneEx.showConfirmDialog(null,
//                                            "Basic Authentication",
//                                            panel,
//                                            JOptionPane.PLAIN_MESSAGE,
//                                            JOptionPane.OK_CANCEL_OPTION,
//                                            panel.userNameField);
        if (ret1 == JOptionPane.OK_OPTION) {
            return panel.getBasicAuthenticationString();
        } else {
            return null;
        }

//        int ret = new UIHelper(owlEditorKit).showValidatingDialog(
//                "Basic Authentication",
//                panel,
//                panel.userNameField);
//        if (ret == JOptionPane.OK_OPTION) {
//            return panel.getBasicAuthenticationString();
//        } else {
//            return null;
//        }
//        if (JOptionPane.showOptionDialog(null, authenticationPanel,
//                AuthenticationTitle,
//                JOptionPane.OK_CANCEL_OPTION,
//                JOptionPane.INFORMATION_MESSAGE,
//                null, AuthenticationOptionNames,
//                AuthenticationOptionNames[0]) != 0) {
//            System.exit(0);
//        }
//        username = userNameField.getText();
//        password = passwordField.getText();
//        // Convert username and password into base64 string
//        String authString = username + ":" + password;
//        System.out.println("auth string: " + authString);
//        byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
//        String authStringEnc = new String(authEncBytes);
//        System.out.println("Base64 encoded auth string: " + authStringEnc);
//        String basicAuth = "Basic " + authStringEnc;
//        return basicAuth;
    }

//    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
//        listeners.add(listener);
//        listener.verifiedStatusChanged(currentlyValid);
//    }
//
//    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
//        listeners.remove(listener);
//    }
//    private String getBasicAuthenticationString() {
//        username = this.getEntityUsername();
//        password = this.getEntityPassword();
//        String authString = username + ":" + password;
//        System.out.println("auth string: " + authString);
//        byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
//        String authStringEnc = new String(authEncBytes);
//        System.out.println("Base64 encoded auth string: " + authStringEnc);
//        String basicAuth = "Basic " + authStringEnc;
//        return basicAuth;
//    }
    public class AuthenticationPanel extends JPanel implements VerifiedInputEditor {

        private String username, password;

        private JPanel authenticationPanel;

        private JTextField userNameField;

        private JPasswordField passwordField;

        private List<InputVerificationStatusChangedListener> listeners
                = new ArrayList<>();

        private boolean currentlyValid = true;

        public AuthenticationPanel() {
            createUI();
        }

        private void createUI() {
            setLayout(new BorderLayout());
            JPanel holder = new JPanel(new GridBagLayout());
            add(holder);
            Insets insets = new Insets(0, 0, 2, 4);

            int rowIndex = 0;

            JLabel usernameLabel = new JLabel("Username:");
            holder.add(usernameLabel, new GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, insets, 0, 0));
            usernameLabel.setFont(usernameLabel.getFont().deriveFont(Font.BOLD));

            userNameField = new JTextField(30);
            userNameField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    update();
                }

                public void removeUpdate(DocumentEvent e) {
                    update();
                }

                public void changedUpdate(DocumentEvent e) {
                }
            });

            holder.add(userNameField, new GridBagConstraints(1, rowIndex, 1, 1, 100.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, insets, 0, 0));

            rowIndex++;
            JLabel passwordLabel = new JLabel("Password:");
            holder.add(passwordLabel, new GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, insets, 0, 0));
            passwordLabel.setFont(passwordLabel.getFont().deriveFont(Font.BOLD));

            passwordField = new JPasswordField(30);
            passwordField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    update();
                }

                public void removeUpdate(DocumentEvent e) {
                    update();
                }

                public void changedUpdate(DocumentEvent e) {
                }
            });
            holder.add(passwordField, new GridBagConstraints(1, rowIndex, 1, 1, 100.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.HORIZONTAL, insets, 0, 0));

            holder.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            update();
        }

        public void setEnabled(boolean b) {
            userNameField.setEnabled(b);
            passwordField.setEditable(b);
            super.setEnabled(b);
        }

        public void setUserName(String name) {
            userNameField.setText(name);
        }

        public void setPassword(String password) {
            passwordField.setText(password);
        }

        public String getEntityUsername() {
            return userNameField.getText().trim();
        }

        public String getEntityPassword() {
            return new String(passwordField.getPassword());
        }

        @Override
        public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
            listeners.add(listener);
            listener.verifiedStatusChanged(currentlyValid);
        }

        @Override
        public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
            listeners.remove(listener);
        }

        private void update() {
            try {

                if (userNameField.getText().trim().isEmpty()) {
                    setValid(false);
                    return;
                }
                if (getEntityPassword().trim().isEmpty()) {
                    setValid(false);
                    return;
                }

                setValid(true);
            } catch (RuntimeException e) {
                setValid(false);
                throw e;
//            Throwable cause = e.getCause();
//            if (cause != null) {
//                if(cause instanceof OWLOntologyCreationException) {
//                    messageArea.setText("Entity already exists");
//                }
//                else {
//                    messageArea.setText(cause.getMessage());
//                }
//            }
//            else {
//                messageArea.setText(e.getMessage());
//            }
            }

        }

        private void setValid(boolean valid) {
            currentlyValid = valid;
            fireVerificationStatusChanged();
        }

        private void fireVerificationStatusChanged() {
            for (InputVerificationStatusChangedListener l : listeners) {
                l.verifiedStatusChanged(currentlyValid);
            }
        }
        
        private String getBasicAuthenticationString() {
            username = this.getEntityUsername();
            password = this.getEntityPassword();
            String authString = username + ":" + password;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            String basicAuth = "Basic " + authStringEnc;
            return basicAuth;
        }
    }
}
