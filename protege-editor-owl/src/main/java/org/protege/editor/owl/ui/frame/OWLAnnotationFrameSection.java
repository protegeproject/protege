package org.protege.editor.owl.ui.frame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.AnnotationPropertyComparator;
import org.protege.editor.owl.ui.editor.OWLAnnotationEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 26-Jan-2007<br><br>
 */
public class OWLAnnotationFrameSection extends AbstractOWLFrameSection<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation> {

    private static final String LABEL = "Annotations";

    private static OWLAnnotationSectionRowComparator comparator;


    public OWLAnnotationFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLAnnotationSubject> frame) {
        super(editorKit, LABEL, "Entity annotation", frame);
        comparator = new OWLAnnotationSectionRowComparator(editorKit.getModelManager());
    }


    protected void clear() {
    }


    protected void refill(OWLOntology ontology) {
        boolean hidden = false;
        final OWLAnnotationSubject annotationSubject = getRootObject();
        for (OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(annotationSubject)) {
            if (!getOWLEditorKit().getWorkspace().isHiddenAnnotationURI(ax.getAnnotation().getProperty().getIRI().toURI())) {
                addRow(new OWLAnnotationsFrameSectionRow(getOWLEditorKit(), this, ontology, annotationSubject, ax));
            } else {
                hidden = true;
            }
        }
        if (hidden) {
            setLabel(LABEL + " (some annotations are hidden)");
        } else {
            setLabel(LABEL);
        }
    }


    protected OWLAnnotationAssertionAxiom createAxiom(OWLAnnotation object) {
        return getOWLDataFactory().getOWLAnnotationAssertionAxiom(getRootObject(), object);
    }


    public OWLObjectEditor<OWLAnnotation> getObjectEditor() {
        return new OWLAnnotationEditor(getOWLEditorKit());
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     *
     * @return A comparator if to sort the rows in this section,
     * or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation>> getRowComparator() {
        return comparator;
    }


    public boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLAnnotation)) {
                return false;
            }
        }
        return true;
    }


    public boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        for (OWLObject obj : objects) {
            if (obj instanceof OWLAnnotation) {
                OWLAnnotation annot = (OWLAnnotation) obj;
                OWLAxiom ax = getOWLDataFactory().getOWLAnnotationAssertionAxiom(getRootObject(), annot);
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            } else {
                return false;
            }
        }
        getOWLModelManager().applyChanges(changes);
        return true;
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
        return change.isAxiomChange() &&
                change.getAxiom() instanceof OWLAnnotationAssertionAxiom &&
                ((OWLAnnotationAssertionAxiom) change.getAxiom()).getSubject().equals(getRootObject());
    }


    private class OWLAnnotationSectionRowComparator implements Comparator<OWLFrameSectionRow<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation>> {

        private Comparator<OWLObject> owlObjectComparator;

        private Comparator<OWLOntology> byActiveOntologyStatus;

        private Comparator<OWLOntology> byOntologyRendering;

        private Comparator<OWLOntology> comparingByOntology;

        public OWLAnnotationSectionRowComparator(OWLModelManager owlModelManager) {
            owlObjectComparator = owlModelManager.getOWLObjectComparator();
            byActiveOntologyStatus = Comparator.<OWLOntology, Boolean>comparing(o -> getOWLModelManager().getActiveOntology().equals(o)).reversed();
            byOntologyRendering = Comparator.comparing(o -> getOWLModelManager().getRendering(o));
            comparingByOntology = Comparator.nullsLast(byActiveOntologyStatus.thenComparing(byOntologyRendering));
        }

        public int compare(OWLFrameSectionRow<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation> o1,
                           OWLFrameSectionRow<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation> o2) {
            OWLAnnotation annotation1 = o1.getAxiom().getAnnotation();
            OWLAnnotation annotation2 = o2.getAxiom().getAnnotation();
            int annotationDifference = getAnnotationDifference(annotation1, annotation2);
            if(annotationDifference != 0) {
                return annotationDifference;
            }
            OWLOntology ont1 = o1.getOntology();
            OWLOntology ont2 = o2.getOntology();
            return comparingByOntology.compare(ont1, ont2);
        }

        private int getAnnotationDifference(OWLAnnotation annotation1, OWLAnnotation annotation2) {
            int diff = getAnnotationPropertyDifference(annotation1.getProperty(), annotation2.getProperty());
            if (diff == 0) {
                diff = getAnnotationLanguageDifference(annotation1.getValue(), annotation2.getValue());
            }
            if (diff == 0) {
                diff = getAnnotationValueDifference(annotation1.getValue(), annotation2.getValue());
            }
            return diff;
        }


        private int getAnnotationPropertyDifference(OWLAnnotationProperty property1, OWLAnnotationProperty property2) {
            OWLRendererPreferences preferences = OWLRendererPreferences.getInstance();
            List<IRI> annotationIRIs = preferences.getAnnotationIRIs();
            int index1 = annotationIRIs.indexOf(property1.getIRI());
            int index2 = annotationIRIs.indexOf(property2.getIRI());
            int diff = 0;
            if (index1 != -1 && index2 != -1) {
                diff = index1 - index2;
            }
            if (diff != 0) {
                return diff;
            }
            AnnotationPropertyComparator comparator = AnnotationPropertyComparator.withDefaultOrdering(owlObjectComparator);
            return comparator.compare(property1, property2);
        }

        private int getAnnotationLanguageDifference(OWLAnnotationValue value1, OWLAnnotationValue value2) {
            int diff = 0;
            if (value1 instanceof OWLLiteral && value2 instanceof OWLLiteral) {
                OWLLiteral lit1 = (OWLLiteral) value1;
                String lang1 = lit1.getLang();
                OWLLiteral lit2 = (OWLLiteral) value2;
                String lang2 = lit2.getLang();
                List<String> langs = OWLRendererPreferences.getInstance().getAnnotationLangs();
                int langIndex1 = langs.indexOf(lang1);
                int langIndex2 = langs.indexOf(lang2);
                if (langIndex1 == -1) {
                    diff = 1;
                } else if (langIndex2 == -1) {
                    diff = -1;
                } else {
                    diff = langIndex1 - langIndex2;
                }

            }
            return diff;
        }

        private int getAnnotationValueDifference(OWLAnnotationValue value1, OWLAnnotationValue value2) {
            return owlObjectComparator.compare(value1, value2);
        }
    }

}
