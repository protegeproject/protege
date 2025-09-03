package org.protege.editor.owl.ui.breadcrumb;

import org.protege.editor.core.util.HandlerRegistration;
import org.protege.editor.owl.model.OWLWorkspace;

import javax.annotation.Nonnull;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 21 Aug 2017
 */
public class BreadcrumbTrailPresenter {

    @Nonnull
    private final OWLWorkspace workspace;

    @Nonnull
    private final BreadcrumbTrailView breadcrumbTrailView;

    private HandlerRegistration handlerRegistration = () -> {};

    public BreadcrumbTrailPresenter(@Nonnull OWLWorkspace workspace,
                                    @Nonnull BreadcrumbTrailView breadcrumbTrailView) {
        this.workspace = checkNotNull(workspace);
        this.breadcrumbTrailView = checkNotNull(breadcrumbTrailView);
    }

    @Nonnull
    public BreadcrumbTrailView getBreadcrumbTrailView() {
        return breadcrumbTrailView;
    }

    public void start() {
        handlerRegistration = workspace.addBreadcrumbTrailChangedHandler(this::updateDisplayedBreadcrumbTrail);
        breadcrumbTrailView.setBreadcrumbClickedHandler(clickedBreadcrumb -> {
            workspace.getBreadcrumbTrailProvider().ifPresent(p -> p.goToBreadcrumb(clickedBreadcrumb));
        });
        updateDisplayedBreadcrumbTrail();
    }

    private void updateDisplayedBreadcrumbTrail() {
        Optional<BreadcrumbTrailProvider> provider = workspace.getBreadcrumbTrailProvider();
        provider.ifPresent(p -> {
            breadcrumbTrailView.setDisplayedBreadcrumbTrail(p.getBreadcrumbTrail());
        });
        if(!provider.isPresent()) {
            breadcrumbTrailView.clearDisplayedBreadcrumbTrail();
        }
    }

    public void dispose() {
        handlerRegistration.removeHandler();
    }
}
