package org.protege.editor.owl.model.idrange;

import com.google.common.collect.ImmutableList;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.EntityCreationPreferences;
import org.protege.editor.owl.model.git.GitRepositoryManager;
import org.protege.editor.owl.model.user.DefaultUserNameProvider;
import org.protege.editor.owl.model.user.UserNamePreferencesManager;
import org.protege.editor.owl.model.user.UserNameProvider;
import org.protege.editor.owl.model.user.UserPreferences;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-25
 */
public class ActiveOntologyIdRangesPolicyManager {

    private static final Logger logger = LoggerFactory.getLogger(ActiveOntologyIdRangesPolicyManager.class);

    private static final String ID_RANGES_FILE_SUFFIX = "-idranges.owl";

    @Nonnull
    private final OWLModelManager modelManager;

    private ImmutableList<IdRangesPolicy> idRangesPolicies = ImmutableList.of();

    public ActiveOntologyIdRangesPolicyManager(@Nonnull OWLModelManager modelManager) {
        this.modelManager = checkNotNull(modelManager);
    }

    public ImmutableList<IdRangesPolicy> getIdRangesPolicies() {
        return idRangesPolicies;
    }

    public void reload() {
        EntityCreationPreferences.setPolicyRangeName(null);
        idRangesPolicies = ImmutableList.of();
        OWLOntology activeOntology = modelManager.getActiveOntology();
        URI activeOntologyLocation = modelManager.getOntologyPhysicalURI(activeOntology);
        if(!isFileUri(activeOntologyLocation)) {
            return;
        }
        Path activeOntologyPath = Paths.get(activeOntologyLocation);
        if(!Files.exists(activeOntologyPath)) {
            return;
        }
        Path activeOntologyDirectory = activeOntologyPath.getParent();
        try {
            List<IdRangesPolicy> policies = Files
                    .list(activeOntologyDirectory)
                    .filter(this::isIdRangesFile)
                    .peek(path -> logger.info("[IdRanges] Loading id ranges policy from {}", path))
                    .map(this::toOntology)
                    .map(this::toIdRangesPolicy)
                    .filter(Objects::nonNull)
                    .collect(toList());
            this.idRangesPolicies = ImmutableList.copyOf(policies);
            updateNewEntitiesPreferences();
        } catch(IOException e) {
            logger.warn("An error ocurred whilst listing the active ontology directory contents: {}", e.getMessage());
        }
    }

    public void updateNewEntitiesPreferences() {
        logger.info("[IdRanges] Updating entity creation preferences");
        idRangesPolicies.stream().findFirst().ifPresent(policy -> {
            IdPolicyEntityCreationPreferencesUpdater updater =
                    new IdPolicyEntityCreationPreferencesUpdater(policy,
                                                                 new NoRangeForUserNameHandlerUi(),
                                                                 getUserNameProvider());
            updater.updatePreferences();
        });
    }

    private UserNameProvider getUserNameProvider() {
        return new DefaultUserNameProvider(
                GitRepositoryManager.get(modelManager),
                new UserNamePreferencesManager(UserPreferences.get()),
                System.getProperties()
        );
    }

    private boolean isIdRangesFile(Path path) {
        return path.toFile().getName().endsWith(ID_RANGES_FILE_SUFFIX);
    }

    private static boolean isFileUri(URI activeOntologyLocation) {
        return "file".equalsIgnoreCase(activeOntologyLocation.getScheme());
    }

    @Nullable
    private OWLOntology toOntology(@Nonnull Path path) {
        try(InputStream is = new BufferedInputStream(Files.newInputStream(path))) {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            return manager.loadOntologyFromOntologyDocument(new StreamDocumentSource(is));
        } catch(Exception e) {
            logger.warn("[IdRanges] An error occurred whilst loading the Id ranges policy ontology at {}.  Details: {}", path, e.getMessage());
            return null;
        }
    }

    @Nullable
    private IdRangesPolicy toIdRangesPolicy(@Nullable OWLOntology ontology) {
        if(ontology == null) {
            return null;
        }
        try {
            return IdRangesPolicyParser.get(ontology).parse();
        } catch(IdRangesPolicyParseException e) {
            logger.warn("Count not parse Id ranges policy ontology: {}", e.getMessage());
            return null;
        }
    }
}
