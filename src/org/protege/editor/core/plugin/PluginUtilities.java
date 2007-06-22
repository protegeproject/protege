package org.protege.editor.core.plugin;

import org.java.plugin.PluginManager;
import org.protege.editor.core.ProtegeApplication;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 17, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PluginUtilities {

    private static PluginUtilities instance;

    private ProtegeApplication protegeApplication;


    private PluginUtilities() {

    }


    /**
     * Gets the one and only instance of <code>PluginUtilities</code>.
     */
    public static synchronized PluginUtilities getInstance() {
        if (instance == null) {
            instance = new PluginUtilities();
        }
        return instance;
    }


    /**
     * This method is called by the system to initialise the
     * plugin utilities.  Users should <b>not</b> call this method.
     */
    public void initialise(ProtegeApplication protegeApplication) {
        this.protegeApplication = protegeApplication;
    }


    /**
     * Gets the Java Plugin Framework <code>PluginManager</code>.
     */
    public PluginManager getPluginManager() {
        return protegeApplication.getManager();
    }
}




