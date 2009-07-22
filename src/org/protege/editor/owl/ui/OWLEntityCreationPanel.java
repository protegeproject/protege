package org.protege.editor.owl.ui;

import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.model.entity.OWLEntityCreationException;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditorPreferences;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLDescriptionAutoCompleter;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owlapi.model.*;

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

    private final int INTERNAL_PADDING = 5;

    private OWLEditorKit owlEditorKit;

    private JTextField textField;

    private JLabel errorLabel;

    private final Icon warningIcon = Icons.getIcon("warning.png");

    private Class<T> type;

    private java.util.List<InputVerificationStatusChangedListener> listeners = new ArrayList<InputVerificationStatusChangedListener>();

    private boolean currentlyValid = true;

    private Timer timer = new Timer(ExpressionEditorPreferences.getInstance().getCheckDelay(), new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            performCheck();
            timer.stop();
        }
    });

    private JLabel uriPreviewLabel;


    public OWLEntityCreationPanel(OWLEditorKit owlEditorKit, String message, Class<T> type) {
        this.owlEditorKit = owlEditorKit;
        this.type = type;
        createUI(message);
    }


    public void setEnabled(boolean b) {
        textField.setEnabled(b);
        super.setEnabled(b);
    }


    public void setName(String name){
        textField.setText(name);
    }


    private void createUI(String message) {
        setLayout(new BorderLayout());

        JPanel entryPanel = new JPanel(new BorderLayout());
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

        if (message != null){
            final JLabel label = new JLabel(message);
            label.setBorder(BorderFactory.createEmptyBorder(INTERNAL_PADDING, INTERNAL_PADDING, INTERNAL_PADDING, INTERNAL_PADDING));
            entryPanel.add(label, BorderLayout.NORTH);
        }

        entryPanel.add(textField, BorderLayout.SOUTH);


        errorLabel = new JLabel("");
        errorLabel.setFont(errorLabel.getFont().deriveFont(10.0f));
        errorLabel.setBorder(BorderFactory.createEmptyBorder(INTERNAL_PADDING, INTERNAL_PADDING, INTERNAL_PADDING, INTERNAL_PADDING));
        errorLabel.setPreferredSize(new Dimension(errorLabel.getPreferredSize().width, 40));

        uriPreviewLabel = new JLabel("");
        uriPreviewLabel.setFont(errorLabel.getFont().deriveFont(10.0f));
        uriPreviewLabel.setBorder(BorderFactory.createEmptyBorder(INTERNAL_PADDING, INTERNAL_PADDING, INTERNAL_PADDING, INTERNAL_PADDING));
        Box previewPanel = new Box(BoxLayout.PAGE_AXIS);
        previewPanel.add(uriPreviewLabel);

        add(entryPanel, BorderLayout.NORTH);
        add(previewPanel, BorderLayout.CENTER);
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
                                                       OWLDatatype.class.isAssignableFrom(type),
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
                                                                                        getBaseIRI());
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


    public IRI getBaseIRI() {
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
            final String name = getEntityName();
            OWLEntityCreationSet<T> changeSet = owlEditorKit.getModelManager().getOWLEntityFactory().preview(type,
                                                                                                             name,
                                                                                                             getBaseIRI());
            URI uri = changeSet.getOWLEntity().getURI();
            uriPreviewLabel.setText(uri.toString());

            currentlyValid = true;

            String warningMessage = null;

            for (OWLOntology ont : owlEditorKit.getOWLModelManager().getActiveOntologies()){
                if (ont.containsEntityReference(uri)){
                    warningMessage = "Warning: this is a pun for an existing entity.";
                    break;
                }
            }
            if (warningMessage == null){
                OWLEntity entity = owlEditorKit.getModelManager().getEntityFinder().getOWLEntity(name);
                if(entity != null){
                    warningMessage = "Warning: an entity with that name already exists.";
                }
                else{
                    clearMessage();
                }
            }

            if (warningMessage != null){
                displayWarningMessage(warningMessage);
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


    public JComponent getFocusComponent() {
        return textField;
    }
}
