package org.protege.editor.owl.ui.ontology.location;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLOntology;


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

    private static final Logger logger = Logger.getLogger(PhysicalLocationTableModel.class);

    private OWLModelManager owlModelManager;

    private Map<URI, URI> uriMap;

    private List<URI> ontologyURIs;

    private static final String [] COLUMN_NAMES = new String []{"Ontology", "Physical location"};


    public PhysicalLocationTableModel(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
        uriMap = new TreeMap<URI, URI>();
        ontologyURIs = new ArrayList<URI>();
        refill();
    }


    public void dispose() {

    }


    public void refill() {
        uriMap.clear();
        ontologyURIs.clear();
        for (OWLOntology ont : owlModelManager.getOntologies()) {
            ontologyURIs.add(ont.getURI());
            uriMap.put(ont.getURI(), owlModelManager.getOntologyPhysicalURI(ont));
        }
    }


    public int getRowCount() {
        return ontologyURIs.size();
    }


    public int getColumnCount() {
        return 2;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        URI ontologyURI = ontologyURIs.get(rowIndex);
        if (columnIndex == 0) {
            return ontologyURI;
        }
        else {
            return uriMap.get(ontologyURI);
        }
    }


    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
}
