package org.protege.editor.owl.model.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.protege.editor.owl.model.OWLModelManager;
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

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toSet;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 2017
 */
public class GitRepository {

    private static final Logger logger = LoggerFactory.getLogger(GitRepository.class);

    @Nonnull
    private final OWLModelManager modelManager;

    @Nullable
    private final Repository repository;

    private GitRepository(@Nonnull OWLModelManager modelManager,
                          @Nullable Repository repository) {
        this.modelManager = checkNotNull(modelManager);
        this.repository = repository;
    }

    public static GitRepository get(@Nonnull OWLModelManager modelManager) {
        Optional<Path> repoDir = getRepositoryDirectory(modelManager);
        if(repoDir.isPresent()) {
            logger.info("[GitRepo] Git repository detected: {}", repoDir.get());
            try {
                Repository repo = new FileRepositoryBuilder()
                        .setGitDir(repoDir.get().toFile())
                        .build();
                return new GitRepository(modelManager, repo);
            } catch (IOException e) {
                logger.warn("[GitRepo] An error occurred whilst build the Git repository: {}", e.getMessage(), e);
                return new GitRepository(modelManager, null);
            }
        }
        else {
            return new GitRepository(modelManager, null);
        }
    }

    public boolean isPresent() {
        return repository != null;
    }

    public Optional<Repository> getRepository() {
        return Optional.ofNullable(repository);
    }

    public Optional<String> getBranchName() {
        if (repository == null) {
            return Optional.empty();
        }
        else {
            try {
                String branchName = repository.getBranch();
                return Optional.of(branchName);
            } catch (IOException e) {
                logger.warn("[GitRepo] An error occurred whilst getting the current branch name: {}", e.getMessage(), e);
                return Optional.empty();
            }
        }
    }

    public boolean isAnyOntologyModified() {
        try {
            if (repository == null) {
                return false;
            }
            else {
                Git git = new Git(repository);
                Status status = git.status().call();
                Set<String> modifiedPaths = status.getModified();
                Path gitDirectory = repository.getDirectory().toPath();
                Path parentDirectory = gitDirectory.getParent();
                Set<Path> ontologyDocumentPaths = modelManager.getOntologies().stream()
                                                              .map(modelManager::getOntologyPhysicalURI)
                                                              .filter(u -> u.getScheme().equals("file"))
                                                              .map(Paths::get)
                                                              .collect(toSet());
                return modifiedPaths.stream()
                                    .map(parentDirectory::resolve)
                                    .filter(ontologyDocumentPaths::contains).findFirst().isPresent();
            }
        } catch (GitAPIException e) {
            logger.warn("[GitRepo] An error occurred whilst getting repository information: ", e.getMessage(), e);
            return false;
        }
    }

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
