package org.protege.editor.owl.ui.ontology.location;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.swing.table.AbstractTableModel;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 02-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PhysicalLocationTableModel extends AbstractTableModel {

    private OWLModelManager owlModelManager;

    private Map<OWLOntologyID, URI> uriMap;

    private List<OWLOntologyID> ontologyIDs;

    private static final String [] COLUMN_NAMES = new String []{"Ontology", "Physical location"};


    public PhysicalLocationTableModel(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
        uriMap = new TreeMap<OWLOntologyID, URI>();
        ontologyIDs = new ArrayList<OWLOntologyID>();
        refill();
    }


    public void dispose() {

    }


    public void refill() {
        uriMap.clear();
        ontologyIDs.clear();
        for (OWLOntology ont : owlModelManager.getOntologies()) {
            ontologyIDs.add(ont.getOntologyID());
            uriMap.put(ont.getOntologyID(), owlModelManager.getOntologyPhysicalURI(ont));
        }
    }


    public int getRowCount() {
        return ontologyIDs.size();
    }


    public int getColumnCount() {
        return 2;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        OWLOntologyID id = ontologyIDs.get(rowIndex);
        if (columnIndex == 0) {
            return id;
        }
        else {
            return uriMap.get(id);
        }
    }


    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
}
