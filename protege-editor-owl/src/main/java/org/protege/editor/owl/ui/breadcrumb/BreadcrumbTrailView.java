package org.protege.editor.owl.ui.breadcrumb;

import org.protege.editor.core.util.ClickHandler;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 21 Aug 2017
 */
public interface BreadcrumbTrailView {

    void setDisplayedBreadcrumbTrail(@Nonnull List<Breadcrumb> path);

    void setBreadcrumbClickedHandler(@Nonnull ClickHandler<Breadcrumb> clickedHandler);

    void clearDisplayedBreadcrumbTrail();

    @Nonnull
    JComponent asJComponent();
}
