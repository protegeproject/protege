package org.protege.editor.core.ui.list;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 21-Sep-2008<br><br>
 */
public class RemovableObjectList<O> extends MList {

        private ListCellRenderer rendererDelegate;


    public RemovableObjectList() {
        super.setModel(new MutableObjectListModel());
        final MListCellRenderer ren = (MListCellRenderer) getCellRenderer();
        ren.setContentRenderer(new DefaultListCellRenderer() {

            /**
             * 
             */
            private static final long serialVersionUID = -4512962926323639137L;

            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                if (rendererDelegate != null) {
                    Object val = ((RemovableObjectListItem) value).getObject();
                    return rendererDelegate.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);
                }
                else {
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            }
        });
    }

    public List<O> getListItems() {
        List<O> result = new ArrayList<>();
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

/*  
 * At SVN revision: 26201, method: 
 * public void setListData(final Vector<?> listData)
 * was deleted because it was not compiling against Java 1.7.
 * This method seems to be never called (even by internal Java methods)
*/

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
        Collection<O> objects = new ArrayList<>();
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


        @SuppressWarnings("unchecked")
        public void setElementAt(Object obj, int index) {
            super.setElementAt(new RemovableObjectListItem((O)obj), index);
        }


        @SuppressWarnings("unchecked")
        public Object set(int index, Object element) {
            return super.set(index, new RemovableObjectListItem((O)element));
        }


        @SuppressWarnings("unchecked")
        public void addElement(Object obj) {
            super.addElement(new RemovableObjectListItem((O)obj));
        }


        @SuppressWarnings("unchecked")
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
            return model.remove(index) != null;
        }


        public String getTooltip() {
            return object.toString();
        }


        public O getObject() {
            return object;
        }
    }
}
