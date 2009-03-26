package org.protege.editor.owl.ui.util;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLClassDescriptionEditor;
import org.protege.editor.owl.ui.editor.OWLDescriptionEditor;
import org.protege.editor.owl.ui.editor.OWLDescriptionEditorPlugin;
import org.protege.editor.owl.ui.editor.OWLDescriptionEditorPluginLoader;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLDataPropertySelectorPanel;
import org.protege.editor.owl.ui.selector.OWLIndividualSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owl.model.AxiomType;
import org.semanticweb.owl.model.OWLDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Feb 26, 2009<br><br>
 */
public class OWLComponentFactoryImpl implements OWLComponentFactory {

    private OWLEditorKit eKit;

    private OWLClassSelectorPanel classSelectorPanel;

    private OWLObjectPropertySelectorPanel objectPropertySelectorPanel;

    private OWLDataPropertySelectorPanel dataPropertySelectorPanel;

    private OWLIndividualSelectorPanel individualSelectorPanel;

    private List<OWLDescriptionEditorPlugin> descriptionEditorPlugins;


    public OWLComponentFactoryImpl(OWLEditorKit eKit) {
        this.eKit = eKit;
    }


    public OWLClassDescriptionEditor getOWLClassDescriptionEditor(OWLDescription expr) {
        return getOWLClassDescriptionEditor(expr, null);
    }


    public OWLClassDescriptionEditor getOWLClassDescriptionEditor(OWLDescription expr, AxiomType type) {
        OWLClassDescriptionEditor editor = new OWLClassDescriptionEditor(eKit, expr);
        for (OWLDescriptionEditorPlugin plugin : getDescriptionEditorPlugins()) {
            try {
                if (type == null || plugin.isSuitableFor(type)){
                    OWLDescriptionEditor editorPanel = plugin.newInstance();
                    if (type != null){
                        editorPanel.setAxiomType(type);
                    }
                    editorPanel.initialise();
                    editor.addPanel(editorPanel);
                }
            }
            catch (Exception e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }

        return editor;
    }


    public OWLClassSelectorPanel getOWLClassSelectorPanel() {
        if (classSelectorPanel == null) {
            classSelectorPanel = new OWLClassSelectorPanel(eKit);
        }
        return classSelectorPanel;
    }


    public OWLObjectPropertySelectorPanel getOWLObjectPropertySelectorPanel() {
        if (objectPropertySelectorPanel == null) {
            objectPropertySelectorPanel = new OWLObjectPropertySelectorPanel(eKit);
        }
        return objectPropertySelectorPanel;
    }


    public OWLDataPropertySelectorPanel getOWLDataPropertySelectorPanel() {
        if (dataPropertySelectorPanel == null) {
            dataPropertySelectorPanel = new OWLDataPropertySelectorPanel(eKit);
        }
        return dataPropertySelectorPanel;
    }


    public OWLIndividualSelectorPanel getOWLIndividualSelectorPanel() {
        if (individualSelectorPanel == null) {
            individualSelectorPanel = new OWLIndividualSelectorPanel(eKit);
        }
        return individualSelectorPanel;
    }


    public void dispose() {
        if (classSelectorPanel != null) {
            classSelectorPanel.dispose();
        }
        if (objectPropertySelectorPanel != null) {
            objectPropertySelectorPanel.dispose();
        }
        if (dataPropertySelectorPanel != null) {
            dataPropertySelectorPanel.dispose();
        }
        if (individualSelectorPanel != null) {
            individualSelectorPanel.dispose();
        }
    }


    private List<OWLDescriptionEditorPlugin> getDescriptionEditorPlugins() {
        if (descriptionEditorPlugins == null){
            OWLDescriptionEditorPluginLoader loader = new OWLDescriptionEditorPluginLoader(eKit);
            descriptionEditorPlugins = new ArrayList<OWLDescriptionEditorPlugin>(loader.getPlugins());
            Comparator<OWLDescriptionEditorPlugin> clsDescrPluginComparator = new Comparator<OWLDescriptionEditorPlugin>(){
                public int compare(OWLDescriptionEditorPlugin p1, OWLDescriptionEditorPlugin p2) {
                    return p1.getIndex().compareTo(p2.getIndex());
                }
            };
            Collections.sort(descriptionEditorPlugins, clsDescrPluginComparator);
        }
        return descriptionEditorPlugins;
    }
}
