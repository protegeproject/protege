package org.protege.editor.owl.model.annotation;

import org.protege.editor.owl.model.user.DefaultUserNameProvider;
import org.protege.editor.owl.model.user.OrcidPreferencesManager;
import org.protege.editor.owl.model.user.UserNamePreferencesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29/01/16
 */
public class PreferencesBasedEntityCreationMetadataProviderFactory {

    private final UserNamePreferencesManager userNamePreferencesManager;

    private final OrcidPreferencesManager orcidPreferencesManager;

    private final EntityCreationMetadataPreferencesManager metadataPreferencesManager;

    public PreferencesBasedEntityCreationMetadataProviderFactory(UserNamePreferencesManager userNamePreferencesManager, OrcidPreferencesManager orcidPreferencesManager, EntityCreationMetadataPreferencesManager metadataPreferencesManager) {
        this.userNamePreferencesManager = userNamePreferencesManager;
        this.orcidPreferencesManager = orcidPreferencesManager;
        this.metadataPreferencesManager = metadataPreferencesManager;
    }

    public EntityCreationMetadataProvider getProvider() {
        List<AnnotationProvider> fields = new ArrayList<>();
        if(metadataPreferencesManager.isCreatedByAnnotationEnabled()) {
            AnnotationProvider provider = getCreatedByAnnotationProvider();
            fields.add(provider);
        }
        if(metadataPreferencesManager.isCreationDateAnnotationEnabled()) {
            AnnotationProvider provider = getCreationDateAnnotationProvider();
            fields.add(provider);
        }
        return new SimpleEntityCreationMetadataProvider(fields);
    }

    private AnnotationProvider getCreationDateAnnotationProvider() {
        return new AnnotationProvider(
                        new DefaultDateAnnotationPropertyIriProvider(metadataPreferencesManager),
                        new DateAnnotationValueProvider(metadataPreferencesManager.getDateFormatter())
                );
    }

    private AnnotationProvider getCreatedByAnnotationProvider() {
        AnnotationValueProvider createdByValueProvider;
        if(metadataPreferencesManager.isCreatedByValueOrcid() && orcidPreferencesManager.getOrcid().isPresent()) {
            createdByValueProvider = new OrcidAnnotationValueProvider(orcidPreferencesManager);
        }
        else {
            createdByValueProvider = new UserNameAnnotationValueProvider(
                    new DefaultUserNameProvider(
                            userNamePreferencesManager,
                            System.getProperties()
                    )
            );
        }
        return new AnnotationProvider(
                new DefaultUserNameAnnotationPropertyIriProvider(metadataPreferencesManager),
                createdByValueProvider);
    }
}
