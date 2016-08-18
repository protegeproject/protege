package org.protege.editor.owl.ui.renderer.layout;

import org.protege.editor.core.ui.util.NativeBrowserLauncher;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public class ISBN10Link extends Link {

    private static final String URL_BASE = "http://www.isbnsearch.org/isbn/";

    private String isbn;

    public ISBN10Link(String isbn) {
        super("ISBN:" + isbn);
        this.isbn = isbn;
    }

    @Override
    public void activate(Component component, MouseEvent event) {
        NativeBrowserLauncher.openURL(URL_BASE + isbn);
    }

    @Override
    public boolean isRollOverLink() {
        return false;
    }
}
