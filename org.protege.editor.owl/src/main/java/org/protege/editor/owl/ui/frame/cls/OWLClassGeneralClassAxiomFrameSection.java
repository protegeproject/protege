package org.protege.editor.owl.ui.frame.cls;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.ui.editor.OWLGeneralAxiomEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLAxiomVisitorExAdapter;

import javax.swing.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 09/06/2014
 */
public class OWLClassGeneralClassAxiomFrameSection extends AbstractOWLFrameSection<
        OWLClass,
        OWLClassAxiom,
        OWLClassAxiom> {

    public OWLClassGeneralClassAxiomFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLClass> frame) {
        super(editorKit, "General class axioms", "General class axiom", frame);
    }

    @Override
    protected OWLClassAxiom createAxiom(OWLClassAxiom object) {
        return object;
    }

    @Override
    public OWLObjectEditor<OWLClassAxiom> getObjectEditor() {
        return new OWLGeneralAxiomEditor(getOWLEditorKit());
    }

    @Override
    public void handleEditingFinished(Set<OWLClassAxiom> editedObjects) {
        checkEditedAxiom(getOWLEditorKit(), editedObjects, getRootObject());
        super.handleEditingFinished(editedObjects);
    }

    static void checkEditedAxiom(OWLEditorKit editorKit, Set<OWLClassAxiom> editedObjects, OWLClass root) {
        if(editedObjects.isEmpty()) {
            return;
        }

        OWLClassAxiom axiom = editedObjects.iterator().next();
        if(!axiom.containsEntityInSignature(root)) {
            String classesInSigRendering = "";
            for(Iterator<OWLClass> it = axiom.getClassesInSignature().iterator(); it.hasNext(); ) {
                OWLClass cls = it.next();
                classesInSigRendering += editorKit.getModelManager().getRendering(cls);
                if(it.hasNext()) {
                    classesInSigRendering += ",\n";
                }
            }

            JOptionPane.showMessageDialog(editorKit.getOWLWorkspace(),
                    "The axiom that you edited has been added to the ontology.  However, it will not be visible " +
                            "in the view below as it does not mention the selected class (" + editorKit.getOWLModelManager().getRendering(root) + ").\n" +
                            "To view the axiom, select any of the classes it mentions: \n" + classesInSigRendering);
        }
    }

    @Override
    protected void refill(OWLOntology ontology) {
        OWLWorkspace workspace = getOWLEditorKit().getOWLWorkspace();
        OWLClass cls = workspace.getOWLSelectionModel().getLastSelectedClass();
        if(cls == null) {
            return;
        }
        for(OWLClassAxiom ax : ontology.getGeneralClassAxioms()) {
            if (ax.containsEntityInSignature(cls)) {
                addRow(new OWLClassGeneralClassAxiomFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
            }
        }
    }

    @Override
    protected void clear() {

    }

    @Override
    public Comparator<OWLFrameSectionRow<OWLClass, OWLClassAxiom, OWLClassAxiom>> getRowComparator() {
        return null;
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
        if(!change.isAxiomChange()) {
            return false;
        }
        if(!change.getSignature().contains(getRootObject())) {
            return false;
        }
        OWLAxiom axiom = change.getAxiom();
        return axiom.accept(new OWLAxiomVisitorExAdapter<Boolean>() {
            @Override
            protected Boolean handleDefault(OWLAxiom axiom) {
                return false;
            }

            @Override
            public Boolean visit(OWLSubClassOfAxiom axiom) {
                return axiom.isGCI();
            }

            @Override
            public Boolean visit(OWLEquivalentClassesAxiom axiom) {
                return !axiom.contains(getRootObject());
            }

            @Override
            public Boolean visit(OWLDisjointClassesAxiom axiom) {
                return !axiom.contains(getRootObject());
            }
        });
    }
}
