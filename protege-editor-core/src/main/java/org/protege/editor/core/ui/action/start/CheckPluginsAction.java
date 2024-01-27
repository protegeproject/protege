package org.protege.editor.core.ui.action.start;

import java.awt.event.ActionEvent;
import java.io.IOException;

import org.protege.editor.core.update.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 6, 2008<br><br>
 */
public class CheckPluginsAction extends AltStartupAction {

    private static final Logger logger = LoggerFactory.getLogger(CheckPluginsAction.class);

    public void actionPerformed(ActionEvent event) {
        try {
            PluginManager.getInstance().runCheckForPlugins();
        } catch (IOException e) {
            logger.warn("A problem occurred whilst checking for plugin updates: {}", e.getMessage());
        }
    }


    public void initialise() throws Exception {
        // do nothing
    }


    public void dispose() throws Exception {
        // do nothing
    }
}
