package org.protege.editor.owl.model.util;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-12
 */
public interface LowMemoryNotificationView {

    /**
     * Display a low memory notification.  This method will only be called
     * in the Event Dispatch Thread.
     */
    void displayLowMemoryNotification();
}
