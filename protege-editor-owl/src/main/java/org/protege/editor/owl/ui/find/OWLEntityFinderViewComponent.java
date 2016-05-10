package org.protege.editor.owl.ui.find;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityFinderViewComponent extends AbstractOWLViewComponent {

    private Set<OWLEntity> entities;


    public OWLEntityFinderViewComponent(Set<OWLEntity> entities) {
        this.entities = entities;
    }


    public void disposeOWLView() {
    }


    public void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        final JList list = new JList(entities.toArray());
        list.setCellRenderer(getOWLEditorKit().getWorkspace().createOWLCellRenderer());
        add(ComponentFactory.createScrollPane(list));
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                OWLEntity selEntity = (OWLEntity) list.getSelectedValue();
                getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(selEntity);
            }
        });
    }
}
