package org.protege.editor.owl.ui.frame.ontology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLIndirectImportsFrameSection {}
// @@TODO v3 port - import decls are no longer axioms
//extends AbstractOWLFrameSection<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> {
//
//    private static final Logger logger = Logger.getLogger(OWLIndirectImportsFrameSection.class);
//
//
//    public static final String LABEL = "Indirect imports";
//
//    private Set<OWLImportsDeclaration> added = new HashSet<OWLImportsDeclaration>();
//
//
//    public OWLIndirectImportsFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLOntology> frame) {
//        super(editorKit, LABEL, "Indirect import", frame);
//    }
//
//
//    protected OWLImportsDeclaration createAxiom(OWLImportsDeclaration object) {
//        return object;
//    }
//
//
//    public OWLFrameSectionRowObjectEditor<OWLImportsDeclaration> getObjectEditor() {
//        return null;
//    }
//
//
//    protected void clear() {
//        added.clear();
//    }
//
//
//    public boolean canAdd() {
//        return false;
//    }
//
//
//    protected void refill(OWLOntology ontology) {
//        try {
//            for (OWLOntology ont : getOWLModelManager().getOWLOntologyManager().getImportsClosure(ontology)) {
//                if (!ont.equals(ontology)) {
//                    for (OWLImportsDeclaration dec : ont.getImportsDeclarations()) {
//                        if (!added.contains(dec)) {
//                            addRow(new OWLImportsDeclarationAxiomFrameSectionRow(getOWLEditorKit(),
//                                                                                 this,
//                                                                                 ont,
//                                                                                 ontology,
//                                                                                 dec));
//                            added.add(dec);
//                        }
//                    }
//                }
//            }
//        }
//        catch (UnknownOWLOntologyException e) {
//            // Programming error - convert to runtime exception
//            throw new OWLRuntimeException(e);
//        }
//    }
//
//
//    public Comparator<OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration>> getRowComparator() {
//        return new Comparator<OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration>>() {
//            public int compare(OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> o1,
//                               OWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> o2) {
//                final String uri1 = getOWLModelManager().getURIRendering(o1.getAxiom().getURI());
//                final String uri2 = getOWLModelManager().getURIRendering(o2.getAxiom().getURI());
//                return uri1.compareToIgnoreCase(uri2);
//            }
//        };
//    }
//
//
//    public void visit(OWLImportsDeclaration axiom) {
//        reset();
//    }
//}
