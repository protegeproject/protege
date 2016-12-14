package org.protege.editor.owl.ui.ontology.imports;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

    public OntologyImportsList(OWLEditorKit editorKit, boolean read_only) {
        this.editorKit = editorKit;
        setFixedCellHeight(-1);
        setCellRenderer(new OntologyImportsItemRenderer(editorKit));

        directImportsHeader = new MListSectionHeader() {
            public String getName() {
                return "Direct Imports";
            }
            public boolean canAdd() {
            	if (read_only) {
            		return false;
            	}
                return true;
            }
        };

        indirectImportsHeader = new MListSectionHeader() {
            public String getName() {
                return "Indirect Imports";
            }
            public boolean canAdd() {
            	if (read_only) {
            		return false;
            	}
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
