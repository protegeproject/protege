package org.protege.editor.owl.ui;

import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.model.entity.OWLEntityCreationException;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLDescriptionAutoCompleter;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
public class OWLEntityCreationPanel<T extends OWLEntity> extends JPanel implements VerifiedInputEditor {

    private final int INTERNAL_PADDING = 10;

    private OWLEditorKit owlEditorKit;

    private JTextField textField;

    private JLabel errorLabel;

    private final Icon warningIcon = Icons.getIcon("error.png");

    private Class<T> type;

    private java.util.List<InputVerificationStatusChangedListener> listeners = new ArrayList<InputVerificationStatusChangedListener>();

    private boolean currentlyValid = true;

    private Timer timer = new Timer(500, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            performCheck();
            timer.stop();
        }
    });


    public OWLEntityCreationPanel(OWLEditorKit owlEditorKit, String message, Class<T> type) {
        this.owlEditorKit = owlEditorKit;
        this.type = type;
        createUI(message);
    }


    private void createUI(String message) {
        setLayout(new BorderLayout());

        JPanel entryPanel = new JPanel(new BorderLayout());
        entryPanel.setBorder(BorderFactory.createEmptyBorder(INTERNAL_PADDING, INTERNAL_PADDING, INTERNAL_PADDING, INTERNAL_PADDING));
        textField = new JTextField(30);
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent e) {
                update();
            }

            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });
        entryPanel.add(new JLabel(message), BorderLayout.NORTH);
        entryPanel.add(textField, BorderLayout.SOUTH);


        errorLabel = new JLabel("");
        errorLabel.setFont(errorLabel.getFont().deriveFont(10.0f));
        errorLabel.setBorder(BorderFactory.createEmptyBorder(INTERNAL_PADDING, INTERNAL_PADDING, INTERNAL_PADDING, INTERNAL_PADDING));
        errorLabel.setPreferredSize(new Dimension(errorLabel.getPreferredSize().width, 40));

        add(entryPanel, BorderLayout.NORTH);
        add(errorLabel, BorderLayout.SOUTH);

        OWLDescriptionAutoCompleter completer = new OWLDescriptionAutoCompleter(owlEditorKit, textField, new OWLExpressionChecker() {
            public void check(String text) throws OWLExpressionParserException {
                throw new OWLExpressionParserException(text,
                                                       0,
                                                       text.length(),
                                                       OWLClass.class.isAssignableFrom(type),
                                                       OWLObjectProperty.class.isAssignableFrom(type),
                                                       OWLDataProperty.class.isAssignableFrom(type),
                                                       OWLIndividual.class.isAssignableFrom(type),
                                                       false,
                                                       new HashSet<String>());
            }


            public Object createObject(String text) throws OWLExpressionParserException {
                return null;
            }
        });
    }


    public String getEntityName() {
        return textField.getText();
    }


    public OWLEntityCreationSet<T> getOWLEntityCreationSet() {
        try {
            return owlEditorKit.getModelManager().getOWLEntityFactory().createOWLEntity(type,
                                                                                        getEntityName(),
                                                                                        getBaseURI());
        }
        catch (OWLEntityCreationException e) {
            return null;
        }
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.add(listener);
        performCheck();
        listener.verifiedStatusChanged(currentlyValid);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.remove(listener);
    }


    public static <T extends OWLEntity> OWLEntityCreationSet<T> showDialog(OWLEditorKit owlEditorKit, String message, Class<T> type) {
        OWLEntityCreationPanel panel = new OWLEntityCreationPanel<T>(owlEditorKit, message, type);

        int ret = new UIHelper(owlEditorKit).showValidatingDialog("Create a new " + type.getSimpleName(), panel, panel.textField);

        if (ret == JOptionPane.OK_OPTION) {
            return panel.getOWLEntityCreationSet();
        }
        else {
            return null;
        }
    }


    public URI getBaseURI() {
        return null; // let this be managed by the EntityFactory for now - we could add a selector later
    }


    private void update() {
        currentlyValid = false;
        for (InputVerificationStatusChangedListener l : listeners){
            l.verifiedStatusChanged(currentlyValid);
        }
        timer.restart();
    }


    private void performCheck() {
        boolean wasValid = currentlyValid;
        try{
            owlEditorKit.getModelManager().getOWLEntityFactory().tryCreate(type,
                                                                           getEntityName(),
                                                                           getBaseURI());
            currentlyValid = true;
            OWLEntity entity = owlEditorKit.getModelManager().getOWLEntity(getEntityName());
            if(entity != null){
                displayWarningMessage("Warning: an entity with that name already exists.");
            }
            else{
                clearMessage();
            }
        }
        catch(OWLEntityCreationException e){
            currentlyValid = false;
            handleException(e);

        }
        finally{
            if (wasValid != currentlyValid){
                for (InputVerificationStatusChangedListener l : listeners){
                    l.verifiedStatusChanged(currentlyValid);
                }
            }
        }
    }


    private void handleException(OWLEntityCreationException e) {
        boolean handled = false;
        final Throwable cause = e.getCause();
        if (cause != null){
            if (cause instanceof URISyntaxException){
                handleURISyntaxException((URISyntaxException)cause);
                handled = true;
            }
        }
        if (!handled){
            displayWarningMessage("Error: " + e.getMessage());
        }
    }


    // selects the text where the URI is incorrect
    private void handleURISyntaxException(URISyntaxException e) {
        int actualIndex = e.getIndex();
        String fullURI = e.getInput();
        int indexFromRHS = fullURI.length()-actualIndex;
        int relativeIndex = getEntityName().length() - indexFromRHS;
        textField.setSelectionStart(relativeIndex);
        textField.setSelectionEnd(getEntityName().length());
        displayWarningMessage("Invalid name: " + e.getReason());
    }


    private void displayWarningMessage(String message) {
        errorLabel.setIcon(warningIcon);
        errorLabel.setText("<html>" + message + "</html>");
        errorLabel.validate();
    }


    private void clearMessage() {
        errorLabel.setIcon(null);
        errorLabel.setText("");
        errorLabel.validate();
    }
}
