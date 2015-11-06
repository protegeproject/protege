package org.protege.editor.owl.ui.prefix;

import org.protege.editor.owl.ui.OWLIcons;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AddPrefixMappingAction extends AbstractAction {

    private PrefixMapperTables tables;


    public AddPrefixMappingAction(PrefixMapperTables tables) {
        super("Add prefix", OWLIcons.getIcon("prefix.add.png"));
        putValue(AbstractAction.SHORT_DESCRIPTION, "Add prefix mapping");
        this.tables = tables;
    }


    public void actionPerformed(ActionEvent e) {
        tables.getPrefixMapperTable().createAndEditRow();
    }
}
