package org.protege.editor.owl.ui.frame.ontology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Jan-2007<br><br>
 */
public class OWLImportsDeclarationAxiomFrameSectionRow{}
// @@TODO v3 port - import decls are no longer axioms
//extends AbstractOWLFrameSectionRow<OWLOntology, OWLAxiom, OWLImportsDeclaration> {
//
//    private MListButton fixImportsButton = new FixImportsButton(new ActionListener() {
//
//        public void actionPerformed(ActionEvent e) {
//            handleImportsFix();
//        }
//    });
//
//    private List<MListButton> additionalButtons;
//
//    public OWLImportsDeclarationAxiomFrameSectionRow(OWLEditorKit editorKit, OWLFrameSection section,
//                                                     OWLOntology ontology, OWLOntology rootObject,
//                                                     OWLImportsDeclaration axiom) {
//        super(editorKit, section, ontology, rootObject, axiom);
//        additionalButtons = new ArrayList<MListButton>();
//        additionalButtons.add(fixImportsButton);
//    }
//
//
//    protected OWLFrameSectionRowObjectEditor<OWLImportsDeclaration> getObjectEditor() {
//        return null;
//    }
//
//
//    protected OWLImportsDeclaration createAxiom(OWLImportsDeclaration editedObject) {
//        return editedObject;
//    }
//
//
//    public boolean isDeleteable() {
//        return super.isEditable();
//    }
//
//
//    public boolean isEditable() {
//        return false;
//    }
//
//
//    /**
//     * Gets a list of objects contained in this row.  These objects
//     * could be placed on the clip board during a copy operation,
//     * or navigated to etc.
//     */
//    public List<? extends OWLObject> getManipulatableObjects() {
//        return Arrays.asList(getAxiom());
//    }
//
//
//    public List<MListButton> getAdditionalButtons() {
//        OWLOntology ont = getOWLModelManager().getOWLOntologyManager().getImportedOntology(getAxiom());
//        if (ont != null && !getAxiom().getURI().equals(ont.getURI())) {
//            return additionalButtons;
//        }
//        else {
//            return super.getAdditionalButtons();
//        }
//    }
//
//    private void handleImportsFix() {
//        int ret = JOptionPane.showConfirmDialog(getOWLEditorKit().getWorkspace(),
//                                      getMismatchedImportMessage(),
//                                      "Mismatched import",
//                                      JOptionPane.YES_NO_OPTION,
//                                      JOptionPane.WARNING_MESSAGE);
//
//        if(ret == JOptionPane.YES_OPTION) {
//            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
//            OWLImportsDeclaration decl = getAxiom();
//            changes.add(new RemoveAxiom(getOntology(), decl));
//            OWLOntology impOnt = getOWLOntologyManager().getImportedOntology(decl);
//            changes.add(new AddAxiom(getOntology(), getOWLDataFactory().getOWLImportsDeclarationAxiom(getOntology(), impOnt.getURI())));
//            getOWLModelManager().applyChanges(changes);
//        }
//    }
//
//    private String getMismatchedImportMessage() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("<html><body>");
//        sb.append("The imports URI:<br>");
//        sb.append("<font color=\"blue\">");
//        sb.append(getAxiom().getURI());
//        sb.append("</font>");
//        sb.append("<br>");
//        sb.append("does not match the URI of the ontology that has been imported:<br>");
//        sb.append("<font color=\"blue\">");
//        OWLOntology ont = getOWLModelManager().getOWLOntologyManager().getImportedOntology(getAxiom());
//        sb.append(ont == null ? "(Not loaded)" : ont.getURI());
//        sb.append("</font><br><br>");
//        sb.append("Do you want to fix the mismatch by modifying the imports statement?");
//        sb.append("</body></html>");
//
//        return sb.toString();
//    }
//
//
//    private class FixImportsButton extends MListButton {
//
//
//        public FixImportsButton(ActionListener actionListener) {
//            super("Mismatched import!", Color.ORANGE, actionListener);
//        }
//
//
//        public void paintButtonContent(Graphics2D g) {
//            Rectangle bounds = getBounds();
//            g.translate(bounds.x, bounds.y - 1);
//            g.drawLine(bounds.width / 2, 4, 4, bounds.height - 4);
//            g.drawLine(bounds.width / 2, 4, bounds.width - 4, bounds.height - 4);
//            g.drawLine(4, bounds.height - 4, bounds.width - 4, bounds.height - 4);
//            g.translate(-bounds.x, -bounds.y + 1);
//        }
//
//
//        public Color getBackground() {
//            return Color.ORANGE;
//        }
//    }
//}
