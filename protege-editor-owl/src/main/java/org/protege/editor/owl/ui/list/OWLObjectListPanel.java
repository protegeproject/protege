package org.protege.editor.owl.ui.list;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;
import java.awt.*;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLObjectListPanel<E extends OWLObject> extends JPanel {

    private OWLObjectList list;


    public OWLObjectListPanel(Set<E> objects, OWLEditorKit owlEditorKit) {
        this(null, objects, owlEditorKit);
    }


    public OWLObjectListPanel(String message, Set<E> objects, OWLEditorKit owlEditorKit) {
        setLayout(new BorderLayout(7, 7));
        if (message != null) {
            add(new JLabel("<html><body>" + message + "</body></html>"), BorderLayout.NORTH);
        }
        list = new OWLObjectList(owlEditorKit);
        list.setListData(objects.toArray());
        add(ComponentFactory.createScrollPane(list));
    }


    public E getSelectedObject() {
        return (E) list.getSelectedValue();
    }


    public void setSelectedObject(E object) {
        list.setSelectedValue(object, true);
    }
}
