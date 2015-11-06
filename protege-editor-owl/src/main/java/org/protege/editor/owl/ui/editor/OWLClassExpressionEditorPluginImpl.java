package org.protege.editor.owl.ui.editor;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractProtegePlugin;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.AxiomType;
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
public class OWLClassExpressionEditorPluginImpl extends AbstractProtegePlugin<OWLClassExpressionEditor> implements OWLClassExpressionEditorPlugin {

    private OWLEditorKit editorKit;

    public OWLClassExpressionEditorPluginImpl(OWLEditorKit editorKit, IExtension extension) {
        super(extension);
        this.editorKit = editorKit;
    }


    @SuppressWarnings("unchecked")
	public boolean isSuitableFor(AxiomType type) {
        String axiomTypes = getPluginProperty("axiomTypes");
        if (axiomTypes == null){
            return true;
        }

        if (type != null){
            for(String axiomType : axiomTypes.split(",")){
                if (type.toString().equals(axiomType.trim())){
                    return true;
                }
            }
        }
        return false;
    }


    public String getIndex() {
        String index = getPluginProperty("index");
        return index != null ? index : "ZZZ";
    }


    public OWLClassExpressionEditor newInstance() throws InstantiationException, ClassNotFoundException, IllegalAccessException {
        OWLClassExpressionEditor editor =  super.newInstance();
        editor.setup(getId(), getPluginProperty("label"), editorKit);
        return editor;
    }
}
