package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.core.ui.util.CheckTable;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 
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

        list = new CheckTable<>("Axioms");
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
            list.getModel().setData(new ArrayList<>(unfilteredAxioms), true);
            list.checkAll(true);
        }
    }


    public void commit() {
        filterModel.setFilteredAxioms(getFilteredAxioms());
    }


    private Set<OWLAxiom> getFilteredAxioms() {
        return new HashSet<>(list.getFilteredValues());
    }
}
