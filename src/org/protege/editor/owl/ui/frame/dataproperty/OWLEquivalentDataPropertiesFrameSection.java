package org.protege.editor.owl.ui.frame.dataproperty;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.editor.OWLDataPropertyEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.CollectionFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLEquivalentDataPropertiesFrameSection extends AbstractOWLFrameSection<OWLDataProperty, OWLEquivalentDataPropertiesAxiom, OWLDataProperty> {

    public static final String LABEL = "Equivalent properties";

    private Set<OWLEquivalentDataPropertiesAxiom> added = new HashSet<OWLEquivalentDataPropertiesAxiom>();


    protected void clear() {
    }


    public OWLEquivalentDataPropertiesFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLDataProperty> frame) {
        super(editorKit, LABEL, "Equivalent property", frame);
    }


    protected OWLEquivalentDataPropertiesAxiom createAxiom(OWLDataProperty object) {
        return getOWLDataFactory().getOWLEquivalentDataPropertiesAxiom(CollectionFactory.createSet(getRootObject(),
                                                                                                   object));
    }


    public OWLObjectEditor<OWLDataProperty> getObjectEditor() {
        return new OWLDataPropertyEditor(getOWLEditorKit());
    }


    protected void refill(OWLOntology ontology) {
        added.clear();
        for (OWLEquivalentDataPropertiesAxiom ax : ontology.getEquivalentDataPropertiesAxioms(getRootObject())) {
            addRow(new OWLEquivalentDataPropertiesFrameSectionRow(getOWLEditorKit(),
                                                                  this,
                                                                  ontology,
                                                                  getRootObject(),
                                                                  ax));
            added.add(ax);
        }
    }


    protected void refillInferred() {
        getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_EQUIVALENT_DATATYPE_PROPERTIES, new Runnable() {
                public void run() {
                    Set<OWLDataProperty> equivs = new HashSet<OWLDataProperty>(getReasoner().getEquivalentDataProperties(getRootObject()).getEntities());
                    equivs.remove(getRootObject());
                    if (!equivs.isEmpty()){
                        OWLEquivalentDataPropertiesAxiom ax = getOWLDataFactory().getOWLEquivalentDataPropertiesAxiom(equivs);
                        if (!added.contains(ax)) {
                            addRow(new OWLEquivalentDataPropertiesFrameSectionRow(getOWLEditorKit(),
                                                                                  OWLEquivalentDataPropertiesFrameSection.this,
                                                                                  null,
                                                                                  getRootObject(),
                                                                                  ax));
                        }
                    }
                }
            });
    }


    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
        if (axiom.getProperties().contains(getRootObject())) {
            reset();
        }
    }


    public Comparator<OWLFrameSectionRow<OWLDataProperty, OWLEquivalentDataPropertiesAxiom, OWLDataProperty>> getRowComparator() {
        return null;
    }
}
