package org.protege.editor.core.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */

import javax.swing.table.AbstractTableModel;

import org.protege.editor.core.OntologyRepository;
import org.protege.editor.core.OntologyRepositoryEntry;


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 18-Oct-2008<br><br>
 */
public class RepositoryTableModel extends AbstractTableModel {

    private OntologyRepository repository;

    private List<OntologyRepositoryEntry> entries;

    public static final int ONTOLOGY_URI_COL = 1;

    public RepositoryTableModel(OntologyRepository repository) {
        this.repository = repository;
        entries = new ArrayList<>(repository.getEntries());
        Collections.sort(entries, new EntryShortNameURIComparator());
    }


    public int getColumnCount() {
        return 2 + repository.getMetaDataKeys().size();
    }

    public void sortByColumn(int col) {
        if(col < 0) {
            return;
        }
        if(col >= getColumnCount()) {
            return;
        }
        if(col == 0) {
            sortByShortName();
        }
        else if(col == 1) {
            sortByOntologyURI();
        }
    }

    public void sortByShortName() {
        Collections.sort(entries, new EntryShortNameURIComparator());
        fireTableDataChanged();
    }

    public void sortByOntologyURI() {
        Collections.sort(entries, new EntryURIComparator());
        fireTableDataChanged();
    }

    public int getRowCount() {
        return entries.size();
    }


    public String getColumnName(int column) {
        if(column == 0) {
            return "Name";
        }
        else if(column == 1) {
            return "Ontology URI";
        }
        else if(column < repository.getMetaDataKeys().size() + 2) {
            return repository.getMetaDataKeys().get(column - 2).toString();
        }
        else {
            return "";
        }
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            return entries.get(rowIndex).getOntologyShortName();
        }
        else if(columnIndex == 1) {
            return entries.get(rowIndex).getOntologyURI();
        }
        else {
            return entries.get(rowIndex).getMetaData(repository.getMetaDataKeys().get(columnIndex - 2));
        }
    }


    public OntologyRepositoryEntry getEntryAt(int selectedRow) {
        if(selectedRow == -1) {
            return null;
        }
        return entries.get(selectedRow);
    }


    private class EntryShortNameURIComparator implements Comparator<OntologyRepositoryEntry> {

        public int compare(OntologyRepositoryEntry o1, OntologyRepositoryEntry o2) {
            int diff = o1.getOntologyShortName().compareTo(o2.getOntologyShortName());
            if(diff != 0) {
                return diff;
            }
            return o1.getOntologyURI().compareTo(o2.getOntologyURI());
        }
    }

    private class EntryURIComparator implements Comparator<OntologyRepositoryEntry> {

        public int compare(OntologyRepositoryEntry o1, OntologyRepositoryEntry o2) {
            return o1.getOntologyURI().compareTo(o2.getOntologyURI());
        }
    }
}
