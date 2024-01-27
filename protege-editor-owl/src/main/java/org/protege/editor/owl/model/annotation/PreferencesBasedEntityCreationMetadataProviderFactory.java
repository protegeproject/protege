package org.protege.editor.owl.model.annotation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.git.GitRepositoryManager;
import org.protege.editor.owl.model.user.DefaultUserNameProvider;
import org.protege.editor.owl.model.user.OrcidPreferencesManager;
import org.protege.editor.owl.model.user.UserNamePreferencesManager;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29/01/16
 */
public class PreferencesBasedEntityCreationMetadataProviderFactory {

    @Nonnull
    private final OWLModelManager modelManager;

    @Nonnull
    private final UserNamePreferencesManager userNamePreferencesManager;

    @Nonnull
    private final OrcidPreferencesManager orcidPreferencesManager;

    @Nonnull
    private final EntityCreationMetadataPreferencesManager metadataPreferencesManager;

    public PreferencesBasedEntityCreationMetadataProviderFactory(@Nonnull OWLModelManager manager,
                                                                 @Nonnull UserNamePreferencesManager userNamePreferencesManager,
                                                                 @Nonnull OrcidPreferencesManager orcidPreferencesManager,
                                                                 @Nonnull EntityCreationMetadataPreferencesManager metadataPreferencesManager) {
        this.modelManager = checkNotNull(manager);
        this.userNamePreferencesManager = checkNotNull(userNamePreferencesManager);
        this.orcidPreferencesManager = checkNotNull(orcidPreferencesManager);
        this.metadataPreferencesManager = checkNotNull(metadataPreferencesManager);
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
                            GitRepositoryManager.get(modelManager),
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
