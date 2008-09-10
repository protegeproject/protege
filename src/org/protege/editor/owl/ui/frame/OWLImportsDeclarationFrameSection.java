package org.protege.editor.owl.ui.frame;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLOntology;

import java.util.Comparator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Jan-2007<br><br>
 */
public class OWLImportsDeclarationFrameSection extends AbstractOWLFrameSection<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> {

    private static final Logger logger = Logger.getLogger(OWLImportsDeclarationFrameSection.class);


    public static final String LABEL = "Direct imports";


    public OWLImportsDeclarationFrameSection(OWLEditorKit editorKit, OWLFrame<OWLOntology> frame) {
        super(editorKit, LABEL, "Import", frame);
    }


    public String getLabel() {
        return LABEL;
    }


    protected void clear() {
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        if (ontology.equals(getRootObject())) {
            for (OWLImportsDeclaration decl : ontology.getImportsDeclarations()) {
                addRow(new OWLImportsDeclarationAxiomFrameSectionRow(getOWLEditorKit(),
                                                                     this,
                                                                     ontology,
                                                                     getRootObject(),
                                                                     decl));
            }
        }
    }


    protected OWLImportsDeclaration createAxiom(OWLImportsDeclaration object) {
        return object;
    }


    public OWLFrameSectionRowObjectEditor<OWLImportsDeclaration> getObjectEditor() {
        return new OWLImportsDeclarationEditor(getOWLEditorKit());
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration>> getRowComparator() {
        return new Comparator<OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration>>() {
            public int compare(OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> o1,
                               OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> o2) {
                final String uri1 = getOWLModelManager().getURIRendering(o1.getAxiom().getImportedOntologyURI());
                final String uri2 = getOWLModelManager().getURIRendering(o2.getAxiom().getImportedOntologyURI());
                return uri1.compareToIgnoreCase(uri2);
            }
        };
    }


    public void visit(OWLImportsDeclaration axiom) {
        if (axiom.getSubject().equals(getRootObject())) {
            reset();
        }
    }
}
