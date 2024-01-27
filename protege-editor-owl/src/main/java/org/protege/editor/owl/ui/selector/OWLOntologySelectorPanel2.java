package org.protege.editor.owl.ui.selector;

import java.awt.BorderLayout;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.protege.editor.core.ui.util.CheckTable;
import org.protege.editor.core.ui.util.CheckTableModel;
import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 30, 2008<br><br>
 */
public class OWLOntologySelectorPanel2 extends JComponent {

    private CheckTable<OWLOntology> list;


    public OWLOntologySelectorPanel2(final OWLEditorKit eKit, Set<OWLOntology> onts) {
        setLayout(new BorderLayout(6, 12));

        final ArrayList<OWLOntology> ontologies = new ArrayList<>(onts);
        final List<URI> files = new ArrayList<>(ontologies.size());
        final List<Boolean> dirty = new ArrayList<>(ontologies.size());
        for (OWLOntology ont : ontologies){
            files.add(eKit.getModelManager().getOntologyPhysicalURI(ont));
            dirty.add(eKit.getModelManager().getDirtyOntologies().contains(ont));
        }
        list = new CheckTable<>("Ontologies");
        list.getModel().setData(ontologies, true);
        list.checkAll(true);
        CheckTableModel model = list.getModel();
        model.addColumn("Location", files.toArray());
        model.addColumn("", dirty.toArray());

        list.setDefaultRenderer(new OWLCellRenderer(eKit){
            protected String getRendering(Object object) {
                    if (object instanceof Boolean){
                    if ((Boolean)object){
                        return "Local changes have been made";
                    }
                    return "";
                }
                return super.getRendering(object);
            }


            protected Icon getIcon(Object object) {
                if (object instanceof Boolean && (Boolean)object){
                    return Icons.getIcon("warning.png");
                }
                return super.getIcon(object);
            }
        });
        JScrollPane scroller = new JScrollPane(list);

        add(scroller, BorderLayout.CENTER);
    }


    public Set<OWLOntology> getSelectedOntologies() {
        return new HashSet<>(list.getFilteredValues());
    }


    public void checkAll(boolean checkAll) {
        list.checkAll(checkAll);
    }
}
