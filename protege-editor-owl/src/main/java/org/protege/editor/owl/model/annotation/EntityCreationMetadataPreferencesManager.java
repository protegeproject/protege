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

    private static final String CREATED_BY_ANNOTATION_ENABLED_KEY = "created.by.enabled";

    private static final String CREATED_BY_ANNOTATION_PROPERTY_IRI_KEY = "created.by.annotation.iri";

    private static final String CREATED_BY_USE_ORCID = "created.by.use.orcid";

    private static final String CREATED_BY_USE_GIT_USERNAME = "created.by.use.git.username";


    private static final String CREATION_DATE_ANNOTATION_ENABLED_KEY = "creation.date.enabled";

    private static final String CREATION_DATE_ANNOTATION_PROPERTY_IRI_KEY = "creation.date.annotation.iri";

    private static final String DATE_FORMATTER_CLASS_KEY = "creation.date.formatter";

    private DateFormatter dateFormatter = null;

    public EntityCreationMetadataPreferencesManager(Preferences preferences) {
        this.preferences = preferences;
    }

    public boolean isCreatedByAnnotationEnabled() {
        return preferences.getBoolean(CREATED_BY_ANNOTATION_ENABLED_KEY, false);
    }

    public void setCreatedByAnnotationEnabled(boolean enabled) {
        preferences.putBoolean(CREATED_BY_ANNOTATION_ENABLED_KEY, enabled);
    }

    public boolean isCreationDateAnnotationEnabled() {
        return preferences.getBoolean(CREATION_DATE_ANNOTATION_ENABLED_KEY, false);
    }

    public boolean isCreatedByValueOrcid() {
        return preferences.getBoolean(CREATED_BY_USE_ORCID, false);
    }

    public void setCreatedByValueOrcid(boolean b) {
        preferences.putBoolean(CREATED_BY_USE_ORCID, b);
    }

    public void setCreationDateAnnotationEnabled(boolean enabled) {
        preferences.putBoolean(CREATION_DATE_ANNOTATION_ENABLED_KEY, enabled);
    }

    public boolean isCreatedByValueGitUserName() {
        return preferences.getBoolean(CREATED_BY_USE_GIT_USERNAME, false);
    }

    public void setCreatedByValueGitUserName(boolean b) {
        preferences.putBoolean(CREATED_BY_USE_GIT_USERNAME, b);
    }

    public IRI getCreatedByAnnotationPropertyIRI() {
        return getIRI(CREATED_BY_ANNOTATION_PROPERTY_IRI_KEY, DublinCoreVocabulary.CREATOR.getIRI());
    }

    public void setCreatedByAnnotationPropertyIRI(IRI iri) {
        preferences.putString(CREATED_BY_ANNOTATION_PROPERTY_IRI_KEY, iri.toString());
    }


    public IRI getCreationDateAnnotationPropertyIRI() {
        return getIRI(CREATION_DATE_ANNOTATION_PROPERTY_IRI_KEY, DublinCoreVocabulary.DATE.getIRI());
    }

    public void setCreationDateAnnotationPropertyIRI(IRI iri) {
        preferences.putString(CREATION_DATE_ANNOTATION_PROPERTY_IRI_KEY, iri.toString());
    }

    public DateFormatter getDateFormatter() {
        if(dateFormatter == null) {
            dateFormatter = instantiateDateFormatter();
        }
        return dateFormatter;
    }

    private DateFormatter instantiateDateFormatter() {
        String formatterClass = preferences.getString(DATE_FORMATTER_CLASS_KEY, ISO8601Formatter.class.getName());
        try {
            Class<?> cls = Class.forName(formatterClass);
            return (DateFormatter) cls.newInstance();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            logger.error("Could not create date formatter: {}", e.getMessage(), e);
            return new ISO8601Formatter();
        }
    }

    public void setDateFormatter(DateFormatter dateFormatter) {
        preferences.putString(DATE_FORMATTER_CLASS_KEY, dateFormatter.getClass().getName());
        this.dateFormatter = dateFormatter;
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
