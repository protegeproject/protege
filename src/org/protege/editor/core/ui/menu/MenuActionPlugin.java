package org.protege.editor.core.ui.menu;

import org.protege.editor.core.ui.action.ProtegeActionPlugin;

import javax.swing.*;
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
 * Date: Mar 28, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A <code>ProtegeActionPlugin</code> that is extends to
 * to represent actions that appear as menu items in the UI.  This extends the
 * <code>ProtegeActionPlugin</code> with methods to get the
 * parent menu id, the menu item group and group index.
 */
public interface MenuActionPlugin extends ProtegeActionPlugin {

    /**
     * Gets the parent menu id.  This is the id of the
     * <code>ProtegeActionPlugin</code> that represents
     * the parent menu.  If the parent id is an empty string
     * then this signifies a top level menu on the main menu
     * bar.  If the parent
     * id is contained in square brackets (e.g.  [parID])
     * then this signifies a top level menu item on some
     * popup menu.
     */
    public String getParentId();


    /**
     * Menu items can belong to groups.  Groups are usually
     * separated on the menu with menu separators (although
     * this is look and feel dependent).  Providing a group
     * name makes it possible to gain some control over where
     * the item appears on a menu.  Groups are sorted
     * alphabetically.
     * @return A <code>String</code> that represents the group
     *         the menu item belongs to.
     */
    public String getGroup();


    /**
     * Within a menu item group, items are sorted according to
     * the group index.  A group index is a string. Sets of
     * groups index strings are sorted alphabetically.
     */
    public String getGroupIndex();


    /**
     * Gets the accelerator <code>KeyStroke</code> for the
     * menu item defined by this plugin.  If the menu item
     * does not have an accelerator <code>KeyStroke</code>
     * then this method returns <code>null</code>.
     */
    public KeyStroke getAccelerator();


    /**
     * Determines if the menu is dynamically constructed
     * after the plugin has been loaded.
     */
    public boolean isDynamic();
}
