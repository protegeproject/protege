package org.protege.editor.owl.ui.framelist;

import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.CreateDefinedClassPanel;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObject;

import java.awt.event.ActionEvent;
import java.util.List;
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
 * Date: Nov 24, 2008<br><br>
 */
public class CreateNewEquivalentClassAction<C extends OWLObject> extends OWLFrameListPopupMenuAction<C> {

    protected String getName() {
        return "Create new defined class";
    }


    protected void initialise() throws Exception {
    }


    protected void dispose() throws Exception {
    }


    private OWLClassExpression getSelectedRowDescription() {
        Object selVal = getFrameList().getSelectedValue();
        if (selVal instanceof OWLFrameSectionRow) {
            List objects = ((OWLFrameSectionRow) selVal).getManipulatableObjects();
            if (objects.size() == 1){
                Object o = objects.iterator().next();
                if (o instanceof OWLClassExpression && ((OWLClassExpression)o).isAnonymous()){
                    return (OWLClassExpression)o;
                }
            }
        }
        return null;
    }


    protected void updateState() {
        setEnabled(getSelectedRowDescription() != null);
    }


    public void actionPerformed(ActionEvent e) {
        OWLClassExpression descr = getSelectedRowDescription();
        if (descr != null) {
            OWLEntityCreationSet<OWLClass> creationSet = CreateDefinedClassPanel.showDialog(descr, getOWLEditorKit());
            if (creationSet != null){
                getOWLModelManager().applyChanges(creationSet.getOntologyChanges());
                getOWLEditorKit().getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(creationSet.getOWLEntity());
            }
        }
    }
}
