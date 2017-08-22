package org.protege.editor.owl.ui.breadcrumb;

import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 22 Aug 2017
 */
public interface HasBreadcrumbTrailProvider {

    Optional<BreadcrumbTrailProvider> getBreadcrumbTrailProvider();
}
