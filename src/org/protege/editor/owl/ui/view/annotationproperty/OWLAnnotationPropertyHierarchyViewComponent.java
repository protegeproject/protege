package org.protege.editor.owl.ui.view.annotationproperty;

import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.view.AbstractOWLEntityHierarchyViewComponent;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.AbstractDeleteEntityAction;
import org.protege.editor.core.ui.view.DisposableAction;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLEntitySetProvider;

import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.awt.event.ActionEvent;
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
 * Date: Apr 23, 2009<br><br>
 */
public class OWLAnnotationPropertyHierarchyViewComponent extends AbstractOWLEntityHierarchyViewComponent<OWLAnnotationProperty>{

    protected void performExtraInitialisation() throws Exception {
        addAction(new AddSubAnnotationPropertyAction(), "A", "A");
        addAction(new AddSibAnnotationPropertyAction(), "A", "B");
        addAction(new DeleteAnnotationPropertyAction(), "B", "A");
    }


    protected OWLObjectHierarchyProvider<OWLAnnotationProperty> getHierarchyProvider() {
        return getOWLModelManager().getOWLHierarchyManager().getOWLAnnotationPropertyHierarchyProvider();
    }


    protected OWLObject updateView() {
        return updateView(getOWLWorkspace().getOWLSelectionModel().getLastSelectedAnnotationProperty());
    }


    public List<OWLAnnotationProperty> find(String match) {
        return new ArrayList<OWLAnnotationProperty>(getOWLModelManager().getEntityFinder().getMatchingOWLAnnotationProperties(match));
    }

    private void createSibProperty() {
        OWLAnnotationProperty property = getTree().getSelectedOWLObject();
        if (property == null) {
            // Shouldn't really get here, because the
            // action should be disabled
            return;
        }
        // We need to apply the changes in the active ontology
        OWLEntityCreationSet<OWLAnnotationProperty> creationSet = getOWLWorkspace().createOWLAnnotationProperty();
        if (creationSet != null) {
            // Combine the changes that are required to create the OWLAnnotationProperty, with the
            // changes that are required to make it a sibling property.
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            changes.addAll(creationSet.getOntologyChanges());
            OWLModelManager mngr = getOWLModelManager();
            OWLDataFactory df = mngr.getOWLDataFactory();
            for (OWLAnnotationProperty par : getHierarchyProvider().getParents(property)) {
                OWLAxiom ax = df.getOWLSubAnnotationPropertyOfAxiom(creationSet.getOWLEntity(), par);
                changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
            }
            mngr.applyChanges(changes);
            setSelectedEntity(creationSet.getOWLEntity());
        }
    }


    private void createSubProperty() {
        OWLAnnotationProperty selProp = getSelectedEntity();
        if (selProp == null) {
            return;
        }
        OWLEntityCreationSet<OWLAnnotationProperty> set = getOWLWorkspace().createOWLAnnotationProperty();
        if (set != null) {
            java.util.List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            changes.addAll(set.getOntologyChanges());
            OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
            OWLAxiom ax = df.getOWLSubAnnotationPropertyOfAxiom(set.getOWLEntity(), selProp);
            changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            getOWLModelManager().applyChanges(changes);
            setSelectedEntity(set.getOWLEntity());
        }
    }

    private class AddSibAnnotationPropertyAction extends DisposableAction implements TreeSelectionListener {

        public AddSibAnnotationPropertyAction() {
            super("Add sibling property", OWLIcons.getIcon("property.annotation.addsib.png"));
            getTree().getSelectionModel().addTreeSelectionListener(this);
            setEnabled(getSelectedEntity() != null);
        }


        public void dispose() {
        }


        public void actionPerformed(ActionEvent e) {
            createSibProperty();
        }


        public void valueChanged(TreeSelectionEvent event) {
            setEnabled(getSelectedEntity() != null);
        }
    }


    private class AddSubAnnotationPropertyAction extends DisposableAction implements TreeSelectionListener {

        public AddSubAnnotationPropertyAction() {
            super("Add sub property", OWLIcons.getIcon("property.annotation.addsub.png"));
            getTree().getSelectionModel().addTreeSelectionListener(this);
            setEnabled(getSelectedEntity() != null);
        }


        public void dispose() {
        }


        public void actionPerformed(ActionEvent e) {
            createSubProperty();
        }


        public void valueChanged(TreeSelectionEvent event) {
            setEnabled(getSelectedEntity() != null);
        }
    }

    public class DeleteAnnotationPropertyAction extends AbstractDeleteEntityAction<OWLAnnotationProperty> {


        public DeleteAnnotationPropertyAction() {
            super("Delete selected properties",
                  OWLIcons.getIcon("property.annotation.delete.png"),
                  getOWLEditorKit(),
                  getHierarchyProvider(),
                  new OWLEntitySetProvider<OWLAnnotationProperty>() {
                      public Set<OWLAnnotationProperty> getEntities() {
                          return new HashSet<OWLAnnotationProperty>(getTree().getSelectedOWLObjects());
                      }
                  });
        }


        protected String getPluralDescription() {
            return "properties";
        }
    }
}
