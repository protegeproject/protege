package org.protege.editor.owl.ui.annotation;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.annotation.EntityCreationMetadataPreferences;
import org.protege.editor.owl.model.annotation.EntityCreationMetadataPreferencesManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.user.OrcidPreferencesManager;
import org.protege.editor.owl.model.user.UserPreferences;
import org.protege.editor.owl.model.util.DateFormatter;
import org.protege.editor.owl.model.util.ISO8601Formatter;
import org.protege.editor.owl.model.util.TimestampFormatter;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLAutoCompleter;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.vocab.DublinCoreVocabulary;
import org.semanticweb.owlapi.vocab.Namespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 27/01/16
 */
public class EntityCreationMetadataPreferencesPanel extends OWLPreferencesPanel {

    private final static Logger logger = LoggerFactory.getLogger(EntityCreationMetadataPreferences.class);

    private final JCheckBox createdByAnnotationEnabled = new JCheckBox("Annotate new entities with creator (user)");

    private final JTextField createdByPropertyIriField = new AugmentedJTextField(40, "Enter annotation property name");

    private final JCheckBox creationDateAnnotationEnabled = new JCheckBox("Annotate new entities with creation date/time");

    private final JTextField creationDatePropertyIriField = new AugmentedJTextField(40, "Enter annotation property name");

    private final JRadioButton iso8601Radio = new JRadioButton("ISO-8601");

    private final JRadioButton timestampRadio = new JRadioButton("Timestamp");

    private final JRadioButton useUserName = new JRadioButton("Use user name");

    private final JRadioButton useOrcid = new JRadioButton("Use ORCID");

