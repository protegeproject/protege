package org.protege.editor.core.update;

import java.util.List;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 6, 2008<br><br>
 */
public interface PluginRegistry {

    List<PluginInfo> getAvailablePlugins();

    void reload();
}
