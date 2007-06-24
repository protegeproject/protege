package org.protege.editor.core.ui.view;


import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.PluginProperties;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.action.ToolBarActionPluginJPFImpl;

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
 */
public class ViewActionPluginJPFImpl extends ToolBarActionPluginJPFImpl implements ViewActionPlugin {

    public static final String EXTENSION_POINT_ID = "ViewAction";

    public static final String VIEW_ID_PARAM = "viewId";


    private View view;


    public ViewActionPluginJPFImpl(EditorKit editorKit, View view, Extension extension) {
        super(editorKit, extension);
        this.view = view;
    }


    public String getViewId() {
        return PluginProperties.getParameterValue(getExtension(), VIEW_ID_PARAM, null);
    }


    public ProtegeAction newInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        ExtensionInstantiator<ViewAction> instantiator = new ExtensionInstantiator<ViewAction>(getExtension());
        ViewAction viewAction = instantiator.instantiate();
        viewAction.putValue(AbstractAction.NAME, getName());
        viewAction.putValue(AbstractAction.SHORT_DESCRIPTION, getToolTipText());
        viewAction.setEditorKit(getEditorKit());
        viewAction.setView(view);
        viewAction.setEditorKit(getEditorKit());
        return viewAction;
    }


    public View getView() {
        return view;
    }
}
