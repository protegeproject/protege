package org.protege.editor.owl.ui.prefix;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Comparator.naturalOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.swing.table.AbstractTableModel;

import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PrefixMapperTableModel extends AbstractTableModel {

	private final Logger logger = LoggerFactory.getLogger(PrefixMapperTableModel.class);
	
	public enum Column {
		PREFIX_NAME, PREFIX
	}
	

	@Nonnull
	private final List<String> prefixNames = new ArrayList<>();

	@Nonnull
    private final Map<String, String> prefixName2PrefixMap = new HashMap<>();

    @Nonnull
    private final PrefixDocumentFormat prefixManager;
    
    private boolean changed = false;


    public PrefixMapperTableModel(@Nonnull PrefixDocumentFormat prefixManager) {
    	this.prefixManager = checkNotNull(prefixManager);
        refill();
    }

    public void refill() {
    	changed = false;
    	// arguably here we should only delete the prefixNames that don't have an empty value
    	// it is a little weird when they disappear because they were not committed to the PrefixOWLOntologyFormat
    	prefixNames.clear();
    	prefixName2PrefixMap.clear();

		prefixManager.getPrefixName2PrefixMap()
				.forEach(prefixName2PrefixMap::put);
        prefixNames.addAll(prefixName2PrefixMap.keySet());
        prefixNames.sort(naturalOrder());
        fireTableDataChanged();
    }
    
    public int getIndexOfPrefix(String prefix) {
    	return prefixNames.indexOf(prefix);
    }

    public int addMapping(String prefix, String value) {
    	changed = changed || (value != null && value.length() != 0);
    	prefixNames.add(prefix);
        prefixName2PrefixMap.put(prefix, value);
	    fireTableDataChanged();
        return prefixNames.indexOf(prefix);
    }


    public void removeMapping(String prefix) {
    	prefixNames.remove(prefix);
	    String prefixValue = prefixName2PrefixMap.remove(prefix);
	    changed = changed || (prefixValue != null & prefixValue.length() != 0);
	    fireTableDataChanged();
	}

	public boolean commitPrefixes() {
		if(!changed) {
			return false;
		}
		prefixManager.setPrefixManager(new DefaultPrefixManager());
		for (Map.Entry<String, String> prefixName2PrefixEntry : prefixName2PrefixMap.entrySet()) {
			String prefixName = prefixName2PrefixEntry.getKey();
			String prefix     = prefixName2PrefixEntry.getValue();
			if (prefix != null && prefix.length() != 0) {
				prefixManager.setPrefix(prefixName, prefix);
			}
		}
		changed = false;
		return true;
	}


    public void sortTable() {
    	Collections.sort(prefixNames);
    }

    /*
     * Table Model interfaces
     */

	@Override
	public String getColumnName(int column) {
		switch (Column.values()[column]) {
		case PREFIX_NAME:
			return "Prefix Name";
		case PREFIX:
			return "Prefix";
		default:
			throw new UnsupportedOperationException("Programmer error: missed a case");
		}
	}


	public int getRowCount() {
	    return prefixNames.size();
	}


	public int getColumnCount() {
	    return Column.values().length;
	}


	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
	    return !PrefixUtilities.isStandardPrefix(prefixNames.get(rowIndex));
	}


	public Object getValueAt(int rowIndex, int columnIndex) {
	    String prefix = prefixNames.get(rowIndex);
		switch (Column.values()[columnIndex]) {
		case PREFIX_NAME:
			return prefix;
		case PREFIX:
			return prefixName2PrefixMap.get(prefix);
		default:
			throw new UnsupportedOperationException("Programmer error: missed a case");
		}
	}


	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	    String currentPrefixName = (String) getValueAt(rowIndex, Column.PREFIX_NAME.ordinal());
		switch (Column.values()[columnIndex]) {
		case PREFIX_NAME:
			setPrefixNameAt(aValue, currentPrefixName);
			break;
		case PREFIX:
			setPrefixAt(aValue, currentPrefixName);
			break;
		default:
			throw new UnsupportedOperationException("Programmer error: missed a case");
		}
	}

	private void setPrefixNameAt(Object aValue,
								 String currentPrefixName) {
		String newPrefix = aValue.toString();
		if(prefixNames.contains(newPrefix)) {
			return;
		}
		prefixNames.remove(currentPrefixName);
		prefixNames.add(newPrefix);
		String prefixValue = prefixName2PrefixMap.remove(currentPrefixName);
		prefixName2PrefixMap.put(newPrefix, prefixValue);
		changed = changed || (prefixValue != null && prefixValue.length() != 0);
		fireTableDataChanged();
	}

	private void setPrefixAt(Object aValue,
							 String currentPrefixName) {
		removeMapping(currentPrefixName);
		addMapping(currentPrefixName, aValue.toString());
	}
}
