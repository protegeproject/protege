package org.protege.editor.core.ui.view;

import org.coode.mdock.ComponentPropertiesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 26-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewComponentPropertiesFactory implements ComponentPropertiesFactory {

    private final Logger logger = LoggerFactory.getLogger(ViewComponentPropertiesFactory.class);


    public Map<String, String> getProperties(JComponent component) {
        if (!(component instanceof View)) {
            logger.debug("Component is not a View - ignoring");
            return Collections.emptyMap();
        }
        View view = (View) component;
        Map<String, String> map = new HashMap<>();
        map.put("pluginId", view.getId());
        return map;
    }
}
