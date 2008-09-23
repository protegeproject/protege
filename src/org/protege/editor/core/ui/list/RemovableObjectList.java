package org.protege.editor.core.ui.list;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListDataListener;
import java.util.*;
import java.util.List;
import java.awt.*;
/*
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 21-Sep-2008<br><br>
 */
public class RemovableObjectList<O> extends MList {

    private ListCellRenderer rendererDelegate;

    public RemovableObjectList() {
        super.setModel(new MutableObjectListModel());
        final MListCellRenderer ren = (MListCellRenderer) getCellRenderer();
        ren.setContentRenderer(new ListCellRenderer() {

            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                if (rendererDelegate != null) {
                    Object val = ((RemovableObjectListItem) value).getObject();
                    return rendererDelegate.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);
                }
                else {
                    return ren.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            }
        });
        ;
    }

    public List<O> getListItems() {
        List<O> result = new ArrayList<O>();
        for(int i = 0; i < getModel().getSize(); i++) {
            result.add((O) ((RemovableObjectListItem)getModel().getElementAt(i)).getObject());
        }
        return result;

    }

    public void setCellRenderer(ListCellRenderer cellRenderer) {
        if(!(getCellRenderer() instanceof MListCellRenderer)) {
            super.setCellRenderer(cellRenderer);
        }
        else {
            rendererDelegate = cellRenderer;
        }
    }


    public void setModel(ListModel model) {
        throw new RuntimeException("Cannot change model in MutableObjectList");
    }

    public void addObject(Collection<O> objects) {
        for(Object o : objects) {
            ((MutableObjectListModel) getModel()).addElement(o);
        }
    }

    public void setListData(final Vector<?> listData) {
        MutableObjectListModel model = (MutableObjectListModel) getModel();
        model.clear();
        for (Object o : listData) {
                model.addElement(o);
            }

    }


    public void setListData(final Object[] listData) {
        MutableObjectListModel model = (MutableObjectListModel) getModel();
        model.clear();
        for (Object o : listData) {
                model.addElement(o);
            }
    }


    public void setObjects(Collection<O> objects) {
        MutableObjectListModel model = (MutableObjectListModel) getModel();
        model.clear();
        for (Object o : objects) {
                model.addElement(o);
            }
    }


    public O getSelectedValue() {
        RemovableObjectListItem item = ((RemovableObjectListItem) super.getSelectedValue());
        if(item == null) {
            return null;
        }
        return (O) item.getObject();
    }


    public O getSelectedObject() {
        return getSelectedValue();
    }

    

    public Collection<O> getSelectedObjects() {
        Collection<O> objects = new ArrayList<O>();
        for (Object o : getSelectedValues()) {
            O sel = (O) ((RemovableObjectListItem) o).getObject();
            objects.add(sel);
        }
        return objects;
    }


    protected void handleDelete() {
        MListItem item = (MListItem) super.getSelectedValue();
        item.handleDelete();
    }


    private class MutableObjectListModel extends DefaultListModel {


        public MutableObjectListModel() {
        }


        public MutableObjectListModel(Collection objects) {
            for (Object o : objects) {
                addElement(o);
            }
        }


        public MutableObjectListModel(Object[] objects) {
            for (Object o : objects) {
                addElement(o);
            }
        }


        public void setElementAt(Object obj, int index) {
            super.setElementAt(new RemovableObjectListItem((O)obj), index);
        }


        public Object set(int index, Object element) {
            return super.set(index, new RemovableObjectListItem((O)element));
        }


        public void addElement(Object obj) {
            super.addElement(new RemovableObjectListItem((O)obj));
        }


        public void add(int index, Object element) {
            super.add(index, new RemovableObjectListItem((O)element));
        }


    }


    public class RemovableObjectListItem implements MListItem {

        private O object;


        public RemovableObjectListItem(O object) {
            this.object = object;
        }


        public boolean isEditable() {
            return false;
        }


        public void handleEdit() {
        }


        public boolean isDeleteable() {
            return true;
        }


        public String toString() {
            return "LI: " + object.toString();
        }


        public boolean handleDelete() {
            MutableObjectListModel model = ((MutableObjectListModel) getModel());
            int index = model.indexOf(this);
            System.out.println("Index: " + index);
            return model.remove(index) != null;
        }


        public String getTooltip() {
            return object.toString();
        }


        public O getObject() {
            return object;
        }
    }


    public static void main(String[] args) {
        List<String> strings = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            strings.add("Item " + i);
        }
        final RemovableObjectList<String> list = new RemovableObjectList<String>();
        list.setListData(strings.toArray());

        list.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                String x1 = list.getSelectedValue();
                if (x1 != null) {
                    System.out.println(x1);
                }
            }
        });

        list.setCellRenderer(new DefaultListCellRenderer() {

            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setText("MM: " + value.toString());
                return l;
            }
        });

        JFrame f = new JFrame();
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(new JScrollPane(list));
        f.pack();
        f.setVisible(true);

    }
}
