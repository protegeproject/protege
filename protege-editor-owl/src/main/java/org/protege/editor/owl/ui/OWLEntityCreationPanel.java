package org.protege.editor.owl.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.CustomOWLEntityFactory;
import org.protege.editor.owl.model.entity.OWLEntityCreationException;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.util.OboUtilities;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditorPreferences;
import org.protege.editor.owl.ui.preferences.NewEntitiesPreferencesPanel;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.vocab.Namespaces;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityCreationPanel<T extends OWLEntity> extends JPanel implements VerifiedInputEditor {
	
	public enum EntityCreationMode {
		PREVIEW, CREATE
    }

    /**
     *
     */
    private static final long serialVersionUID = -2790553738912229896L;

    public static final int FIELD_WIDTH = 40;

    private OWLEditorKit owlEditorKit;

    private JTextField userSuppliedNameField;

    private Class<T> type;

    private java.util.List<InputVerificationStatusChangedListener> listeners = new ArrayList<>();

    private boolean currentlyValid = true;

    private Timer timer = new Timer(ExpressionEditorPreferences.getInstance().getCheckDelay(), new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            timer.stop();
        }
    });

    private final AugmentedJTextField entityIRIField = new AugmentedJTextField(FIELD_WIDTH, "IRI (auto-generated)");

    private final JTextArea messageArea = new JTextArea(1, FIELD_WIDTH);



    /**
     * Create a new entity creation panel.
     * @param owlEditorKit The relevant editor kit.
     * @param message Not used.
     * @param type The type of entity to be created.
     */
    @Deprecated
    public OWLEntityCreationPanel(@Nonnull OWLEditorKit owlEditorKit, @Deprecated String message, @Nonnull Class<T> type) {
        this.owlEditorKit = owlEditorKit;
        this.type = type;
        createUI();
    }

    /**
     * Create a new entity creation panel.
     * @param owlEditorKit The relevant editor kit.
     * @param type The type of entity to be created.
     */
    public OWLEntityCreationPanel(@Nonnull OWLEditorKit owlEditorKit, @Nonnull Class<T> type) {
        this.owlEditorKit = owlEditorKit;
        this.type = type;
        createUI();
    }


    public void setEnabled(boolean b) {
        userSuppliedNameField.setEnabled(b);
        super.setEnabled(b);
    }


    public void setName(String name) {
        userSuppliedNameField.setText(name);
    }


    private void createUI() {
        setLayout(new BorderLayout());
        JPanel holder = new JPanel(new GridBagLayout());
        add(holder);
        Insets insets = new Insets(0, 0, 2, 4);

        int rowIndex = 0;

        JLabel nameLabel = new JLabel("Name");
        holder.add(nameLabel, new GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, insets, 0, 0));
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));

        userSuppliedNameField = new AugmentedJTextField(30, "Short name or full IRI or Prefix-Name or OBO Id");
        userSuppliedNameField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent e) {
                update();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });


        holder.add(userSuppliedNameField, new GridBagConstraints(1, rowIndex, 1, 1, 100.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, insets, 0, 0));


        rowIndex++;
        holder.add(new JSeparator(), new GridBagConstraints(0, rowIndex, 2, 1, 100.0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 2, 10, 2), 0, 0));

        rowIndex++;
        JLabel iriLabel = new JLabel("IRI");
        holder.add(iriLabel, new GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, insets, 0, 0));
        entityIRIField.setForeground(Color.GRAY);
        entityIRIField.setEditable(false);
        holder.add(entityIRIField, new GridBagConstraints(1, rowIndex, 1, 1, 100.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.HORIZONTAL, insets, 0, 0));


        rowIndex++;
        holder.add(new JButton(new AbstractAction("New entity options...") {
            public void actionPerformed(ActionEvent e) {
                showEntityCreationPreferences();
            }
        }), new GridBagConstraints(1, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(7, 0, 0, 0), 0, 0));


        rowIndex++;
