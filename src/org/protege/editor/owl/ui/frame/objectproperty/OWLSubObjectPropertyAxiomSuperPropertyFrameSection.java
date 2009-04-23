package org.protege.editor.owl.ui.frame.objectproperty;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.protege.editor.owl.ui.frame.editor.OWLObjectPropertyEditor;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.inference.UnsupportedReasonerOperationException;
import org.semanticweb.owl.model.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLSubObjectPropertyAxiomSuperPropertyFrameSection extends AbstractOWLFrameSection<OWLObjectProperty, OWLSubObjectPropertyOfAxiom, OWLObjectProperty> {
    private static Logger log = Logger.getLogger(OWLSubObjectPropertyAxiomSuperPropertyFrameSection.class);

    public static final String LABEL = "Super properties";

    Set<OWLObjectPropertyExpression> added = new HashSet<OWLObjectPropertyExpression>();


    public OWLSubObjectPropertyAxiomSuperPropertyFrameSection(OWLEditorKit editorKit,
                                                              OWLFrame<? extends OWLObjectProperty> frame) {
        super(editorKit, LABEL, "Super property", frame);
    }


    protected void clear() {
        added.clear();
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {

        for (OWLSubObjectPropertyOfAxiom ax : ontology.getObjectSubPropertyAxiomsForSubProperty(getRootObject())) {
            addRow(new OWLSubObjectPropertyAxiomSuperPropertyFrameSectionRow(getOWLEditorKit(),
                                                                             this,
                                                                             ontology,
                                                                             getRootObject(),
                                                                             ax));
            added.add(ax.getSuperProperty());
        }
    }


    protected void refillInferred() {
        try {
            for (OWLObjectPropertyExpression infSup : OWLReasonerAdapter.flattenSetOfSets(getOWLModelManager().getReasoner().getSuperProperties(
                    getRootObject()))) {
                if (!added.contains(infSup)) {
                    addRow(new OWLSubObjectPropertyAxiomSuperPropertyFrameSectionRow(getOWLEditorKit(),
                                                                                     this,
                                                                                     null,
                                                                                     getRootObject(),
                                                                                     getOWLDataFactory().getOWLSubObjectPropertyOfAxiom(
                                                                                             getRootObject(),
                                                                                             infSup)));
                }
            }
        }
        catch (UnsupportedReasonerOperationException e) {
            log.error(e.getMessage());
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    protected OWLSubObjectPropertyOfAxiom createAxiom(OWLObjectProperty object) {
        return getOWLDataFactory().getOWLSubObjectPropertyOfAxiom(getRootObject(), object);
    }


    public OWLFrameSectionRowObjectEditor<OWLObjectProperty> getObjectEditor() {
        return new OWLObjectPropertyEditor(getOWLEditorKit());
    }


    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        if (axiom.getSubProperty().equals(getRootObject())) {
            reset();
        }
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLObjectProperty, OWLSubObjectPropertyOfAxiom, OWLObjectProperty>> getRowComparator() {
        return null;
    }
}
