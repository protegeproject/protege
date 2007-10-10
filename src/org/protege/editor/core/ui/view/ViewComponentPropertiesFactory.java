package org.protege.editor.core.ui.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.coode.mdock.ComponentPropertiesFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 26-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewComponentPropertiesFactory implements ComponentPropertiesFactory {

    private static final Logger logger = Logger.getLogger(ViewComponentPropertiesFactory.class);


    public Map<String, String> getProperties(JComponent component) {
        if (component instanceof View == false) {
            logger.debug("Component is not a View - ignoring");
            return Collections.EMPTY_MAP;
        }
        View view = (View) component;
        Map<String, String> map = new HashMap<String, String>();
        map.put("pluginId", view.getId());
        return map;
    }
}
