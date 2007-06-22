package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.view.View;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;

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
 * Date: 03-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A common base class for selector panels, so that they
 * all have the same preferred size etc.
 */
public abstract class AbstractSelectorPanel extends JPanel {

    private OWLEditorKit editorKit;

    private View view;


    public AbstractSelectorPanel(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        createUI();
    }


    public OWLEditorKit getOWLEditorKit() {
        return editorKit;
    }


    public Dimension getPreferredSize() {
        return new Dimension(300, 500);
    }


    public OWLModelManager getOWLModelManager() {
        return editorKit.getOWLModelManager();
    }


    private void createUI() {
        setLayout(new BorderLayout());
        ViewComponentPlugin plugin = getViewComponentPlugin();
        View v = new View(plugin, editorKit.getOWLWorkspace());
        v.setPinned(true);
        v.setSyncronizing(false);
        add(v);
        v.createUI();
        view = v;
        view.setShowViewBanner(false);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                                                     BorderFactory.createEmptyBorder(2, 2, 2, 2)));
    }


    public boolean requestFocusInWindow() {
        return view.requestFocusInWindow();
    }


    protected abstract ViewComponentPlugin getViewComponentPlugin();
}
