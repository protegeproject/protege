package org.protege.editor.owl.ui.breadcrumb;

import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.JComponent;

import org.protege.editor.core.util.ClickHandler;

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
