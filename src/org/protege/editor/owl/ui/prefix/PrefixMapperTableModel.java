package org.protege.editor.owl.ui.prefix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PrefixMapperTableModel extends AbstractTableModel {

    private List<String> prefixes;

    private Map<String, String> prefixValueMap;


    public PrefixMapperTableModel() {
        prefixValueMap = new HashMap<String, String>();
        prefixes = new ArrayList<String>();
        refill();
    }


    private void refill() {
        prefixes.clear();
        prefixValueMap.clear();
        PrefixMapper mapper = PrefixMapperManager.getInstance().getMapper();
        for (String prefix : mapper.getPrefixes()) {
            prefixValueMap.put(prefix, mapper.getValue(prefix));
        }
        prefixes.addAll(prefixValueMap.keySet());
        Collections.sort(prefixes);
        fireTableDataChanged();
    }


    public int addMapping(String prefix, String value) {
        prefixes.add(prefix);
        prefixValueMap.put(prefix, value);
        Collections.sort(prefixes);
        fireTableDataChanged();
        return prefixes.indexOf(prefix);
    }


    public boolean commitPrefixes() {
        // Remove any bum values.
        for (Iterator<String> it = prefixValueMap.keySet().iterator(); it.hasNext();) {
            String prefix = it.next();
            if (prefix.trim().length() == 0) {
                it.remove();
                continue;
            }
            String value = prefixValueMap.get(prefix);
            if (value.trim().length() == 0) {
                it.remove();
            }
        }
        boolean changed = false;
        PrefixMapper mapper = PrefixMapperManager.getInstance().getMapper();
        for (String prefix : mapper.getPrefixes()) {
            // If the mapping isn't in the new set, remove it from
            // the current
            if (!prefixValueMap.containsKey(prefix)) {
                mapper.removePrefixMapping(prefix);
                changed = true;
            }
        }
        for (String prefix : prefixValueMap.keySet()) {
            if (mapper.addPrefixMapping(prefix, prefixValueMap.get(prefix))) {
                changed = true;
            }
        }
        return changed;
    }


    public void removeMapping(String prefix) {
        prefixes.remove(prefix);
        prefixValueMap.remove(prefix);
        fireTableDataChanged();
    }


    public int getRowCount() {
        return prefixes.size();
    }


    public int getColumnCount() {
        return 2;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        String prefix = prefixes.get(rowIndex);
        if (columnIndex == 0) {
            return prefix;
        }
        else {
            return prefixValueMap.get(prefix);
        }
    }


    public String getColumnName(int column) {
        if (column == 0) {
            return "Prefix";
        }
        else {
            return "Value";
        }
    }


    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }


    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String currentPrefix = (String) getValueAt(rowIndex, 0);
        String currentValue = (String) getValueAt(rowIndex, 1);
        if (columnIndex == 0) {
            // Replacing prefix
            removeMapping(currentPrefix);
            addMapping(aValue.toString(), currentValue);
        }
        else {
            // Replacing value
            removeMapping(currentPrefix);
            addMapping(currentPrefix, aValue.toString());
        }
    }


    public int createNewMapping(String s) {
        for (int i = 0; ; i++) {
            String candidatePrefix = "p" + i;
            if (!prefixValueMap.keySet().contains(candidatePrefix)) {
                return addMapping(candidatePrefix, s);
            }
        }
    }
}
