package org.protege.editor.owl.ui.prefix;

import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.AbstractTableModel;
import java.util.*;


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
	
	private final Logger logger = LoggerFactory.getLogger(PrefixMapperTableModel.class);
	
	public enum Column {
		PREFIX_NAME, PREFIX
	}
	
	
	private List<String> prefixes;

    private Map<String, String> prefixValueMap;
    
    private PrefixDocumentFormat prefixManager;
    
    private boolean changed = false;


    public PrefixMapperTableModel(PrefixDocumentFormat prefixManager) {
    	this.prefixManager = prefixManager;
        prefixValueMap = new HashMap<>();
        prefixes = new ArrayList<>();
        refill();
    }

    public void refill() {
    	logger.debug("Clearing changed flag because of a refill operation");
    	changed = false;
    	// arguably here we should only delete the prefixes that don't have an empty value
    	// it is a little weird when they disappear because they were not committed to the PrefixOWLOntologyFormat
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
    
    public int getIndexOfPrefix(String prefix) {
    	return prefixes.indexOf(prefix);
    }

    public int addMapping(String prefix, String value) {
    	changed = changed || (value != null && value.length() != 0);
    	if (logger.isDebugEnabled()) {
    		logger.debug("adding mapping " + prefix + " -> " + value + " changed = " + changed);
    	}
    	prefixes.add(prefix);
    	Collections.sort(prefixes);
        prefixValueMap.put(prefix, value);
	    fireTableDataChanged();
        return prefixes.indexOf(prefix);
    }


    public void removeMapping(String prefix) {
    	prefixes.remove(prefix);
	    String prefixValue = prefixValueMap.remove(prefix);
	    changed = changed || (prefixValue != null & prefixValue.length() != 0);
    	if (logger.isDebugEnabled()) {
    		logger.debug("removing mapping " + prefix + " -> " + prefixValue + " changed = " + changed);
    	}
	    fireTableDataChanged();
	}

	public boolean commitPrefixes() {
    	if (changed) {
    		if (logger.isDebugEnabled()) {
    			logger.debug("committing prefix changes and clearing changed flag");
    		}
    		prefixManager.setPrefixManager(new DefaultPrefixManager());
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


    public void sortTable() {
    	Collections.sort(prefixes);
    }

    /*
     * Table Model interfaces
     */

	@Override
	public String getColumnName(int column) {
		switch (Column.values()[column]) {
		case PREFIX_NAME:
			return "Prefix";
		case PREFIX:
			return "Value";
		default:
			throw new UnsupportedOperationException("Programmer error: missed a case");
		}
	}


	public int getRowCount() {
	    return prefixes.size();
	}


	public int getColumnCount() {
	    return Column.values().length;
	}


	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
	    return !PrefixUtilities.isStandardPrefix(prefixes.get(rowIndex));
	}


	public Object getValueAt(int rowIndex, int columnIndex) {
	    String prefix = prefixes.get(rowIndex);
		switch (Column.values()[columnIndex]) {
		case PREFIX_NAME:
			return prefix;
		case PREFIX:
			return prefixValueMap.get(prefix);
		default:
			throw new UnsupportedOperationException("Programmer error: missed a case");
		}
	}


	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	    String currentPrefixName = (String) getValueAt(rowIndex, Column.PREFIX_NAME.ordinal());
		switch (Column.values()[columnIndex]) {
		case PREFIX_NAME:
	        // Replacing prefix name 
	    	String newPrefix = aValue.toString();
	        if (!prefixes.contains(newPrefix)){
	        	prefixes.remove(currentPrefixName);
	        	prefixes.add(newPrefix);
	        	Collections.sort(prefixes);
	        	String prefixValue = prefixValueMap.remove(currentPrefixName);
	        	prefixValueMap.put(newPrefix, prefixValue);
	        	changed = changed || (prefixValue != null && prefixValue.length() != 0);
	        	if (logger.isDebugEnabled()) {
	        		logger.debug("Changed the name associated with the prefix " + prefixValue + " from " + currentPrefixName + " to " + newPrefix + " changed = " + changed);
	        	}
	        	fireTableDataChanged();
	        }
	        break;
		case PREFIX:
	        // Replacing value
			if (logger.isDebugEnabled()) {
				logger.debug("Changing the value associated with the prefix " + currentPrefixName + " with a delete and an add.");
			}
			removeMapping(currentPrefixName);
	        addMapping(currentPrefixName, aValue.toString());
	        break;
		default:
			throw new UnsupportedOperationException("Programmer error: missed a case");
		}
	}
}
