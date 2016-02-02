package org.protege.editor.owl.ui.util;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLClassDescriptionEditor;
import org.protege.editor.owl.ui.editor.OWLClassExpressionEditor;
import org.protege.editor.owl.ui.editor.OWLClassExpressionEditorPlugin;
import org.protege.editor.owl.ui.editor.OWLClassExpressionEditorPluginLoader;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLDataPropertySelectorPanel;
import org.protege.editor.owl.ui.selector.OWLIndividualSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.slf4j.LoggerFactory;

import java.util.*;


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
 * Date: Feb 26, 2009<br><br>
 */
public class OWLComponentFactoryImpl implements OWLComponentFactory {
    private OWLEditorKit eKit;

    private OWLClassSelectorPanel classSelectorPanel;

    private OWLObjectPropertySelectorPanel objectPropertySelectorPanel;

    private OWLDataPropertySelectorPanel dataPropertySelectorPanel;

    private OWLIndividualSelectorPanel individualSelectorPanel;

    private List<OWLClassExpressionEditorPlugin> descriptionEditorPlugins;
    
    public OWLComponentFactoryImpl(OWLEditorKit eKit) {
        this.eKit = eKit;
    }


    public OWLClassDescriptionEditor getOWLClassDescriptionEditor(OWLClassExpression expr) {
        return getOWLClassDescriptionEditor(expr, null);
    }

    @SuppressWarnings("unchecked")
    public OWLClassDescriptionEditor getOWLClassDescriptionEditor(OWLClassExpression expr, AxiomType type) {
        OWLClassDescriptionEditor editor = new OWLClassDescriptionEditor(eKit, expr);
        TreeMap<String, OWLClassExpressionEditor> editorMap = new TreeMap<String, OWLClassExpressionEditor>();
        for (OWLClassExpressionEditorPlugin plugin : getDescriptionEditorPlugins()) {
            try {
                if (type == null || plugin.isSuitableFor(type)){
                    OWLClassExpressionEditor editorPanel = plugin.newInstance();
                    if (type != null){
                        editorPanel.setAxiomType(type);
                    }
                    editorPanel.initialise();
                    editorMap.put(plugin.getIndex(), editorPanel);
                }
            }
            catch (Throwable e) {
                LoggerFactory.getLogger(OWLComponentFactoryImpl.class)
                .error("An error occurred whilst initializing a class description editor: {}", e);
            }
        }
        for(String key : editorMap.keySet()) {
            editor.addPanel(editorMap.get(key));
        }
        editor.selectPreferredEditor();
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


    private List<OWLClassExpressionEditorPlugin> getDescriptionEditorPlugins() {
        if (descriptionEditorPlugins == null){
            OWLClassExpressionEditorPluginLoader loader = new OWLClassExpressionEditorPluginLoader(eKit);
            descriptionEditorPlugins = new ArrayList<OWLClassExpressionEditorPlugin>(loader.getPlugins());
            Comparator<OWLClassExpressionEditorPlugin> clsDescrPluginComparator = (p1, p2) -> p1.getIndex().compareTo(p2.getIndex());
            Collections.sort(descriptionEditorPlugins, clsDescrPluginComparator);
        }
        return descriptionEditorPlugins;
    }
}
