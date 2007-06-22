package org.protege.editor.owl.ui.prefix;

import org.protege.editor.core.PropertyUtil;
import org.protege.editor.core.ProtegeProperties;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.view.ViewBarComponent;
import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PrefixMappingPanel extends JPanel {

    private PrefixMapperTable table;

    public PrefixMappingPanel(OWLEditorKit owlEditorKit) {
        table = new PrefixMapperTable();
        ViewBarComponent vbc = new ViewBarComponent("Prefix mappings",
                PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.ONTOLOGY_COLOR_KEY), Color.GRAY),
                ComponentFactory.createScrollPane(table));
        setLayout(new BorderLayout(7, 7));
        add(vbc);
        vbc.addAction(new AddPrefixMappingAction(table));
        vbc.addAction(new GeneratePrefixFromOntologyAction(owlEditorKit, table));
        vbc.addAction(new RemovePrefixMappingAction(table));
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 500);
    }

    public static void showDialog(OWLEditorKit owlEditorKit) {
        PrefixMappingPanel panel = new PrefixMappingPanel(owlEditorKit);
        int ret = JOptionPaneEx.showConfirmDialog(owlEditorKit.getWorkspace(),
                                        "Prefix mappings",
                                        panel,
                                        JOptionPane.PLAIN_MESSAGE,
                                        JOptionPane.OK_CANCEL_OPTION,
                                        panel);
        if(ret == JOptionPane.OK_OPTION) {
           if(panel.table.getPrefixMapperTableModel().commitPrefixes()) {
               // Reset the renderer to force an update - there should
               // probably be an easier way to do this.
               owlEditorKit.getOWLModelManager().setOWLEntityRenderer(owlEditorKit.getOWLModelManager().getOWLEntityRenderer());
           }
        }
    }



}
