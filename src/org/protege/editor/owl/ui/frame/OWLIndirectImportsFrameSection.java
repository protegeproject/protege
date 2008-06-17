package org.protege.editor.owl.ui.frame;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLRuntimeException;
import org.semanticweb.owl.model.UnknownOWLOntologyException;
import org.semanticweb.owl.util.SimpleURIShortFormProvider;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLIndirectImportsFrameSection extends AbstractOWLFrameSection<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> {

    private static final Logger logger = Logger.getLogger(OWLIndirectImportsFrameSection.class);


    public static final String LABEL = "Indirect imports";

    private Set<OWLImportsDeclaration> added = new HashSet<OWLImportsDeclaration>();


    public OWLIndirectImportsFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLOntology> frame) {
        super(editorKit, LABEL, frame);
    }


    protected OWLImportsDeclaration createAxiom(OWLImportsDeclaration object) {
        return object;
    }


    public OWLFrameSectionRowObjectEditor<OWLImportsDeclaration> getObjectEditor() {
        return null;
    }


    protected void clear() {
        added.clear();
    }


    public boolean canAdd() {
        return false;
    }


    protected void refill(OWLOntology ontology) {
        try {
            for (OWLOntology ont : getOWLModelManager().getOWLOntologyManager().getImportsClosure(ontology)) {
                if (!ont.equals(ontology)) {
                    for (OWLImportsDeclaration dec : ont.getImportsDeclarations()) {
                        if (!added.contains(dec)) {
                            addRow(new OWLImportsDeclarationAxiomFrameSectionRow(getOWLEditorKit(),
                                                                                 this,
                                                                                 ont,
                                                                                 ontology,
                                                                                 dec));
                            added.add(dec);
                        }
                    }
                }
            }
        }
        catch (UnknownOWLOntologyException e) {
            // Programming error - convert to runtime exception
            throw new OWLRuntimeException(e);
        }
    }


    public Comparator<OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration>> getRowComparator() {
        return new Comparator<OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration>>() {

            SimpleURIShortFormProvider sfp = new SimpleURIShortFormProvider();

            public int compare(OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> o1,
                               OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> o2) {
                final String uri1 = sfp.getShortForm(o1.getAxiom().getImportedOntologyURI());
                final String uri2 = sfp.getShortForm(o2.getAxiom().getImportedOntologyURI());
                return uri1.compareToIgnoreCase(uri2);
            }
        };
    }


    public void visit(OWLImportsDeclaration axiom) {
        reset();
    }
}
