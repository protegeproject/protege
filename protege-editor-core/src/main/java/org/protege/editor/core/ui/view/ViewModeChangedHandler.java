package org.protege.editor.core.ui.view;

import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 26/01/16
 */
public interface ViewModeChangedHandler {

    void handleViewModeChanged(Optional<ViewMode> viewMode);
}
