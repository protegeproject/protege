package org.protege.editor.owl.ui.selector;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLOntologySelectorPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(OWLOntologySelectorPanel.class);

    private OWLEditorKit owlEditorKit;

    private OWLObjectList list;


    public OWLOntologySelectorPanel(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        list = new OWLObjectList(owlEditorKit);
        final OWLModelManager mngr = owlEditorKit.getOWLModelManager();
        final List<OWLOntology> orderedOntologies = new ArrayList<OWLOntology>(mngr.getOntologies());
        Collections.sort(orderedOntologies, mngr.getOWLObjectComparator());
        list.setListData(orderedOntologies.toArray());
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
    }


    public void setMultipleSelectionEnabled(boolean multiselect){
        list.setSelectionMode(multiselect ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);
    }


    public Set<OWLOntology> getSelectedOntologies() {
        Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
        for (Object o : list.getSelectedValues()) {
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
