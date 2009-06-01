package org.protege.editor.owl.ui.ontology.imports;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportParameters;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportVerifier;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;
import org.protege.editor.owl.ui.renderer.OWLCellRendererSimple;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 28, 2009<br><br>
 */
public class OntologyImportsList extends MList {

    private OWLEditorKit eKit;

    private OWLOntology ont;

    private MListSectionHeader directImportsHeader;

    private MListSectionHeader indirectImportsHeader;

    private OntologyImportWizard wizard;

    private OWLOntologyChangeListener ontChangeListener = new OWLOntologyChangeListener(){
        public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
            handleOntologyChanges(changes);
        }
    };

    public OntologyImportsList(OWLEditorKit eKit) {
        this.eKit = eKit;

        setCellRenderer(new OWLCellRendererSimple(eKit){
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof OntologyImportItem){
                    value = ((OntologyImportItem)value).getImportDeclaration();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });


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

        eKit.getOWLModelManager().addOntologyChangeListener(ontChangeListener);
    }


    protected void handleAdd() {
        // don't need to check the section as only the direct imports can be added
        if (wizard == null){
            wizard = new OntologyImportWizard((Frame) SwingUtilities.getAncestorOfClass(Frame.class, eKit.getWorkspace()), eKit);
        }
        int ret = wizard.showModalDialog();

        if (ret == Wizard.FINISH_RETURN_CODE) {
            ImportVerifier verifier = wizard.getImportVerifier();
            ImportParameters params = verifier.checkImports();
            params.performImportSetup(eKit);

            final OWLModelManager mngr = eKit.getOWLModelManager();
            OWLOntology ont = eKit.getModelManager().getActiveOntology();

            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            for (IRI iri : params.getOntologiesToBeImported()) {
                OWLImportsDeclaration decl = mngr.getOWLDataFactory().getOWLImportsDeclaration(iri);
                changes.add(new AddImport(ont, decl));
                try {
                    OWLOntology importedOnt = mngr.getOWLOntologyManager().loadOntology(iri);
                    eKit.addRecent(mngr.getOWLOntologyManager().getPhysicalURIForOntology(importedOnt));
                    mngr.fireEvent(EventType.ONTOLOGY_LOADED);
                }
                catch (OWLException e) {
                    // do nothing - error already reported to user
                }
            }
            eKit.getModelManager().applyChanges(changes);
        }
    }


    public void setOntology(OWLOntology ont){
        this.ont = ont;

        List<Object> data = new ArrayList<Object>();

        data.add(directImportsHeader);

        // @@TODO ordering
        for (OWLImportsDeclaration decl : ont.getImportsDeclarations()){
            data.add(new OntologyImportItem(ont, decl, eKit));
        }

        data.add(indirectImportsHeader);

        // @@TODO ordering
        try {
            for (OWLOntology ontRef : eKit.getOWLModelManager().getOWLOntologyManager().getImportsClosure(ont)) {
                if (!ontRef.equals(ont)) {
                    for (OWLImportsDeclaration dec : ontRef.getImportsDeclarations()) {
                        if (!data.contains(dec)) {
                            data.add(new OntologyImportItem(ontRef, dec, eKit));
                        }
                    }
                }
            }
        }
        catch (UnknownOWLOntologyException e) {
            throw new OWLRuntimeException(e);
        }

        setListData(data.toArray());
    }


    private void handleOntologyChanges(List<? extends OWLOntologyChange> changes) {
        for (OWLOntologyChange change : changes){
            if (change instanceof AddImport ||
                change instanceof RemoveImport){
                if (change.getOntology().equals(ont)){
                    refresh();
                    return;
                }
            }
        }
    }


    private void refresh() {
        setOntology(ont);
    }


    public void dispose(){
        eKit.getOWLModelManager().removeOntologyChangeListener(ontChangeListener);
    }
}
