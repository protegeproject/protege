package org.protege.editor.core.ui.view;

import org.protege.editor.core.plugin.ProtegePlugin;
import org.protege.editor.core.ui.workspace.Workspace;

import javax.annotation.Nonnull;
import java.awt.*;
import java.net.URI;
import java.util.Optional;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 18, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface ViewComponentPlugin extends ProtegePlugin<ViewComponent> {

    public static final String VISIBLE = "visible";

    public static final String HIDDEN = "hidden";


    String getLabel();


    Color getBackgroundColor();


    boolean isUserCreatable();


    Workspace getWorkspace();


    Set<String> getCategorisations();
    
    boolean isEager();


    Set<String> getNavigates();

    /**
     * Gets the Help Link for this view component.  The help link should point to a web page that describes the
     * functionality of, and how to use, the view component.
     * @return An optional help link.
     */
    @Nonnull
    default Optional<URI> getHelpLink() {
        return Optional.empty();
    }

}
