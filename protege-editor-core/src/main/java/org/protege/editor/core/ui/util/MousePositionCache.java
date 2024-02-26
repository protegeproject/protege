package org.protege.editor.core.ui.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-01-17
 */
public class MousePositionCache {

    @Nonnull
    private final Component component;

    @Nonnull
    private final Supplier<Point> mousePositionSupplier;

    private boolean stale = true;

    private Point cachedPosition = null;

    private MousePositionCache(@Nonnull Component component,
                               @Nonnull Supplier<Point> mousePositionSupplier) {
        this.component = checkNotNull(component);
        this.mousePositionSupplier = checkNotNull(mousePositionSupplier);
    }

    public static MousePositionCache createAndInstall(@Nonnull Component component,
                                                      @Nonnull Supplier<Point> mousePositionSupplier) {
        MousePositionCache cache = new MousePositionCache(component, mousePositionSupplier);
        cache.attachListeners();
        return cache;
    }

    private void attachListeners() {
        component.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                markAsStale();
            }
        });
    }

    private void markAsStale() {
        stale = true;
    }

    @Nullable
    public Point getMousePosition() {
        if(stale) {
            cachedPosition = mousePositionSupplier.get();
            stale = false;
        }
        return cachedPosition;
    }
}
