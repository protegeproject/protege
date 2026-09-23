package org.protege.editor.owl.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Creates a named class that is equivalent to a supplied class expression.
 *
 * <p>The class name is retained for compatibility with plugins that call
 * {@link #showDialog(OWLClassExpression, OWLEditorKit)}.</p>
 *
 * @author Nick Drummond
 */
public final class CreateDefinedClassPanel {

    private CreateDefinedClassPanel() {
    }

    /**
     * Shows a dialog for choosing the name of a new class. The returned creation set contains
     * an {@link OWLEquivalentClassesAxiom} that defines the new class using the supplied class
     * expression.
     *
     * @param desc the class expression that defines the new class
     * @param eKit the editor kit used to create the class
     * @return the class and its ontology changes, including the defining equivalent-classes
     * axiom, or {@code null} if the dialog was cancelled
     * @deprecated Use {@link #showDialogForDefinedClass(OWLClassExpression, OWLEditorKit)}.
     */
    @Deprecated
    public static OWLEntityCreationSet<OWLClass> showDialog(OWLClassExpression desc, OWLEditorKit eKit) {
        return showDialogForDefinedClass(desc, eKit).orElse(null);
    }

    /**
     * Shows a dialog for choosing the name of a new class. The returned creation set contains
     * an {@link OWLEquivalentClassesAxiom} that defines the new class using the supplied class
     * expression.
     *
     * @param desc the class expression that defines the new class
     * @param eKit the editor kit used to create the class
     * @return the class and its ontology changes, including the defining equivalent-classes
     * axiom, or an empty optional if the dialog was cancelled
     */
    public static Optional<OWLEntityCreationSet<OWLClass>> showDialogForDefinedClass(
            OWLClassExpression desc,
            OWLEditorKit eKit) {
        OWLEntityCreationSet<OWLClass> creationSet = OWLEntityCreationPanel.showDialog(eKit, OWLClass.class);
        if (creationSet != null) {
            return Optional.of(appendDefinitionToCreationSet(creationSet, desc, eKit));
        }
        else {
            return Optional.empty();
        }
    }

    static OWLEntityCreationSet<OWLClass> appendDefinitionToCreationSet(
            OWLEntityCreationSet<OWLClass> creationSet,
            OWLClassExpression desc,
            OWLEditorKit eKit) {
        final OWLClass owlEntity = creationSet.getOWLEntity();
        final OWLAxiom ax = eKit.getOWLModelManager()
                .getOWLDataFactory()
                .getOWLEquivalentClassesAxiom(owlEntity, desc);

        final List<OWLOntologyChange> changes = new ArrayList<>(creationSet.getOntologyChanges());
        changes.add(new AddAxiom(eKit.getOWLModelManager().getActiveOntology(), ax));

        return new OWLEntityCreationSet<>(owlEntity, changes);
    }
}
