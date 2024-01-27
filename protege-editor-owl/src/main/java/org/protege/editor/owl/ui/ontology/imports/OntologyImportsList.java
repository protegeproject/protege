package org.protege.editor.owl.ui.ontology.imports;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 28, 2009<br><br>
 */
public class OntologyImportsList extends MList {

    private final OWLEditorKit editorKit;

    private OWLOntology ont;

    private final MListSectionHeader directImportsHeader;

    private final MListSectionHeader indirectImportsHeader;

    private OWLOntologyChangeListener ontChangeListener = this::handleOntologyChanges;

    public OntologyImportsList(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        setFixedCellHeight(-1);
        setCellRenderer(new OntologyImportsItemRenderer(editorKit));

        directImportsHeader = new MListSectionHeader() {
            public String getName() {
                return "Direct Imports";
            }
            public boolean canAdd() {
                return true;
            }
        };

        indirectImportsHeader = new MListSectionHeader() {
            public String getName() {
                return "Indirect Imports";
            }
            public boolean canAdd() {
                return false;
            }
        };

        editorKit.getOWLModelManager().addOntologyChangeListener(ontChangeListener);
    }


    protected void handleAdd() {
        // don't need to check the section as only the direct imports can be added
        OntologyImportWizard wizard = new OntologyImportWizard((Frame) SwingUtilities.getAncestorOfClass(Frame.class, editorKit.getWorkspace()), editorKit);
        int ret = wizard.showModalDialog();

        if (ret == Wizard.FINISH_RETURN_CODE) {
            AddImportsStrategy strategy = new AddImportsStrategy(editorKit, ont, wizard.getImports());
            strategy.addImports();
        }
    }

    public void setOntology(OWLOntology ont) {
        this.ont = ont;
        List<Object> data = new ArrayList<>();
        data.add(directImportsHeader);

        // @@TODO ordering
        for (OWLImportsDeclaration decl : ont.getImportsDeclarations()) {
            data.add(new OntologyImportItem(ont, decl, editorKit));
        }
        data.add(indirectImportsHeader);
        // @@TODO ordering
        try {
            for (OWLOntology ontRef : editorKit.getOWLModelManager().getOWLOntologyManager().getImportsClosure(ont)) {
                if (!ontRef.equals(ont)) {
                    for (OWLImportsDeclaration dec : ontRef.getImportsDeclarations()) {
                        if (!data.contains(dec)) {
                            data.add(new OntologyImportItem(ontRef, dec, editorKit));
                        }
                    }
                }
            }
        } catch (UnknownOWLOntologyException e) {
            throw new OWLRuntimeException(e);
        }
        setListData(data.toArray());
    }


    private void handleOntologyChanges(List<? extends OWLOntologyChange> changes) {
        for (OWLOntologyChange change : changes) {
            if (change instanceof AddImport ||
                    change instanceof RemoveImport) {
                if (change.getOntology().equals(ont)) {
                    refresh();
                    return;
                }
            }
        }
    }


    private void refresh() {
        setOntology(ont);
    }


    public void dispose() {
        editorKit.getOWLModelManager().removeOntologyChangeListener(ontChangeListener);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }
}
