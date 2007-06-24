package org.protege.editor.core.plugin;


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
 * <p/>
 * This interface is used by the plugin utilities to filter
 * out certain types of plugin extensions.  Users of
 * <code>PluginExtensionMatcher</code> implementations should
 * define how the matcher is used.
 */
public interface PluginExtensionMatcher {

    /**
     * Determines whether the specified <code>Extension</code>
     * constitutes a "match" or not.
     * @param extension The <code>Extension</code> to test.
     * @return <code>true</code> if the <code>Extension</code> matches
     *         or <code>false</code> if the <code>Extension</code> doesn't match.
     */
    public boolean matches(Extension extension);
}
