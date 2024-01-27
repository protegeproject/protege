package org.protege.editor.core.ui.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 22-Sep-2008<br><br>
 */
public class CheckList extends JComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -358732140734918547L;

    private JList list;

    private Map<Object, JCheckBox> item2CheckBoxMap;

    private List<CheckListListener> listeners;

    private ListDataListener lsnr = new ListDataListener() {

        public void intervalAdded(ListDataEvent e) {
            updateCheckBoxes();
        }


        public void intervalRemoved(ListDataEvent e) {
            updateCheckBoxes();
        }


        public void contentsChanged(ListDataEvent e) {
        }
    };


    public CheckList(JList list) {
        this.list = list;
        this.listeners = new ArrayList<>();
        item2CheckBoxMap = new IdentityHashMap<>();
        setLayout(new CheckListLayoutManager());
        list.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals("model")) {
                handleModelChange();
                ((ListModel) evt.getOldValue()).removeListDataListener(lsnr);
            }
        });
        list.getModel().addListDataListener(lsnr);
        add(list);
        updateCheckBoxes();
    }

    public Collection<Object> getCheckedItems() {
        Collection<Object> items = new ArrayList<>();
        for(Object item : item2CheckBoxMap.keySet()) {
            if(item2CheckBoxMap.get(item).isSelected()) {
                items.add(item);
            }
        }
        return items;
    }

    public void addCheckListListener(CheckListListener listener) {
        listeners.add(listener);
    }

    public void removeCheckListListener(CheckListListener listener) {
        listeners.remove(listener);
    }


    public void handleModelChange() {
        updateCheckBoxes();
        list.getModel().addListDataListener(lsnr);
    }


    private void updateCheckBoxes() {
        Map<Object, JCheckBox> remaining = new IdentityHashMap<>();
        // Add ones that are
        remaining.putAll(item2CheckBoxMap);
        ListModel model = list.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            final Object item = model.getElementAt(i);
            if (item2CheckBoxMap.get(item) == null) {
                final JCheckBox cb = new JCheckBox();
                cb.setSelected(true);
                cb.setOpaque(false);
                item2CheckBoxMap.put(item, cb);
                add(cb);
                final int index = i;
                cb.addActionListener(e -> fireCheckChanged(item, index, cb.isSelected()));
            }
            remaining.remove(item);
        }
        for (Object item : remaining.keySet()) {
            JCheckBox cb = remaining.get(item);
            remove(cb);
            item2CheckBoxMap.remove(item);
        }
        repaint();
    }

    protected void fireCheckChanged(Object item, int index, boolean b) {
        for(CheckListListener lsnr : new ArrayList<>(listeners)) {
            if(b) {
                lsnr.itemChecked(item);
            }
            else {
                lsnr.itemUnchecked(item);
            }
        }
    }


    private class CheckListLayoutManager implements LayoutManager {

        private JCheckBox cb = new JCheckBox();


        public void addLayoutComponent(String name, Component comp) {
        }


        public void removeLayoutComponent(Component comp) {
        }


        public Dimension preferredLayoutSize(Container parent) {
            Dimension listPrefSize = list.getPreferredSize();
            return new Dimension(listPrefSize.width + 20, listPrefSize.height);
        }


        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension();
        }


        public void layoutContainer(Container parent) {
            cb.setSize(cb.getPreferredSize());
            cb.setLocation(2, 2);
            parent.getInsets();
            list.setBounds(20, 0, parent.getWidth() - 20, parent.getHeight());
            ListModel listModel = list.getModel();
            for (int i = 0; i < listModel.getSize(); i++) {
                JCheckBox cb = item2CheckBoxMap.get(listModel.getElementAt(i));
                if (cb != null) {
                    Rectangle bounds = list.getCellBounds(i, i);
                    cb.setBounds(0, bounds.y, bounds.height + 20, bounds.height);
                }
            }
        }
    }

    public static interface CheckListListener {

        void itemChecked(Object item);

        void itemUnchecked(Object item);
    }
}
