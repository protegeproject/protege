package org.protege.editor.owl.ui.frame.objectproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.CollectionFactory;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLEquivalentObjectPropertiesAxiomFrameSection extends AbstractOWLFrameSection<OWLObjectProperty, OWLEquivalentObjectPropertiesAxiom, OWLObjectProperty> {

    public static final String LABEL = "Equivalent object properties";

    private Set<OWLEquivalentObjectPropertiesAxiom> added = new HashSet<OWLEquivalentObjectPropertiesAxiom>();


    public OWLEquivalentObjectPropertiesAxiomFrameSection(OWLEditorKit editorKit,
                                                          OWLFrame<? extends OWLObjectProperty> frame) {
        super(editorKit, LABEL, "Equivalent object property", frame);
    }


    protected void clear() {
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        added.clear();
        for (OWLEquivalentObjectPropertiesAxiom ax : ontology.getEquivalentObjectPropertiesAxioms(getRootObject())) {
            addRow(new OWLEquivalentObjectPropertiesAxiomFrameSectionRow(getOWLEditorKit(),
                                                                         this,
                                                                         ontology,
                                                                         getRootObject(),
                                                                         ax));
            added.add(ax);
        }
    }

    protected void refillInferred() {
        getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_EQUIVALENT_OBJECT_PROPERTIES, 
                                                                  new Runnable() {
            public void run() {
                Set<OWLObjectProperty> equivs = new HashSet<OWLObjectProperty>(getReasoner().getEquivalentObjectProperties(getRootObject()).getEntities());
                equivs.remove(getRootObject());
                if (!equivs.isEmpty()){
                    OWLEquivalentObjectPropertiesAxiom ax = getOWLDataFactory().getOWLEquivalentObjectPropertiesAxiom(equivs);
                    if (!added.contains(ax)) {
                        addRow(new OWLEquivalentObjectPropertiesAxiomFrameSectionRow(getOWLEditorKit(),
                                                                                     OWLEquivalentObjectPropertiesAxiomFrameSection.this,
                                                                                     null,
                                                                                     getRootObject(),
                                                                                     ax));
                    }
                }
            }
        });

    }


    protected OWLEquivalentObjectPropertiesAxiom createAxiom(OWLObjectProperty object) {
        return getOWLDataFactory().getOWLEquivalentObjectPropertiesAxiom(CollectionFactory.createSet(getRootObject(),
                                                                                                     object));
    }


    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        if (axiom.getProperties().contains(getRootObject())) {
            reset();
        }
    }


    public OWLObjectEditor<OWLObjectProperty> getObjectEditor() {
        return new OWLObjectPropertyEditor(getOWLEditorKit());
    }


    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
        if (axiom.getProperties().contains(getRootObject())) {
            reset();
        }
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLObjectProperty, OWLEquivalentObjectPropertiesAxiom, OWLObjectProperty>> getRowComparator() {
        return null;
    }
}
