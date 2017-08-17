package org.protege.editor.owl.ui.view.annotationproperty;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.selection.SelectionDriver;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.AbstractDeleteEntityAction;
import org.protege.editor.owl.ui.action.AbstractOWLTreeAction;
import org.protege.editor.owl.ui.renderer.*;
import org.protege.editor.owl.ui.view.AbstractOWLEntityHierarchyViewComponent;
import org.protege.editor.owl.ui.view.CreateNewChildTarget;
import org.protege.editor.owl.ui.view.CreateNewSiblingTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/
import java.util.stream.Stream;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Apr 23, 2009<br><br>
 */
public class OWLAnnotationPropertyHierarchyViewComponent extends AbstractOWLEntityHierarchyViewComponent<OWLAnnotationProperty>
        implements CreateNewChildTarget, CreateNewSiblingTarget, SelectionDriver {

    protected void performExtraInitialisation() throws Exception {
        addAction(new AbstractOWLTreeAction<OWLAnnotationProperty>("Add sub property", new AddChildIcon(new OWLAnnotationPropertyIcon()),
                getTree().getSelectionModel()) {
            public void actionPerformed(ActionEvent event) {
                createNewChild();
            }

            protected boolean canPerform(OWLAnnotationProperty prop) {
                return canCreateNewChild();
            }
        }, "A", "A");

        addAction(new AbstractOWLTreeAction<OWLAnnotationProperty>("Add sibling property", new AddSiblingIcon(new OWLAnnotationPropertyIcon()),
                getTree().getSelectionModel()) {

            public void actionPerformed(ActionEvent event) {
                createNewSibling();
            }

            protected boolean canPerform(OWLAnnotationProperty cls) {
                return canCreateNewSibling();
            }
        }, "A", "B");

        addAction(new DeleteAnnotationPropertyAction(), "B", "A");
    }


    protected OWLObjectHierarchyProvider<OWLAnnotationProperty> getHierarchyProvider() {
        return getOWLModelManager().getOWLHierarchyManager().getOWLAnnotationPropertyHierarchyProvider();
    }

    @Override
    protected Optional<OWLObjectHierarchyProvider<OWLAnnotationProperty>> getInferredHierarchyProvider() {
        return Optional.empty();
    }

    protected OWLObject updateView() {
        return updateView(getOWLWorkspace().getOWLSelectionModel().getLastSelectedAnnotationProperty());
    }


    public List<OWLAnnotationProperty> find(String match) {
        return new ArrayList<>(getOWLModelManager().getOWLEntityFinder().getMatchingOWLAnnotationProperties(match));
    }


    public boolean canCreateNewChild() {
        return true;
    }


    public void createNewChild() {
        List<OWLOntologyChange> changes = new ArrayList<>();
        OWLEntityCreationSet<OWLAnnotationProperty> set = getOWLWorkspace().createOWLAnnotationProperty();
        changes.addAll(set.getOntologyChanges());
        OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
        OWLAnnotationProperty selProp = getSelectedEntity();
        if (selProp != null) {
            OWLAxiom ax = df.getOWLSubAnnotationPropertyOfAxiom(set.getOWLEntity(), selProp);
            changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
        }
        getOWLModelManager().applyChanges(changes);
        setGlobalSelection(set.getOWLEntity());
    }


    public boolean canCreateNewSibling() {
        return getSelectedEntity() != null;
    }


    public void createNewSibling() {
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
            List<OWLOntologyChange> changes = new ArrayList<>();
            changes.addAll(creationSet.getOntologyChanges());
            OWLModelManager mngr = getOWLModelManager();
            OWLDataFactory df = mngr.getOWLDataFactory();
            for (OWLAnnotationProperty par : getHierarchyProvider().getParents(property)) {
                OWLAxiom ax = df.getOWLSubAnnotationPropertyOfAxiom(creationSet.getOWLEntity(), par);
                changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
            }
            mngr.applyChanges(changes);
            setGlobalSelection(creationSet.getOWLEntity());
        }
    }


    private class InternalOWLEntitySetProvider implements OWLEntitySetProvider<OWLAnnotationProperty> {

        public Set<OWLAnnotationProperty> getEntities() {
            return new HashSet<>(getTree().getSelectedOWLObjects());
        }

		@Override
		public Stream<OWLAnnotationProperty> entities() {
			return getEntities().stream();
		}
    }

    @Override
    public Component asComponent() {
        return this;
    }

    @Override
    public Optional<OWLObject> getSelection() {
        return Optional.ofNullable(getSelectedEntity());
    }

    public class DeleteAnnotationPropertyAction extends AbstractDeleteEntityAction<OWLAnnotationProperty> {

        /*
         * WARNING... Using an anonymous class instead of the InternalOWLEntitySetProvider class
         * below activates the java compiler bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6348760.
         * This bug has been fixed in Java 6 and in several Java 5 IDE compilers but it has not - as far
         * as I can tell - been fixed by apple's Java 5 compiler or the Sun Java 5 compilers.  At svn 
         * revision 14332, ant clean followed by ant equinox or ant install (depending on whether you are using
         * the top level build file) will result in a java.lang.AssertionError.
         * It took a fair bit of effort to track this down.  
         */
        public DeleteAnnotationPropertyAction() {
            super("Delete selected properties",
                    new DeleteEntityIcon(new OWLAnnotationPropertyIcon(OWLEntityIcon.FillType.HOLLOW)),
                    getOWLEditorKit(),
                    getHierarchyProvider(),
                    new InternalOWLEntitySetProvider());
        }


        protected String getPluralDescription() {
            return "properties";
        }
    }
}
