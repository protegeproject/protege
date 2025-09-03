package org.protege.editor.owl.ui.ontology.location;

import org.protege.editor.core.FileUtils;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.SaveConfirmationPanel;
import org.protege.editor.owl.ui.renderer.OWLIconProvider;
import org.protege.editor.owl.ui.renderer.OWLOntologyCellRenderer;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 02-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PhysicalLocationPanel extends JPanel {

    private final Logger logger = LoggerFactory.getLogger(PhysicalLocationPanel.class);

    private static final Color ROLL_OVER_COLOR = new Color(50, 50, 255);

    private final OWLEditorKit owlEditorKit;

    private final MList ontologiesPanel;

    private final Set<OWLOntology> ontologies = new HashSet<>();

    public PhysicalLocationPanel(OWLEditorKit owlEditorKit) {
        this(owlEditorKit, owlEditorKit.getOWLModelManager().getOntologies());
    }


    public PhysicalLocationPanel(OWLEditorKit owlEditorKit, Set<OWLOntology> ontologies) {
        this.owlEditorKit = owlEditorKit;
        this.ontologies.addAll(ontologies);

        setLayout(new BorderLayout(3, 3));
        ontologiesPanel = new MList(){
            protected List<MListButton> getButtons(Object value) {
                List<MListButton> buttons  = new ArrayList<>(super.getButtons(value));
                buttons.add(new ReloadMListButton(e -> handleReload()));
                OWLOntology ont = ((OntologyListItem)value).ont;
                URI ontologyPhysicalURI = owlEditorKit.getModelManager().getOntologyPhysicalURI(ont);
                if (UIUtil.isLocalFile(ontologyPhysicalURI)){
                    buttons.add(new ShowFileMListButton(e -> handleShowFile()));
                }
                if (owlEditorKit.getModelManager().getDirtyOntologies().contains(ont)){
                    buttons.add(new SaveMListButton(e -> handleSave()));
                }
                return buttons;
            }
        };

        ontologiesPanel.setCellRenderer(new OntologyListCellRenderer());
        load();
        JPanel boxHolder = new JPanel(new BorderLayout());
        boxHolder.setOpaque(false);
        boxHolder.add(ontologiesPanel, BorderLayout.NORTH);
        add(ComponentFactory.createScrollPane(boxHolder), BorderLayout.CENTER);

    }


    public Set<OWLOntology> getSelectedOntologies(){
        Set<OWLOntology> onts = new HashSet<>();
        for (Object sel : ontologiesPanel.getSelectedValues()){
            if (sel instanceof OntologyListItem){
                onts.add(((OntologyListItem)sel).ont);
            }
        }
        return onts;
    }


    public void setOntologies(Set<OWLOntology> ontologies){
        this.ontologies.clear();
        this.ontologies.addAll(ontologies);
        reload();
    }

    private void load() {
        final OWLModelManager mngr = owlEditorKit.getModelManager();
        Set<OWLOntology> ts = new TreeSet<>(mngr.getOWLObjectComparator());
        ts.addAll(ontologies);

        java.util.List<OntologyListItem> items = new ArrayList<>();
        for (OWLOntology ont : ts){
            items.add(new OntologyListItem(ont));
        }
        OntologyListItem[] listData = items.toArray(new OntologyListItem[items.size()]);
        ontologiesPanel.setListData(listData);
    }


    private void reload(){
        load();
        revalidate();
    }


    public Dimension getPreferredSize() {
        return new Dimension(800, 500);
    }


    public static void showDialog(OWLEditorKit owlEditorKit) {
        PhysicalLocationPanel panel = new PhysicalLocationPanel(owlEditorKit);
        JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
        JDialog dlg = pane.createDialog(owlEditorKit.getWorkspace(), "Ontology source locations");
        dlg.setResizable(true);
        dlg.setVisible(true);
    }


    private void handleSave(){
        if (ontologiesPanel.getSelectedValue() instanceof OntologyListItem) {
            OntologyListItem item = (OntologyListItem) ontologiesPanel.getSelectedValue();
            OWLOntology ont = item.ont;
            try {
                owlEditorKit.getOWLModelManager().save(ont);
                owlEditorKit.addRecent(owlEditorKit.getOWLModelManager().getOntologyPhysicalURI(ont));
                SaveConfirmationPanel.showDialog(owlEditorKit, Collections.singleton(ont));
                reload();
            } catch (OWLOntologyStorageException e) {
                ErrorLogPanel.showErrorDialog(e);
            }
        }
    }


    private void handleReload(){
        try {
            if (ontologiesPanel.getSelectedValue() instanceof OntologyListItem) {
                OntologyListItem item = (OntologyListItem) ontologiesPanel.getSelectedValue();
                owlEditorKit.getModelManager().reload(item.ont);
            }
        }
        catch (OWLOntologyCreationException e) {
            JOptionPane.showMessageDialog(owlEditorKit.getWorkspace(),
                                          "<html>Failed to reload ontology<p><p>" +
                                          ".</html>");
        }
    }


    private void handleShowFile(){
        if (ontologiesPanel.getSelectedValue() instanceof OntologyListItem) {
            OntologyListItem item = (OntologyListItem) ontologiesPanel.getSelectedValue();
            OWLOntology ont = item.ont;
            URI physicalURI = owlEditorKit.getOWLModelManager().getOntologyPhysicalURI(ont);
            if (!UIUtil.isLocalFile(physicalURI)) {
                throw new IllegalArgumentException("URI must be a file URI!");
            }
            try {
                FileUtils.showFile(new File(physicalURI));
            } catch (IOException ex) {
                logger.error("An error occurred whilst attempting to show a file in the Operating System.", ex);
            }
        }
    }

    
    private void handleClose(OWLOntology ont){
        if (owlEditorKit.getModelManager().removeOntology(ont)){
            ontologies.remove(ont);
            reload();
        }
        else{
            ProtegeManager.getInstance().disposeOfEditorKit(owlEditorKit);
        }
    }

    private class OntologyListItem implements MListItem {

        private OWLOntology ont;


        private OntologyListItem(OWLOntology ont) {
            this.ont = ont;
        }


        public boolean isEditable() {
            return false;
        }


        public void handleEdit() {
            // do nothing
        }


        public boolean isDeleteable() {
            return !ont.equals(owlEditorKit.getModelManager().getActiveOntology());
        }


        public boolean handleDelete() {
            handleClose(ont);
            return true;
        }


        public String getTooltip() {
            return ont.getAxiomCount() + " axioms";
        }
    }


    private class OntologySourcePanel extends JPanel {

        private JLabel locURILabel;

        private JLabel ontURILabel;


        public OntologySourcePanel() {
            setOpaque(true);
            setLayout(new BorderLayout(3, 3));
            setBorder(new EmptyBorder(5, 5, 5, 5));

            ontURILabel = new JLabel();
            ontURILabel.setIcon(OWLIcons.getIcon("ontology.png"));
            add(ontURILabel, BorderLayout.CENTER);
            locURILabel = new JLabel();

            locURILabel.setFont(locURILabel.getFont().deriveFont(12.0f));
            locURILabel.setForeground(Color.DARK_GRAY);
            // Indent the physical URI slightly
            locURILabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
            add(locURILabel, BorderLayout.SOUTH);
        }

        public void setOntology(OWLOntology ont){
            final OWLModelManager mngr = owlEditorKit.getModelManager();
            String label = OWLOntologyCellRenderer.getOntologyLabelText(ont, mngr);

            ontURILabel.setText(label);
            
            //2012.02.01 hilpold use Owl Icon Provider
            OWLIconProvider owlICP = owlEditorKit.getWorkspace().getOWLIconProvider();
            Icon ontIcon = owlICP.getIcon(ont);
            ontURILabel.setIcon(ontIcon);

            final URI physicalURI = mngr.getOntologyPhysicalURI(ont);
            if (UIUtil.isLocalFile(physicalURI)) {
                locURILabel.setText(new File(physicalURI).toString());
            }
            else {
                locURILabel.setText(physicalURI.toString());
            }

        }
    }

    private class OntologyListCellRenderer implements ListCellRenderer {

        OntologySourcePanel panel;

        public Component getListCellRendererComponent(JList jList, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (panel == null){
                panel = new OntologySourcePanel();
            }
            panel.setOntology(((OntologyListItem)value).ont);
            if (isSelected){
                panel.setBackground(jList.getSelectionBackground());
            }
            else{
                panel.setBackground(jList.getBackground());
            }
            return panel;
        }
    }

    private static class ReloadMListButton extends MListButton {
        protected ReloadMListButton(ActionListener actionListener) {
            super("Reload", ROLL_OVER_COLOR, actionListener);
        }

        public void paintButtonContent(Graphics2D g) {
            int w = getBounds().width;
            int h = getBounds().height;
            int x = getBounds().x;
            int y = getBounds().y;
            g.drawArc(x + 4, y + 4, w - 8, h - 8, 0, -270);
            final Polygon arrowHead = new Polygon(new int[]{x+(w/2)+3, x+(w/2), x+(w/2)}, new int[]{y+4, y+2, y+6}, 3);
            g.drawPolygon(arrowHead);
        }
    }

    private static class ShowFileMListButton extends MListButton {

        protected ShowFileMListButton(ActionListener actionListener) {
            super("Show source file", ROLL_OVER_COLOR, actionListener);
        }

        public void paintButtonContent(Graphics2D g) {
            int w = getBounds().width;
            int h = getBounds().height;
            int x = getBounds().x;
            int y = getBounds().y;
            g.drawOval(x + 3, y + 3, 6, 6);
            g.drawLine(x + 8, y + 8, x + w - 5, y + h - 5);
        }
    }

    private static class SaveMListButton extends MListButton {

        protected SaveMListButton(ActionListener actionListener) {
            super("Save ontology", ROLL_OVER_COLOR, actionListener);
        }

        public void paintButtonContent(Graphics2D g) {
            int w = getBounds().width;
            int h = getBounds().height;
            int x = getBounds().x;
            int y = getBounds().y;
            g.drawRect(x + 4, y + 4, w - 8, h - 8);
            g.drawRect(x + 6, y + 4, w - 12, (h - 8)/2);
            g.drawRect(x + 8, y + ((h - 8)/2) + 1, 2, ((h - 8)/2) - 2);
        }
    }
}
