package org.protege.editor.owl.ui.prefix;

import org.protege.editor.owl.ui.OWLIcons;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
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
public class RemovePrefixMappingAction extends AbstractAction {

    private PrefixMapperTable table;


    public RemovePrefixMappingAction(PrefixMapperTable table) {
        super("Remove prefix", OWLIcons.getIcon("prefix.remove.png"));
        this.table = table;
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                update();
            }
        });
        update();
    }


    private void update() {
        setEnabled(RemovePrefixMappingAction.this.table.getSelectedRow() != -1);
    }


    public void actionPerformed(ActionEvent e) {
        int [] selIndexes = table.getSelectedRows();
        List<String> prefixesToRemove = new ArrayList<String>();
        for (int i = 0; i < selIndexes.length; i++) {
            prefixesToRemove.add(table.getModel().getValueAt(selIndexes[i], 0).toString());
        }
        for (String prefix : prefixesToRemove) {
            table.getPrefixMapperTableModel().removeMapping(prefix);
        }
    }
}
