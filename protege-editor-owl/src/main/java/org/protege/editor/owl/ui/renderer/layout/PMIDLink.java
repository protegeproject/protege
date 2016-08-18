package org.protege.editor.owl.ui.renderer.layout;

import org.protege.editor.core.ui.util.NativeBrowserLauncher;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public class PMIDLink extends Link {

    private static final String URL_BASE = "http://www.ncbi.nlm.nih.gov/pubmed/";

    public PMIDLink(Integer id) {
        super(id.toString());
    }

    @Override
    public void activate(Component component, MouseEvent event) {
        String id = getText();
        NativeBrowserLauncher.openURL(URL_BASE + id);
    }

    @Override
    public boolean isRollOverLink() {
        return false;
    }
}
