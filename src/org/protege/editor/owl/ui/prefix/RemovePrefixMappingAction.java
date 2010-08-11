package org.protege.editor.owl.ui.prefix;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.owl.ui.OWLIcons;


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
	private static final long serialVersionUID = 5507854297099664212L;
	
	private PrefixMappingPanel panel;
	private PrefixMapperTable table;
	private ListSelectionListener tableSelectionListener = new ListSelectionListener() {
		
		public void valueChanged(ListSelectionEvent e) {
			update();
		}
	};


    public RemovePrefixMappingAction(PrefixMappingPanel panel) {
        super("Remove prefix", OWLIcons.getIcon("prefix.remove.png"));
        this.panel = panel;
        table = panel.getCurrentPrefixMapperTable();
        table.getSelectionModel().addListSelectionListener(tableSelectionListener);
        panel.getOntologyList().addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
                update();
                table.getSelectionModel().removeListSelectionListener(tableSelectionListener);
                table = RemovePrefixMappingAction.this.panel.getCurrentPrefixMapperTable();
                table.getSelectionModel().addListSelectionListener(tableSelectionListener);
			}
		});
        update();
    }


    private void update() {
        setEnabled(panel.getCurrentPrefixMapperTable().getSelectedRow() != -1);
    }


    public void actionPerformed(ActionEvent e) {
    	PrefixMapperTable table = panel.getCurrentPrefixMapperTable();
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
