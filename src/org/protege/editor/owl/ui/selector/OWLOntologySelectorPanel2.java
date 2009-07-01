package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.util.CheckTable;
import org.protege.editor.core.ui.util.CheckTableModel;
import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
 * Date: Sep 30, 2008<br><br>
 */
public class OWLOntologySelectorPanel2 extends JComponent {

    private CheckTable<OWLOntology> list;


    public OWLOntologySelectorPanel2(final OWLEditorKit eKit, Set<OWLOntology> onts) {
        setLayout(new BorderLayout(6, 12));

        final ArrayList<OWLOntology> ontologies = new ArrayList<OWLOntology>(onts);
        final List<URI> files = new ArrayList<URI>(ontologies.size());
        final List<Boolean> dirty = new ArrayList<Boolean>(ontologies.size());
        for (OWLOntology ont : ontologies){
            files.add(eKit.getModelManager().getOntologyPhysicalURI(ont));
            dirty.add(eKit.getModelManager().getDirtyOntologies().contains(ont));
        }
        list = new CheckTable<OWLOntology>("Ontologies");
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
        return new HashSet<OWLOntology>(list.getFilteredValues());
    }


    public void checkAll(boolean checkAll) {
        list.checkAll(checkAll);
    }
}
