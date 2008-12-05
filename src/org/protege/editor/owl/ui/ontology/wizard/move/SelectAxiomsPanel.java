package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.core.ui.util.CheckTable;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLAxiom;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;/*
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
 * Date: May 30, 2008<br><br>
 */
public class SelectAxiomsPanel extends MoveAxiomsKitConfigurationPanel {

    private CheckTable<OWLAxiom> list;

    private Set<OWLAxiom> unfilteredAxioms;


    private FilteredAxiomsModel filterModel;


    private String id;


    public SelectAxiomsPanel(FilteredAxiomsModel filterModel, String id) {
        this.filterModel = filterModel;
        this.id = id;
    }


    public void initialise() {
        setLayout(new BorderLayout());

        list = new CheckTable<OWLAxiom>("Axioms");
        final OWLCellRenderer owlCellRenderer = new OWLCellRenderer(getEditorKit());
        owlCellRenderer.setHighlightKeywords(true);
        list.setDefaultRenderer(owlCellRenderer);

        final JScrollPane scroller = new JScrollPane(list);
        add(scroller);
    }


    public void dispose() {
        // do nothing
    }


    public String getID() {
        return id;
    }


    public String getTitle() {
        return "Confirm axioms to extract";
    }


    public String getInstructions() {
        return "Confirm the axioms that are to be used in the extraction.";
    }


    public void update() {
        final Set<OWLAxiom> axiomsFromKit = filterModel.getUnfilteredAxioms(getModel().getSourceOntologies());
        if (unfilteredAxioms == null || !unfilteredAxioms.equals(axiomsFromKit)){
            unfilteredAxioms = axiomsFromKit;
            list.getModel().setData(new ArrayList<OWLAxiom>(unfilteredAxioms), true);
            list.checkAll(true);
        }
    }


    public void commit() {
        filterModel.setFilteredAxioms(getFilteredAxioms());
    }


    private Set<OWLAxiom> getFilteredAxioms() {
        return new HashSet<OWLAxiom>(list.getFilteredValues());
    }
}
