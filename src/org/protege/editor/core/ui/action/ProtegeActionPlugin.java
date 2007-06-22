package org.protege.editor.core.ui.action;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.ProtegePlugin;

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
 * A plugin that descirbes a <code>ProtegeAction</code>.
 * The plugin describes essential information such as the
 * name of the action, the tooltip and the icon.  The plugin
 * can also create instances of the action that it describes.
 */
public interface ProtegeActionPlugin extends ProtegePlugin<ProtegeAction> {

    /**
     * Gets the plugin id.
     */
    public String getId();


    /**
     * Gets the name of the action.  This is the name
     * that will be used for the menu item text etc.
     */
    public String getName();


    /**
     * Gets the tooltip text for the action.
     * @return A <code>String</code> representing the tooltip
     *         text, or <code>null</code> if the action created by this
     *         plugin shouldn't have any tooltip text.
     */
    public String getToolTipText();


    /**
     * Gets the icon for this action.
     * @return The <code>Icon</code> for actions created by
     *         this plugin, or <code>null</code> if the actions created
     *         by this plugin shouldn't have any icon.
     */
    public Icon getIcon();


    /**
     * Gets the <code>EditorKit</code> that instances of
     * this plugin will act on.
     */
    public EditorKit getEditorKit();
}