//        holder.add(new JSeparator(), new GridBagConstraints(0, rowIndex, 2, 1, 100.0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 2, 5, 2), 0, 0));

        rowIndex++;
        messageArea.setBackground(null);
        messageArea.setBorder(null);
        messageArea.setEditable(false);
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        messageArea.setFont(messageArea.getFont().deriveFont(12.0f));
        messageArea.setForeground(Color.RED);
        holder.add(messageArea, new GridBagConstraints(0, rowIndex, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 2, 0, 2), 0, 0));

        
        update();
    }


    private void showEntityCreationPreferences() {
        try {
            NewEntitiesPreferencesPanel panel = new NewEntitiesPreferencesPanel();
            panel.setup("Entity creation preferences", owlEditorKit);

            panel.initialise();

            int ret = JOptionPane.showConfirmDialog(this, panel, "Entity Creation Preferences", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ret == JOptionPane.OK_OPTION) {
                panel.applyChanges();
                update();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Determines whether the entered value is an OBO Id
     * @return true if the entered value is an OBO Id otherwise false.
     */
    private boolean isOboId() {
        String enteredValue = getEntityName();
        return !enteredValue.isEmpty() && OboUtilities.isOboId(enteredValue);
    }

    /**
     * Determines if the entity name represents an IRI rather than a short name.
     * @return <code>true</code> if the entity name represents an IRI rather than a short name, otherwise
     * <code>false</code>.
     */
    public boolean isEntityIRI() {
        String entityName = getEntityName();
        for (Namespaces ns : Namespaces.values()) {
            if (entityName.startsWith(ns.name().toLowerCase() + ":")) {
                return true;
            }
        }
        OWLModelManager owlModelManager = owlEditorKit.getOWLModelManager();
        OWLOntologyManager owlOntologyManager = owlModelManager.getOWLOntologyManager();
        for(OWLOntology ont : owlModelManager.getActiveOntologies()) {
            OWLDocumentFormat format = owlOntologyManager.getOntologyFormat(ont);
            if(format != null && format.isPrefixOWLOntologyFormat()) {
                PrefixDocumentFormat prefixFormat = format.asPrefixOWLOntologyFormat();
                for(String prefix : prefixFormat.getPrefixNames()) {
                    if(entityName.startsWith(prefix)) {
                        return true;
                    }
                }
            }
        }
        try {
            URI uri = new URI(entityName);
            return uri.isAbsolute() && uri.getPath() != null;
        }
        catch (URISyntaxException e) {
            return false;
        }
    }

    public String getEntityName() {
        return userSuppliedNameField.getText().trim();
    }


    /**
     * Gets the entity creation set
     * @return The entity creation set
     * @throws RuntimeException which wraps an {@link OWLEntityCreationException} if there was a problem
     */
    public OWLEntityCreationSet<T> getOWLEntityCreationSet() throws RuntimeException {
    	return getOWLEntityCreationSet(EntityCreationMode.CREATE);
    }
    
    public OWLEntityCreationSet<T> getOWLEntityCreationSet(EntityCreationMode preview) throws RuntimeException {
        try {
            if (isEntityIRI()) {
                IRI iri = getRawIRI();
                return getCreationSetForIri(iri);
            }
            else if(isOboId()) {
                IRI iri = OboUtilities.getOboLibraryIriFromOboId(getEntityName());
                if(owlEditorKit.getOWLModelManager().getActiveOntology().containsEntityInSignature(iri, Imports.INCLUDED)) {
                    throw new OWLEntityCreationException("Entity already exists: " + iri);
                }
                return getCreationSetForIri(iri);
            }
            else {
            	switch (preview) {
            	case CREATE:
                    return owlEditorKit.getModelManager().getOWLEntityFactory().createOWLEntity(type, getEntityName(), getBaseIRI());
            	case PREVIEW: 
                    return owlEditorKit.getModelManager().getOWLEntityFactory().preview(type, getEntityName(), getBaseIRI());
            	default:
            		throw new IllegalStateException("Programmer error - report this (with stack trace) to the Protege 4 mailing list");
            	}
            }
        }
        catch (OWLEntityCreationException e) {
            throw new RuntimeException(e);
        }
    }

    private OWLEntityCreationSet<T> getCreationSetForIri(IRI iri) {
        OWLOntology ontology = owlEditorKit.getModelManager().getActiveOntology();
        OWLDataFactory factory = owlEditorKit.getModelManager().getOWLDataFactory();
        T owlEntity = CustomOWLEntityFactory.getOWLEntity(factory, type, iri);
        OWLOntologyChange addDecl = new AddAxiom(ontology, factory.getOWLDeclarationAxiom(owlEntity));
        return new OWLEntityCreationSet<>(owlEntity, Collections.singletonList(addDecl));
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.add(listener);
        listener.verifiedStatusChanged(currentlyValid);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.remove(listener);
    }


    /**
     * @deprecated Use the showDialog method that does not accept a message as a parameter.
     */
    @Deprecated
    public static <T extends OWLEntity> OWLEntityCreationSet<T> showDialog(@Nonnull OWLEditorKit owlEditorKit, @Nullable String message, @Nonnull Class<T> type) {
        return showDialog(owlEditorKit, type);
    }

    /**
     * Display a dialog asking a user to create a new entity of a given type.
     * @param owlEditorKit The relevant editor kit.
     * @param type The type.
     * @param <T> The entity type.
     * @return The OWLEntityCreationSet that can be used to perform the actual creation, or {@code null} if the dialog
     * display was cancelled by the user.
     */
    @Nullable
    public static <T extends OWLEntity> OWLEntityCreationSet<T> showDialog(@Nonnull OWLEditorKit owlEditorKit, @Nonnull Class<T> type) {
        OWLEntityCreationPanel<T> panel = new OWLEntityCreationPanel<>(owlEditorKit, type);
        int ret = new UIHelper(owlEditorKit).showValidatingDialog(
                "Create a new " + getTypeName(type),
                panel,
                panel.userSuppliedNameField);
        if (ret == JOptionPane.OK_OPTION) {
            return panel.getOWLEntityCreationSet();
        }
        else {
            return null;
        }
    }

    private static  <T extends OWLEntity>  String getTypeName(Class<T> type) {
        if(type.equals(OWLClass.class)) {
            return EntityType.CLASS.getPrintName();
        }
        else if(type.equals(OWLObjectProperty.class)) {
            return EntityType.OBJECT_PROPERTY.getPrintName();
        }
        else if(type.equals(OWLDataProperty.class)) {
            return EntityType.DATA_PROPERTY.getPrintName();
        }
        else if(type.equals(OWLAnnotationProperty.class)) {
            return EntityType.ANNOTATION_PROPERTY.getPrintName();
        }
        else if(type.equals(OWLDatatype.class)) {
            return EntityType.DATATYPE.getPrintName();
        }
        else if(type.equals(OWLNamedIndividual.class)) {
            return EntityType.NAMED_INDIVIDUAL.getPrintName();
        }
        else {
            return type.getSimpleName();
        }
    }


    public IRI getBaseIRI() {
        return null; // let this be managed by the EntityFactory for now - we could add a selector later
    }


    private void update() {
        try {

            entityIRIField.setText("");
            messageArea.setText("");
            if (userSuppliedNameField.getText().trim().isEmpty()) {
                setValid(false);
                return;
            }
            OWLEntityCreationSet<?> creationSet = getOWLEntityCreationSet(EntityCreationMode.PREVIEW);
            if(creationSet == null) {
                setValid(false);
                return;
            }
            OWLEntity owlEntity = creationSet.getOWLEntity();
            String iriString = owlEntity.getIRI().toString();
            entityIRIField.setText(iriString);
            setValid(true);
        }
        catch (RuntimeException e) {
            setValid(false);
            Throwable cause = e.getCause();
            if (cause != null) {
                if(cause instanceof OWLOntologyCreationException) {
                    messageArea.setText("Entity already exists");
                }
                else {
                    messageArea.setText(cause.getMessage());
                }
            }
            else {
                messageArea.setText(e.getMessage());
            }
        }

    }

    private void setValid(boolean valid) {
        currentlyValid = valid;
        fireVerificationStatusChanged();
    }

    private void fireVerificationStatusChanged() {
        for (InputVerificationStatusChangedListener l : listeners){
            l.verifiedStatusChanged(currentlyValid);
        }
    }



    private IRI getRawIRI() {
        String text = getEntityName();
        OWLOntology activeOntology = owlEditorKit.getModelManager().getActiveOntology();
        OWLOntologyManager manager = owlEditorKit.getModelManager().getOWLOntologyManager();
        OWLDocumentFormat format = manager.getOntologyFormat(activeOntology);
        for (Namespaces ns : Namespaces.values()) {
            if (text.startsWith(ns.name().toLowerCase() + ":")) {
                return IRI.create(ns.toString() + text.substring(ns.name().length() + 1));
            }
        }
        int colonIndex = text.indexOf(':');
        if (colonIndex >= 0 && format != null && format.isPrefixOWLOntologyFormat()) {
            PrefixDocumentFormat prefixes = format.asPrefixOWLOntologyFormat();
            String prefixName = text.substring(0, colonIndex + 1);
            String prefix = prefixes.getPrefix(prefixName);
            if (prefix != null) {
                return IRI.create(prefix + text.substring(colonIndex + 1));
            }
        }
        return IRI.create(text);
    }


    public JComponent getFocusComponent() {
        return userSuppliedNameField;
    }
}
