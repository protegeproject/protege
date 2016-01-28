package org.protege.editor.owl.model.annotation;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.owl.model.util.DateFormatter;
import org.protege.editor.owl.model.util.ISO8601Formatter;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.DublinCoreVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 27/01/16
 */
public class EntityCreationMetadataPreferencesManager {

    private static final Logger logger = LoggerFactory.getLogger(EntityCreationMetadataPreferencesManager.class);

    private final Preferences preferences;

    private static final String METADATA_ENABLED_KEY = "metadata.enabled";

    private static final String USER_NAME_ANNOTATION_PROPERTY_IRI_KEY = "user.name.annotation.iri";

    private static final String CREATION_DATE_ANNOTATION_PROPERTY_IRI_KEY = "creation.date.annotation.iri";

    private static final String DATE_FORMATTER_CLASS_KEY = "date.formatter";


    public EntityCreationMetadataPreferencesManager(Preferences preferences) {
        this.preferences = preferences;
    }

    public boolean isCreationMetadataEnabled() {
        return preferences.getBoolean(METADATA_ENABLED_KEY, false);
    }

    public void setCreationMetadataEnabled(boolean enabled) {
        preferences.putBoolean(METADATA_ENABLED_KEY, enabled);
    }

    public IRI getUserNameAnnotationPropertyIRI() {
        return getIRI(USER_NAME_ANNOTATION_PROPERTY_IRI_KEY, DublinCoreVocabulary.CREATOR.getIRI());
    }

    public IRI getCreationDateAnnotationPropertyIRI() {
        return getIRI(CREATION_DATE_ANNOTATION_PROPERTY_IRI_KEY, DublinCoreVocabulary.DATE.getIRI());
    }

    public DateFormatter getDateFormatter() {
        String formatterClass = preferences.getString(DATE_FORMATTER_CLASS_KEY, ISO8601Formatter.class.getName());
        try {
            Class<?> cls = Class.forName(formatterClass);
            return (DateFormatter) cls.newInstance();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            logger.error("Could not create date formatter: {}", e.getMessage(), e);
            return new ISO8601Formatter();
        }
    }

    private IRI getIRI(String key, IRI defaultIRI) {
        String value = preferences.getString(key, null);
        if(value != null) {
            return IRI.create(value);
        }
        else {
            return defaultIRI;
        }
    }


}
