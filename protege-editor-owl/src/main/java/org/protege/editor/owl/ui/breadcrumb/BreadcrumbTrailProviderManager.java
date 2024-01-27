package org.protege.editor.owl.ui.breadcrumb;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.protege.editor.core.util.HandlerRegistration;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 22 Aug 2017
 */
public class BreadcrumbTrailProviderManager {

    @Nonnull
    private final OWLWorkspace workspace;

    private final List<BreadcrumbTrailProvider> registeredProviders = new ArrayList<>();

    private final List<BreadcrumbTrailChangedHandler> breadcrumbTrailChangedHandlers = new ArrayList<>();

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Nonnull
    private Optional<BreadcrumbTrailProvider> currentProvider = Optional.empty();

    public BreadcrumbTrailProviderManager(@Nonnull OWLWorkspace workspace) {
        this.workspace = checkNotNull(workspace);
    }

    public void start() {

    }

    @Nonnull
    public HandlerRegistration addBreadcrumbTrailChangedHandler(@Nonnull BreadcrumbTrailChangedHandler handler) {
        breadcrumbTrailChangedHandlers.add(handler);
        return () -> {
            breadcrumbTrailChangedHandlers.remove(handler);
        };
    }

    @Nonnull
    public HandlerRegistration registerBreadcrumbTrailProvider(@Nonnull BreadcrumbTrailProvider provider) {
        // Add provider to our list
        registeredProviders.add(checkNotNull(provider));
        provider.asJComponent().addHierarchyListener(e -> {
            Optional<BreadcrumbTrailProvider> activeProvider = getActiveProvider();
            if(!activeProvider.equals(currentProvider)) {
                currentProvider = activeProvider;
                fireBreadcrumbTrailChanged();
            }
        });
        fireBreadcrumbTrailChanged();
        // Listen for trail changed and pass on the handling if it changes
        HandlerRegistration reg = provider.addBreadcrumbTrailChangedHandler(this::fireBreadcrumbTrailChanged);
        return () -> {
            registeredProviders.remove(provider);
            reg.removeHandler();
        };
    }

    @Nonnull
    public Optional<BreadcrumbTrailProvider> getActiveProvider() {
        return registeredProviders.stream()
                                  .filter(prov -> prov.asJComponent().isShowing())
                                  .findFirst();
    }

    private void fireBreadcrumbTrailChanged() {
        new ArrayList<>(breadcrumbTrailChangedHandlers).forEach(BreadcrumbTrailChangedHandler::handleBreadcrumbTrailChanged);
    }
}
