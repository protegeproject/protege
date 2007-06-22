package org.protege.editor.owl.ui;

import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLDescriptionAutoCompleter;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owl.model.OWLException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.net.URI;
import java.util.HashSet;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityCreationPanel extends JPanel {

    public static final int TYPE_CLASS = 0;

    public static final int TYPE_OBJECT_PROPERTY = 1;

    public static final int TYPE_DATA_PROPERTY = 2;

    public static final int TYPE_INDIVIDUAL = 3;

    private OWLEditorKit owlEditorKit;

    private JTextField textField;

    private JLabel messageLabel;

    private Icon warningIcon = OWLIcons.getIcon("warning.small.png");

    private OWLDescriptionAutoCompleter completer;

    private int type;


    public OWLEntityCreationPanel(OWLEditorKit owlEditorKit, String message, int type) {
        this.owlEditorKit = owlEditorKit;
        this.type = type;
//        owlEditorKit.getOWLModelManager().setOWLEntityFactory(new LabelledOWLEntityFactory(owlEditorKit.getOWLModelManager()));
        createUI(message);
    }


    public String getName() {
        return textField.getText();
    }


    public URI getBaseURI() {
        return owlEditorKit.getOWLModelManager().getActiveOntology().getURI();
    }


    private void createUI(String message) {
        setLayout(new BorderLayout());
        JPanel entryPanel = new JPanel(new BorderLayout(7, 7));
        add(entryPanel, BorderLayout.NORTH);
        entryPanel.add(new JLabel(message));
        textField = new JTextField(30);
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                performCheck();
            }


            public void removeUpdate(DocumentEvent e) {
            }


            public void changedUpdate(DocumentEvent e) {
            }
        });
        entryPanel.add(textField, BorderLayout.SOUTH);
        entryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messageLabel = new JLabel("");
        messageLabel.setFont(messageLabel.getFont().deriveFont(10.0f));
        add(messageLabel, BorderLayout.SOUTH);
        completer = new OWLDescriptionAutoCompleter(owlEditorKit, textField, new OWLExpressionChecker() {
            public void check(String text) throws OWLExpressionParserException, OWLException {
                throw new OWLExpressionParserException(text,
                                                       0,
                                                       text.length(),
                                                       type == TYPE_CLASS,
                                                       type == TYPE_OBJECT_PROPERTY,
                                                       type == TYPE_DATA_PROPERTY,
                                                       type == TYPE_INDIVIDUAL,
                                                       false,
                                                       new HashSet<String>());
            }


            public Object createObject(String text) throws OWLExpressionParserException, OWLException {
                return null;
            }
        });
    }


    private void performCheck() {
    }


    private void displayWarningMessage(String message) {
        messageLabel.setIcon(warningIcon);
        messageLabel.setText(message);
        messageLabel.validate();
    }


    private void clearMessage() {
        messageLabel.setIcon(null);
        messageLabel.setText("");
        messageLabel.validate();
    }


    public URIShortNamePair getUriShortNamePair() {
        if (getName().trim().length() > 0) {
            return new URIShortNamePair(getBaseURI(), getName());
        }
        else {
            return null;
        }
    }


    public class URIShortNamePair {

        private URI uri;

        private String shortName;


        public URIShortNamePair(URI uri, String shortName) {
            this.uri = uri;
            this.shortName = shortName;
        }


        public URI getUri() {
            return uri;
        }


        public String getShortName() {
            return shortName;
        }
    }


    public static URIShortNamePair showDialog(OWLEditorKit owlEditorKit, String message, int type) {
        OWLEntityCreationPanel panel = new OWLEntityCreationPanel(owlEditorKit, message, type);
        int ret = JOptionPaneEx.showConfirmDialog(owlEditorKit.getWorkspace(),
                                                  "Specify name",
                                                  panel,
                                                  JOptionPane.PLAIN_MESSAGE,
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  panel.textField);
        if (ret == JOptionPane.OK_OPTION) {
            return panel.getUriShortNamePair();
        }
        else {
            return null;
        }
    }
}
