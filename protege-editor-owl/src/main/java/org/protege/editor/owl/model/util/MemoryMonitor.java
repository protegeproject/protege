package org.protege.editor.owl.model.util;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;
import javax.swing.SwingUtilities;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-12
 */
public class MemoryMonitor {

    public enum MemoryStatus {
        MEMORY_LOW, MEMORY_OK
    }

    private static final int LOW_MEMORY_THRESHOLD_BYTES = 200_048_576;

    @Nonnull
    private final LowMemoryNotificationView view;

    private boolean displayedLowMemoryNotification;

    public MemoryMonitor(@Nonnull LowMemoryNotificationView view) {
        this.view = checkNotNull(view);
    }

    /**
     * Checks the memory availability and displays a warning message if memory is low
     */
    public MemoryStatus checkMemory() {
        long freeMem = getFreeMemoryInBytes();
        if(freeMem < LOW_MEMORY_THRESHOLD_BYTES) {
            displayLowMemoryNotification();
            return MemoryStatus.MEMORY_LOW;
        }
        else {
            return MemoryStatus.MEMORY_OK;
        }

    }

    private static long getFreeMemoryInBytes() {
        Runtime r = Runtime.getRuntime();
        return r.maxMemory() - r.totalMemory();
    }

    private synchronized void resumeThreads() {
        notifyAll();
    }

    private synchronized void displayLowMemoryNotification() {
        if(!displayedLowMemoryNotification) {
            displayedLowMemoryNotification = true;
            SwingUtilities.invokeLater(() -> {
                view.displayLowMemoryNotification();
                resumeThreads();
            });
            pauseCurrentThreadIfNotEdt();
        }
    }

    private void pauseCurrentThreadIfNotEdt() {
        if(SwingUtilities.isEventDispatchThread()) {
            return;
        }
        try {
            wait();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
