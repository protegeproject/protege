package org.protege.editor.owl.ui.selector;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.semanticweb.owl.model.OWLOntology;


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
        list.setListData(owlEditorKit.getOWLModelManager().getOntologies().toArray());
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
    }


    public Set<OWLOntology> getSelectedOntologies() {
        Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
        for (Object o : list.getSelectedValues()) {
            ontologies.add((OWLOntology) o);
        }
        return ontologies;
    }


    public Dimension getPreferredSize() {
        return new Dimension(800, 400);
    }
}
