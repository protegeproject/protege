package org.protege.editor.core.ui.preferences;

import org.protege.editor.core.ModelManager;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.ProtegePluginInstance;

import javax.swing.*;
import java.awt.*;
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
public abstract class PreferencesPanel extends JPanel implements ProtegePluginInstance {

    private String label;

    private EditorKit editorKit;


    protected void setup(String label, EditorKit editorKit) {
        this.label = label;
        this.editorKit = editorKit;
        setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        setLayout(new PreferencesPanelLayoutManager(this));
    }


    public void add(Component comp, Object constraints) {
        super.add(comp, constraints);
        if (comp instanceof PreferencesPanel) {
            if (constraints instanceof String) {
                ((PreferencesPanel) comp).setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(
                        (String) constraints), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            }
        }
    }


    protected void addVerticalStrut(final int size) {
        add(new JPanel() {
            public Dimension getPreferredSize() {
                return new Dimension(size, size);
            }
        });
    }


    public String getLabel() {
        return label;
    }


    public EditorKit getEditorKit() {
        return editorKit;
    }


    public ModelManager getModelManager() {
        return getEditorKit().getModelManager();
    }


    public abstract void applyChanges();
}
