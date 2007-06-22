package org.protege.editor.owl.ui.ontology.location;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.table.AbstractTableModel;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
/*
 * Copyright (C) 2007, University of Manchester
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
