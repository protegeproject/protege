package org.protege.editor.owl.ui.prefix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;


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
	private static final long serialVersionUID = -5098097390890500539L;
	
	private List<String> prefixes;

    private Map<String, String> prefixValueMap;
    
    private PrefixOWLOntologyFormat prefixManager;
    
    private boolean changed = false;


    public PrefixMapperTableModel(PrefixOWLOntologyFormat prefixManager) {
    	this.prefixManager = prefixManager;
        prefixValueMap = new HashMap<String, String>();
        prefixes = new ArrayList<String>();
        refill();
    }


    private void refill() {
    	changed = false;
        prefixes.clear();
        prefixValueMap.clear();
        for (Map.Entry<String, String> prefixName2PrefixEntry : prefixManager.getPrefixName2PrefixMap().entrySet()) {
        	String prefixName = prefixName2PrefixEntry.getKey();
        	String prefix     = prefixName2PrefixEntry.getValue();
        	// remove trailing :
        	prefixName = prefixName.substring(0, prefixName.length() - 1);
        	prefixValueMap.put(prefixName, prefix);
        }
        prefixes.addAll(prefixValueMap.keySet());
        Collections.sort(prefixes);
        fireTableDataChanged();
    }

    public int addMapping(String prefix, String value) {
    	changed = (value != null && value.length() != 0);
    	prefixes.add(0, prefix);
        prefixValueMap.put(prefix, value);
        return prefixes.indexOf(prefix);
    }


    public boolean commitPrefixes() {
    	if (changed) {
    		prefixManager.clearPrefixes();
    		for (Map.Entry<String, String> prefixName2PrefixEntry : prefixValueMap.entrySet()) {
    			String prefixName = prefixName2PrefixEntry.getKey();
    			String prefix     = prefixName2PrefixEntry.getValue();
    			if (prefix != null && prefix.length() != 0) {
    				// tailing : automatically added in here
    				prefixManager.setPrefix(prefixName, prefix);
    			}
    		}
    		changed = false;
    		return true;
    	}
    	return false;
    }


    public void removeMapping(String prefix) {
    	changed = true;
        prefixes.remove(prefix);
        prefixValueMap.remove(prefix);
        fireTableDataChanged();
    }


    public int createNewMapping(String s) {
        for (int i = 0; ; i++) {
            String candidatePrefix = "p" + i;
            if (!prefixValueMap.keySet().contains(candidatePrefix)) {
                int index = addMapping(candidatePrefix, s);
                fireTableDataChanged();
                return index;
            }
        }
    }
    
    public void sortTable() {
    	Collections.sort(prefixes);
    }

    /*
     * Table Model interfaces
     */

	@Override
	public String getColumnName(int column) {
	    if (column == 0) {
	        return "Prefix";
	    }
	    else {
	        return "Value";
	    }
	}


	public int getRowCount() {
	    return prefixes.size();
	}


	public int getColumnCount() {
	    return 2;
	}


	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
	    return !PrefixUtilities.isStandardPrefix(prefixes.get(rowIndex));
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


	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		changed = true;
	    String currentPrefix = (String) getValueAt(rowIndex, 0);
	    if (columnIndex == 0) {
	        // Replacing prefix
	    	String newPrefix = aValue.toString();
	        if (!prefixes.contains(newPrefix)){
	        	int index = prefixes.indexOf(currentPrefix);
	        	prefixes.remove(currentPrefix);
	        	prefixes.add(index, newPrefix);
	        	String prefixValue = prefixValueMap.remove(currentPrefix);
	        	prefixValueMap.put(newPrefix, prefixValue);
	        	fireTableDataChanged();
	        }
	    }
	    else {
	        // Replacing value
	        removeMapping(currentPrefix);
	        addMapping(currentPrefix, aValue.toString());
	        fireTableDataChanged();
	    }
	}
}