    @Override
    public void initialise() {



        setLayout(new BorderLayout());
        PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        add(panel, BorderLayout.NORTH);

        panel.addGroupComponent(createdByAnnotationEnabled);
        panel.addVerticalPadding();
        panel.addGroup("Creator property");
        panel.addGroupComponent(createdByPropertyIriField);
        panel.addVerticalPadding();
        panel.addGroup("Creator value");
        panel.addGroupComponent(useUserName);
        panel.addGroupComponent(useOrcid);
        panel.closeCurrentButtonRun();

        panel.addSeparator();

        panel.addGroupComponent(creationDateAnnotationEnabled);
        panel.addVerticalPadding();
        panel.addGroup("Date property");
        panel.addGroupComponent(creationDatePropertyIriField);
        panel.addVerticalPadding();
        panel.addGroup("Date value format");
        panel.addGroupComponent(iso8601Radio);
        panel.addGroupComponent(timestampRadio);
        panel.closeCurrentButtonRun();

        OWLEditorKit editorKit = getOWLEditorKit();
        OWLModelManager modelManager = getOWLModelManager();
        new OWLAutoCompleter(editorKit, createdByPropertyIriField, createExpressionChecker(modelManager));
        new OWLAutoCompleter(editorKit, creationDatePropertyIriField, createExpressionChecker(modelManager));

        Preferences prefs = EntityCreationMetadataPreferences.get();
        EntityCreationMetadataPreferencesManager prefsManager = new EntityCreationMetadataPreferencesManager(prefs);

        createdByAnnotationEnabled.setSelected(prefsManager.isCreatedByAnnotationEnabled());
        createdByPropertyIriField.setText(renderIRI(prefsManager.getCreatedByAnnotationPropertyIRI()));

        creationDateAnnotationEnabled.setSelected(prefsManager.isCreationDateAnnotationEnabled());
        creationDatePropertyIriField.setText(renderIRI(prefsManager.getCreationDateAnnotationPropertyIRI()));

        DateFormatter dateFormatter = prefsManager.getDateFormatter();

        if(dateFormatter instanceof ISO8601Formatter) {
            iso8601Radio.setSelected(true);
        }
        else if(dateFormatter instanceof TimestampFormatter) {
            timestampRadio.setSelected(true);
        }
        else {
            iso8601Radio.setSelected(true);
        }

        useUserName.setSelected(!prefsManager.isCreatedByValueOrcid());
        useOrcid.setSelected(prefsManager.isCreatedByValueOrcid());

        updateOrcidRadioButton();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                updateOrcidRadioButton();
            }
        });
    }

    private String renderIRI(IRI iri) {
        OWLAnnotationProperty property = getOWLModelManager().getOWLDataFactory().getOWLAnnotationProperty(iri);
        return getOWLModelManager().getRendering(property);
    }

    private void updateOrcidRadioButton() {
        Preferences orcidPreferences = UserPreferences.get();
        OrcidPreferencesManager orcidPreferencesManager = new OrcidPreferencesManager(orcidPreferences);
        boolean orcidPresent = orcidPreferencesManager.getOrcid().isPresent();
        useOrcid.setEnabled(orcidPresent);
    }

    @Override
    public void applyChanges() {
        Preferences prefs = EntityCreationMetadataPreferences.get();
        EntityCreationMetadataPreferencesManager prefsManager = new EntityCreationMetadataPreferencesManager(prefs);

        try {
            prefsManager.setCreatedByAnnotationEnabled(createdByAnnotationEnabled.isSelected());
            Optional<IRI> iri = getIri(createdByPropertyIriField.getText().trim());
            if (iri.isPresent()) {
                prefsManager.setCreatedByAnnotationPropertyIRI(DublinCoreVocabulary.CREATOR.getIRI());
            }
        } catch (URISyntaxException e) {
            logger.warn("Invalid IRI specified for creator annotation property: {}", e.getMessage());
        }

        try {
            prefsManager.setCreationDateAnnotationEnabled(creationDateAnnotationEnabled.isSelected());
            Optional<IRI> iri = getIri(creationDatePropertyIriField.getText().trim());
            if (iri.isPresent()) {
                prefsManager.setCreationDateAnnotationPropertyIRI(iri.get());
            }
            else {
                prefsManager.setCreationDateAnnotationPropertyIRI(DublinCoreVocabulary.DATE.getIRI());
            }
        } catch (URISyntaxException e) {
            logger.warn("Invalid IRI specified for creation date annotation property: {}", e.getMessage());
        }

        if(iso8601Radio.isSelected()) {
            prefsManager.setDateFormatter(new ISO8601Formatter());
        }
        else if(timestampRadio.isSelected()) {
            prefsManager.setDateFormatter(new TimestampFormatter());
        }

        prefsManager.setCreatedByValueOrcid(useOrcid.isSelected());
    }

    private Optional<IRI> getIri(String text) throws URISyntaxException {
        String trimmedText = text.trim();
        if(trimmedText.isEmpty()) {
            return Optional.empty();
        }
        if(text.startsWith("<") && text.endsWith(">")) {
            return Optional.of(IRI.create(new URI(trimmedText.substring(1, trimmedText.length() - 1))));
        }
        for (Namespaces ns : Namespaces.values()) {
            if (trimmedText.startsWith(ns.name().toLowerCase() + ":")) {
                return Optional.of(IRI.create(ns.toString() + text.substring(ns.name().length() + 1)));
            }
        }
        return Optional.of(IRI.create(new URI(trimmedText)));
    }

    @Override
    public void dispose() throws Exception {

    }


    private static OWLExpressionChecker createExpressionChecker(final OWLModelManager modelManager) {
        return new OWLExpressionChecker() {
            @Override
            public void check(String text) throws OWLExpressionParserException {
                if(getOwlAnnotationProperty(text, modelManager) == null) {
                    throw new OWLExpressionParserException(text, 0, text.length(), false, false, false, false, false, true, Collections.<String>emptySet());
                }
            }

            @Override
            public Object createObject(String text) throws OWLExpressionParserException {
                return getOwlAnnotationProperty(text, modelManager);
            }
        };
    }

    private static OWLAnnotationProperty getOwlAnnotationProperty(String text, OWLModelManager modelManager) {
        for(DublinCoreVocabulary dc : DublinCoreVocabulary.values()) {
            if(dc.getPrefixedName().equals(text) || text.equals(dc.getIRI().toString())) {
                return modelManager.getOWLDataFactory().getOWLAnnotationProperty(dc.getIRI());
            }
        }
        return modelManager.getOWLEntityFinder().getOWLAnnotationProperty(text);
    }

}
