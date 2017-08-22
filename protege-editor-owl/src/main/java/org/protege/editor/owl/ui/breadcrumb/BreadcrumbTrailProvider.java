package org.protege.editor.owl.ui.breadcrumb;

import org.protege.editor.core.util.HandlerRegistration;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 21 Aug 2017
 */
public interface BreadcrumbTrailProvider {

    @Nonnull
    List<Breadcrumb> getBreadcrumbTrail();

    @Nonnull
    JComponent asJComponent();

    @Nonnull
    HandlerRegistration addBreadcrumbTrailChangedHandler(@Nonnull BreadcrumbTrailChangedHandler handler);

    /**
     * Go to the specified breadcrumb in the current trail.  If this breadcrumb is not in the current trail
     * then nothing happens.
     * @param breadcrumb The breadcrumb.
     */
    void goToBreadcrumb(@Nonnull Breadcrumb breadcrumb);
}
