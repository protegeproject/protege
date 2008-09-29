package org.protege.editor.owl.ui.ontology;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.util.CheckTable;
import org.protege.editor.core.ui.util.CheckTableModel;
import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.io.OntologySourcesListener;
import org.protege.editor.owl.model.io.OntologySourcesManager;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.*;
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
 * Date: Sep 24, 2008<br><br>
 */
public class OntologySourcesChangedHandlerUI implements OntologySourcesListener {

    private OWLEditorKit eKit;

    private static final String TITLE = "Ontology sources changed";

    private boolean handlingChange = false;

    private CheckTable<OWLOntology> list;


    public OntologySourcesChangedHandlerUI(final OWLWorkspace workspace) {
        this.eKit = workspace.getOWLEditorKit();

        getSourcesManager().addListener(this);

        // when the workspace is first shown
        workspace.addAncestorListener(new AncestorListener(){
            public void ancestorAdded(AncestorEvent event) {
                // add a listener for when the window gets activated
                ProtegeManager.getInstance().getFrame(workspace).addWindowListener(new WindowAdapter() {
                    public void windowActivated(WindowEvent event) {
                        handleWindowActivated();
                    }
                });
                workspace.removeAncestorListener(this);
            }


            public void ancestorRemoved(AncestorEvent event) {
                // do nothing
            }


            public void ancestorMoved(AncestorEvent event) {
                // do nothing
            }
        });
    }


    public void ontologySourcesChanged(OntologySourcesChangeEvent event) {
        handlingChange = true;
        Set<OWLOntology> onts = event.getOntologies();
        if (onts.size() == 1){
            OWLOntology ont = onts.iterator().next();
            StringBuilder message = new StringBuilder("<html>An ontology has changed outside of Protege.");
            message.append("<p><p><code>").append(eKit.getModelManager().getRendering(ont)).append("</code>");
            message.append("<p><p>Would you like to reload?");
            if (eKit.getModelManager().getDirtyOntologies().contains(ont)){
                message.append("<p><p><b>Warning: this ontology has been edited so you will lose local changes</b>");
            }
            message.append("</html>");
            if (JOptionPane.showConfirmDialog(eKit.getWorkspace(),
                                              message.toString(),
                                              TITLE,
                                              JOptionPane.YES_NO_OPTION,
                                              JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION){
                try {
                    eKit.getModelManager().reload(ont);
                }
                catch (OWLOntologyCreationException e) {
                    handleFailedToReload(ont);
                }
            }
            else{
                getSourcesManager().ignoreUpdates(onts);
            }
        }
        else{
            JComponent ontologiesPanel = createOntologiesPanel(onts);

            final Set<OWLOntology> ignoreOnts = new HashSet<OWLOntology>(onts);

            if (JOptionPaneEx.showConfirmDialog(eKit.getWorkspace(),
                                                TITLE,
                                                ontologiesPanel,
                                                JOptionPane.WARNING_MESSAGE,
                                                JOptionPane.YES_NO_OPTION,
                                                null) == JOptionPane.OK_OPTION){

                final List<OWLOntology> reloadOnts = getFilteredValues();
                ignoreOnts.removeAll(reloadOnts);
                for (OWLOntology ont : reloadOnts){
                    try {
                        eKit.getModelManager().reload(ont);
                    }
                    catch (OWLOntologyCreationException e) {
                        handleFailedToReload(ont);
                    }
                }
            }

            if (!ignoreOnts.isEmpty()){
                getSourcesManager().ignoreUpdates(ignoreOnts);
            }
        }
        handlingChange = false;
    }


    private List<OWLOntology> getFilteredValues() {
        return list.getFilteredValues();
    }


    private JComponent createOntologiesPanel(Set<OWLOntology> onts) {
        final ArrayList<OWLOntology> ontologies = new ArrayList<OWLOntology>(onts);
        final List<URI> files = new ArrayList<URI>(ontologies.size());
        final List<Boolean> dirty = new ArrayList<Boolean>(ontologies.size());
        for (OWLOntology ont : ontologies){
            files.add(eKit.getModelManager().getOntologyPhysicalURI(ont));
            dirty.add(eKit.getModelManager().getDirtyOntologies().contains(ont));
        }
        list = new CheckTable<OWLOntology>("Changed ontologies");
        list.setData(ontologies);
        list.checkAll(true);
        CheckTableModel model = list.getModel();
        model.addColumn("File changed", files.toArray());
        model.addColumn("", dirty.toArray());

        list.setDefaultRenderer(new OWLCellRenderer(eKit){
            protected String getRendering(Object object) {
                if (object instanceof OWLOntology){
                    OWLOntology ont = (OWLOntology) object;
                    return eKit.getModelManager().getURIRendering(ont.getURI());
                }
                else if (object instanceof Boolean){
                    if ((Boolean)object){
                        return "This will overwrite local changes";
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
        JComponent ontologiesPanel = new JPanel(new BorderLayout(6, 12));
        JScrollPane scroller = new JScrollPane(list);
        scroller.setPreferredSize(new Dimension(600, 200));

        ontologiesPanel.add(new JLabel("<html>The following ontologies have changed outside of Protege.<p><p>Would you like to reload?</html>"), BorderLayout.NORTH);
        ontologiesPanel.add(scroller, BorderLayout.CENTER);
        return ontologiesPanel;
    }



    private void handleFailedToReload(OWLOntology ont) {
        JOptionPane.showMessageDialog(eKit.getWorkspace(),
                                      "<html>Failed to reload ontology<p><p>" +
                                      eKit.getModelManager().getRendering(ont) +
                                      ".<p><p>Ignoring update.</html>");
        getSourcesManager().ignoreUpdates(Collections.singleton(ont));
    }


    protected void handleWindowActivated() {
        if (!handlingChange){ // don't bother when we are already performing a check
            getSourcesManager().checkSources();
        }
    }


    private OntologySourcesManager getSourcesManager() {
        return eKit.getModelManager().get(OntologySourcesManager.ID);
    }
}
