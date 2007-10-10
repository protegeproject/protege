package org.protege.editor.owl.ui.datarange;

import java.awt.BorderLayout;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.vocab.XSDVocabulary;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 27-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
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
        list = new OWLObjectList(owlEditorKit);
        add(ComponentFactory.createScrollPane(list));
        // Add the built in datatypes
        List<OWLDataType> builtInDataTypes = new ArrayList<OWLDataType>();
        for (URI uri : XSDVocabulary.ALL_DATATYPES) {
            OWLDataFactory factory = owlEditorKit.getOWLModelManager().getOWLDataFactory();
            builtInDataTypes.add(factory.getOWLDataType(uri));
        }
        list.setListData(builtInDataTypes.toArray());
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
