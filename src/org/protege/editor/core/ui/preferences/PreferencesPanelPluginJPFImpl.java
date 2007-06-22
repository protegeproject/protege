package org.protege.editor.core.ui.preferences;

import org.java.plugin.registry.Extension;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.JPFUtil;
import org.protege.editor.core.plugin.PluginProperties;
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
 * Date: 08-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PreferencesPanelPluginJPFImpl implements PreferencesPanelPlugin {

    public static final String ID = "org.protege.editor.core.preferencespanel";

    public static final String LABEL_PARAM = "label";

    private Extension extension;

    private EditorKit editorKit;


    public PreferencesPanelPluginJPFImpl(Extension extension, EditorKit editorKit) {
        this.extension = extension;
        this.editorKit = editorKit;
    }


    public String getLabel() {
        return PluginProperties.getParameterValue(extension, LABEL_PARAM, "");
    }


    public String getId() {
        return extension.getId();
    }


    public String getDocumentation() {
        return JPFUtil.getDocumentation(extension);
    }


    public PreferencesPanel newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                 InstantiationException {
        ExtensionInstantiator<PreferencesPanel> instantiator = new ExtensionInstantiator<PreferencesPanel>(extension);
        PreferencesPanel preferencesPanel = instantiator.instantiate();
        preferencesPanel.setup(getLabel(), editorKit);
        return preferencesPanel;
    }
}
