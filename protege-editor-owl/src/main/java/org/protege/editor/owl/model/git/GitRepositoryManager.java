package org.protege.editor.owl.model.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.user.UserNameProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toSet;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 2017
 * <p>
 * A {@link GitRepositoryManager} is associated with an {@link OWLModelManager} and manage an
 * underlying Git repository if present.  The underlying repository is determined based on
 * the root ontology document IRI.  If the document IRI is contained within an ancestor directory
 * that contains a .git directory then this is taken to be the underlying Git repository.
 */
public class GitRepositoryManager implements UserNameProvider {

    private static final Logger logger = LoggerFactory.getLogger(GitRepositoryManager.class);

    private static final String USER_SECTION = "user";

    private static final String USER_NAME = "name";

    @Nonnull
    private final OWLModelManager modelManager;

    @Nullable
    private final Repository repository;

    /**
     * Constructs a {@link GitRepositoryManager} wrapper around a given model manager and repository
     *
     * @param modelManager The model manager.
     * @param repository   The repository.  This may be null - in this case a NoOp {@link GitRepositoryManager}
     *                     will be created.
     */
    private GitRepositoryManager(@Nonnull OWLModelManager modelManager,
                                 @Nullable Repository repository) {
        this.modelManager = checkNotNull(modelManager);
        this.repository = repository;
    }

    /**
     * Creates a {@link GitRepositoryManager} based on the specified model manager.
     *
     * @param modelManager The model manage.
     * @return The {@link GitRepositoryManager} for the specified model manager.
     */
    @Nonnull
    public static GitRepositoryManager get(@Nonnull OWLModelManager modelManager) {
        Optional<Path> repoDir = getRepositoryDirectory(checkNotNull(modelManager));
        if (repoDir.isPresent()) {
            logger.info("[GitRepo] Git repository detected: {}", repoDir.get());
            try {
                Repository repo = new FileRepositoryBuilder()
                        .setGitDir(repoDir.get().toFile())
                        .build();
                return new GitRepositoryManager(modelManager, repo);
            } catch (IOException e) {
                logger.warn("[GitRepo] An error occurred whilst build the Git repository: {}", e.getMessage(), e);
                return new GitRepositoryManager(modelManager, null);
            }
        }
        else {
            return new GitRepositoryManager(modelManager, null);
        }
    }

    /**
     * Determines if the underlying Git repository is present.
     *
     * @return true if the underlying Git repository is present, otherwise false.
     */
    public boolean isGitRepositoryPresent() {
        return repository != null;
    }

    /**
     * Gets the underlying Git repository.
     *
     * @return The underlying Git repository, which may or may not be present.
     */
    @Nonnull
    public Optional<Repository> getRepository() {
        return Optional.ofNullable(repository);
    }

    /**
     * Gets the current branch that is checked out in the underlying Git repository.
     *
     * @return The current branch name.  An empty value will be returned if there is no underlying Git repository.
     */
    @Nonnull
    public Optional<String> getBranchName() {
        if (repository == null) {
            return Optional.empty();
        }
        else {
            try {
                String branchName = repository.getBranch();
                return Optional.of(branchName);
            } catch (IOException e) {
                logger.warn("[GitRepo] An error occurred whilst getting the current branch name: {}",
                            e.getMessage(),
                            e);
                return Optional.empty();
            }
        }
    }

    /**
     * Gets the user name for underlying Git repository.
     * @return The username or an empty value if there is no underlying Git repository.
     */
    @Nonnull
    public Optional<String> getUserName() {
        return getRepository().map(repo -> repo.getConfig().getString(USER_SECTION, null, USER_NAME));
    }

    /**
     * Determines if an ontology document has been modified.  The ontology document will be a document in the Git
     * repository that is the document for an ontology loaded into the model manager that this repository was created
     * over.
     *
     * @return true if there is at least one ontology document that has been modified (according to the above criteria)
     * otherwise false.
     */
    public boolean isAnyOntologyDocumentModified() {
        if (repository == null) {
            return false;
        }
        Stream<Path> ontologyDocumentPathsStream = getModifiedOntologyDocumentPaths();
        return ontologyDocumentPathsStream.findFirst().isPresent();
    }

    /**
     * Gets a stream of {@link Path}s that correspond to modified ontology document paths.
     *
     * @return A stream of modified ontology document paths.
     */
    private Stream<Path> getModifiedOntologyDocumentPaths() {
        try {
            if (repository == null) {
                return Stream.empty();
            }
            Git git = new Git(repository);
            Status status = git.status().call();
            Path gitDirectory = repository.getDirectory().toPath();
            Path parentDirectory = gitDirectory.getParent();
            Stream<String> paths = Stream.concat(status.getModified().stream(),
                                                 status.getChanged().stream());
            Set<Path> ontologyDocumentPaths = getFileBasedOntologyDocumentPaths();
            return paths.map(parentDirectory::resolve)
                        .filter(ontologyDocumentPaths::contains);
        } catch (GitAPIException e) {
            logger.warn("[GitRepo] An error occurred whilst getting repository information: ", e.getMessage(), e);
            return Stream.empty();
        }
    }

    private Set<Path> getFileBasedOntologyDocumentPaths() {
        return modelManager.getOntologies().stream()
                           .map(modelManager::getOntologyPhysicalURI)
                           .filter(u -> u.getScheme().equals("file"))
                           .map(Paths::get)
                           .collect(toSet());
    }

    /**
     * Disposes of this manager and closes any connection to the underlying repository.
     */
    public void dispose() {
        getRepository().ifPresent(Repository::close);
    }

    private static Optional<Path> getRepositoryDirectory(@Nonnull OWLModelManager modelManager) {
        URI ontologyDocumentURI = modelManager.getOntologyPhysicalURI(modelManager.getActiveOntology());
        if (!"file".equals(ontologyDocumentURI.getScheme())) {
            return Optional.empty();
        }
        Path ontologyDocumentPath = Paths.get(ontologyDocumentURI);
        Path parentPath = ontologyDocumentPath.getParent();
        while (parentPath != null) {
            Path gitDirectoryPath = parentPath.resolve(".git");
            if (Files.isDirectory(gitDirectoryPath)) {
                return Optional.of(gitDirectoryPath);
            }
            parentPath = parentPath.getParent();
        }
        return Optional.empty();
    }
}
