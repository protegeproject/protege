package org.protege.editor.owl.ui.ontology.location;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.view.ViewBanner;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.ShowFileAction;
import org.protege.editor.owl.ui.renderer.OWLOntologyCellRenderer;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.util.Set;
import java.util.TreeSet;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 02-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PhysicalLocationPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(PhysicalLocationPanel.class);

    private OWLEditorKit owlEditorKit;

    private JComponent ontologiesPanel;


    public PhysicalLocationPanel(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        createUI();
    }


    private void createUI() {
        setLayout(new BorderLayout(3, 3));
        add(new ViewBanner("Loaded ontology sources", OWLSystemColors.getOWLOntologyColor()), BorderLayout.NORTH);
        ontologiesPanel = new Box(BoxLayout.Y_AXIS);
        ontologiesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        load();
        ontologiesPanel.setBackground(Color.WHITE);
        JPanel boxHolder = new JPanel(new BorderLayout());
        boxHolder.setOpaque(false);
        boxHolder.add(ontologiesPanel, BorderLayout.NORTH);
        add(ComponentFactory.createScrollPane(boxHolder), BorderLayout.CENTER);
    }


    private void load() {
        final OWLModelManager mngr = owlEditorKit.getModelManager();
        Set<OWLOntology> ts = new TreeSet<OWLOntology>(mngr.getOWLObjectComparator());
        ts.addAll(mngr.getOntologies());
        for (OWLOntology ont : ts) {
            OntologySourcePanel panel = new OntologySourcePanel(ont);
            panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
            ontologiesPanel.add(panel);
        }
    }


    private void reload(){
        ontologiesPanel.removeAll();
        load();
        revalidate();
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 500);
    }


    public static final void showDialog(OWLEditorKit owlEditorKit) {
        PhysicalLocationPanel panel = new PhysicalLocationPanel(owlEditorKit);
        JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.CLOSED_OPTION);
        JDialog dlg = pane.createDialog(owlEditorKit.getWorkspace(), "Ontology source locations");
        dlg.setResizable(true);
        dlg.setVisible(true);
    }


    private class OntologySourcePanel extends JPanel {

        public OntologySourcePanel(OWLOntology ont) {
            setOpaque(false);
            setLayout(new BorderLayout(3, 3));
            final OWLModelManager mngr = owlEditorKit.getModelManager();
            String label = OWLOntologyCellRenderer.getOntologyLabelText(ont, mngr);

            JLabel ontURILabel = new JLabel(label);
            ontURILabel.setIcon(OWLIcons.getIcon("ontology.png"));
            add(ontURILabel, BorderLayout.NORTH);
            final URI physicalURI = mngr.getOntologyPhysicalURI(ont);
            JLabel locURILabel = new JLabel();
            final JPopupMenu popupMenu = new JPopupMenu();
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        popupMenu.show(OntologySourcePanel.this, e.getX(), e.getY());
                    }
                }


                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        popupMenu.show(OntologySourcePanel.this, e.getX(), e.getY());
                    }
                }
            });
            if (physicalURI.getScheme().equals("file")) {
                locURILabel.setText(new File(physicalURI).toString());
                popupMenu.add(new ShowFileAction(physicalURI));
            }
            else {
                locURILabel.setText(physicalURI.toString());
            }
            popupMenu.add(new ReloadOntologyAction(ont));
            popupMenu.add(new CloseOntologyAction(ont));

            locURILabel.setFont(locURILabel.getFont().deriveFont(12.0f));
            locURILabel.setForeground(Color.DARK_GRAY);
            // Indent the physical URI slightly
            locURILabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
            add(locURILabel, BorderLayout.SOUTH);
        }
    }

    private class ReloadOntologyAction extends AbstractAction {

        private OWLOntology ont;


        public ReloadOntologyAction(OWLOntology ont) {
            super("Reload from file");
            this.ont = ont;
        }


        public void actionPerformed(ActionEvent event) {
            try {
                owlEditorKit.getModelManager().reload(ont);
            }
            catch (OWLOntologyCreationException e) {
                    JOptionPane.showMessageDialog(owlEditorKit.getWorkspace(),
                                                  "<html>Failed to reload ontology<p><p>" +
                                                  owlEditorKit.getModelManager().getRendering(ont) +
                                                  ".</html>");
            }
        }
    }

        private class CloseOntologyAction extends AbstractAction {

        private OWLOntology ont;


        public CloseOntologyAction(OWLOntology ont) {
            super("Close ontology");
            this.ont = ont;
        }


        public void actionPerformed(ActionEvent event) {
            if (owlEditorKit.getModelManager().removeOntology(ont)){
                reload();
            }
            else{
                ProtegeManager.getInstance().disposeOfEditorKit(owlEditorKit);
            }
        }
    }
}
