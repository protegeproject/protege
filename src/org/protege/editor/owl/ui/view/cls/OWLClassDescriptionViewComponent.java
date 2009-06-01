package org.protege.editor.owl.ui.view.cls;

import org.protege.editor.owl.ui.frame.cls.OWLClassDescriptionFrame;
import org.protege.editor.owl.ui.frame.cls.OWLSubClassAxiomFrameSectionRow;
import org.protege.editor.owl.ui.framelist.CreateClosureAxiomAction;
import org.protege.editor.owl.ui.framelist.CreateNewEquivalentClassAction;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.framelist.OWLFrameListPopupMenuAction;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.CollectionFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 04-Feb-2007<br><br>
 */
public class OWLClassDescriptionViewComponent extends AbstractOWLClassViewComponent {

    private OWLFrameList<OWLClass> list;

    public void initialiseClassView() throws Exception {
        list = new OWLFrameList<OWLClass>(getOWLEditorKit(), new OWLClassDescriptionFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane(list);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(sp);
        list.addToPopupMenu(new ConvertSelectionToEquivalentClassAction());
        list.addToPopupMenu(new CreateNewEquivalentClassAction<OWLClass>());
        list.addToPopupMenu(new CreateClosureAxiomAction());
    }


    /**
     * This method is called to request that the view is updated with
     * the specified class.
     * @param selectedClass The class that the view should be updated with.  Note
     *                      that this may be <code>null</code>, which indicates that the view should
     *                      be cleared
     * @return The actual class that the view is displaying after it has been updated
     *         (may be <code>null</code>)
     */
    protected OWLClass updateView(OWLClass selectedClass) {
        list.setRootObject(selectedClass);
        return selectedClass;
    }


    public void disposeView() {
        list.dispose();
    }


    private class ConvertSelectionToEquivalentClassAction extends OWLFrameListPopupMenuAction<OWLClass> {

        protected void initialise() throws Exception {
        }


        protected void dispose() throws Exception {
        }


        protected String getName() {
            return "Convert selected rows to defined class";
        }


        protected void updateState() {
            if (list.getSelectedValue() == null) {
                setEnabled(false);
                return;
            }
            for (Object selVal : list.getSelectedValues()) {
                if (!(selVal instanceof OWLSubClassAxiomFrameSectionRow)) {
                    setEnabled(false);
                    return;
                }
            }
            setEnabled(true);
        }


        public void actionPerformed(ActionEvent e) {
            convertSelectedRowsToDefinedClass();
        }
    }


    private void convertSelectedRowsToDefinedClass() {
        final Object[] selVals = list.getSelectedValues();
        if (selVals.length > 0){
            Set<OWLClassExpression> descriptions = new HashSet<OWLClassExpression>();
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            for (Object selVal : selVals) {
                if (selVal instanceof OWLSubClassAxiomFrameSectionRow) {
                    OWLSubClassAxiomFrameSectionRow row = (OWLSubClassAxiomFrameSectionRow) selVal;
                    changes.add(new RemoveAxiom(row.getOntology(), row.getAxiom()));
                    descriptions.add(row.getAxiom().getSuperClass());
                }
            }
            OWLClassExpression equivalentClass;
            if (descriptions.size() == 1) {
                equivalentClass = descriptions.iterator().next();
            }
            else {
                equivalentClass = getOWLDataFactory().getOWLObjectIntersectionOf(descriptions);
            }
            Set<OWLClassExpression> axiomOperands = CollectionFactory.createSet(list.getRootObject(), equivalentClass);
            changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(),
                                     getOWLDataFactory().getOWLEquivalentClassesAxiom(axiomOperands)));
            getOWLModelManager().applyChanges(changes);
        }
    }
}
