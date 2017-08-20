package org.protege.editor.owl.model.git;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 2017
 */
public class GitRepoStatusPresenter {

    private static final Logger logger = LoggerFactory.getLogger(GitRepoStatusPresenter.class);

    @Nonnull
    private final OWLModelManager modelManager;

    @Nonnull
    private final GitStatusView view;

    @Nonnull
    private final OWLModelManagerListener listener = this::handleModelManagerEvent;


    public GitRepoStatusPresenter(@Nonnull OWLModelManager modelManager,
                                  @Nonnull GitStatusView view) {
        this.modelManager = checkNotNull(modelManager);
        this.view = checkNotNull(view);
    }

    @Nonnull
    public GitStatusView getView() {
        return view;
    }

    public void start() {
        modelManager.addListener(listener);
    }

    public void dispose() {
        modelManager.removeListener(listener);
    }

    private void handleModelManagerEvent(OWLModelManagerChangeEvent event) {
        switch (event.getType()) {
            case ACTIVE_ONTOLOGY_CHANGED:
            case ONTOLOGY_LOADED:
            case ONTOLOGY_RELOADED:
            case ONTOLOGY_SAVED:
                updateStatusView();
        }
    }

    public void update() {
        updateStatusView();
    }


    private void updateStatusView() {
        try {
            GitRepositoryManager repository = GitRepositoryManager.get(modelManager);
            if (repository.isGitRepositoryPresent()) {
                Optional<String> branch = repository.getBranchName();
                branch.ifPresent(b -> {
                    logger.info("[GitRepo] On branch: {}", b);
                    view.setBranch(b);
                });
                view.setModified(repository.isAnyOntologyDocumentModified());
                repository.dispose();
            }
            else {
                view.clear();
            }
        } catch (Exception e) {
            logger.error("[GitRepo] An error occurred: {}", e.getMessage(), e);
        }
    }
}
