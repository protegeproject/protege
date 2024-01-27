package org.protege.editor.owl.ui.selector;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLOntologySelectorPanel extends JPanel {

    private OWLObjectList<OWLOntology> list;

    public OWLOntologySelectorPanel(OWLEditorKit owlEditorKit) {
        this(owlEditorKit, owlEditorKit.getModelManager().getOntologies());
    }


    public OWLOntologySelectorPanel(OWLEditorKit owlEditorKit, Set<OWLOntology> ontologies) {
        list = new OWLObjectList<>(owlEditorKit);
        final OWLModelManager mngr = owlEditorKit.getModelManager();
        final List<OWLOntology> orderedOntologies = new ArrayList<>(ontologies);
        Collections.sort(orderedOntologies, mngr.getOWLObjectComparator());
        list.setListData(orderedOntologies.toArray(new OWLOntology [orderedOntologies.size()]));
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
    }

    
    public void setMultipleSelectionEnabled(boolean multiselect){
        list.setSelectionMode(multiselect ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);
    }


    public void setSelection(Set<OWLOntology> ontologies){
        list.setSelectedValues(ontologies, true);
    }


    public void setSelection(OWLOntology ontology){
        list.setSelectedValues(Collections.singleton(ontology), true);
    }


    public Set<OWLOntology> getSelectedOntologies() {
        Set<OWLOntology> ontologies = new HashSet<>();
        for (Object o : list.getSelectedValuesList()) {
            ontologies.add((OWLOntology) o);
        }
        return ontologies;
    }


    public OWLOntology getSelectedOntology() {
        final Object ont = list.getSelectedValue();
        return (ont == null) ? null : (OWLOntology) ont;
    }


    public Dimension getPreferredSize() {
        return new Dimension(800, 400);
    }
}
