package org.protege.editor.owl.ui.selector;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.util.OWLDataTypeUtils;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLOntologyManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 27-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 * Should extend AbstractSelectorPanel
 */
public class DataRangeSelectionPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(DataRangeSelectionPanel.class);


    private OWLEditorKit owlEditorKit;

    private OWLObjectList list;


    public DataRangeSelectionPanel(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        createUI();
    }


    private void createUI() {
        setLayout(new BorderLayout());

        // Add the built in datatypes
        final OWLOntologyManager mngr = owlEditorKit.getModelManager().getOWLOntologyManager();
        java.util.List<OWLDataType> datatypeList = new ArrayList<OWLDataType>(new OWLDataTypeUtils(mngr).getBuiltinDatatypes());
        Collections.sort(datatypeList, owlEditorKit.getModelManager().getOWLObjectComparator());

        list = new OWLObjectList(owlEditorKit);
        list.setListData(datatypeList.toArray());
        list.setSelectedIndex(0);

        add(ComponentFactory.createScrollPane(list));
    }


    public OWLDataType getSelectedObject(){
        return (OWLDataType)list.getSelectedValue();
    }


    public void setSelectedObject(OWLDataType dt){
        list.setSelectedValue(dt, true);
    }
    

    public static OWLDataRange showDialog(OWLEditorKit owlEditorKit) {
        DataRangeSelectionPanel panel = new DataRangeSelectionPanel(owlEditorKit);
        int ret = JOptionPaneEx.showConfirmDialog(owlEditorKit.getWorkspace(),
                                                  "Data range editor",
                                                  panel,
                                                  JOptionPane.PLAIN_MESSAGE,
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  panel.list);
        if (ret == JOptionPane.OK_OPTION) {
            return (OWLDataRange) panel.list.getSelectedValue();
        }
        else {
            return null;
        }
    }
}
