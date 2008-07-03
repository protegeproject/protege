package org.protege.editor.owl.model.refactor;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLObjectDuplicator;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;/*
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 3, 2008<br><br>
 *
 * Cannot use an OWLEntityRenamer directly because multiple entities may be referenced by the same axiom
 */
public class EntityFindAndReplaceURIRenamer {

    private Logger logger = Logger.getLogger(EntityFindAndReplaceURIRenamer.class);


    private OWLOntologyManager mngr;

    private Collection<OWLEntity> entities;
    private Set<OWLOntology> ontologies;
    private String pattern;
    private String newText;

    private Map<OWLEntity, URI> uriMap = new HashMap<OWLEntity, URI>();
    private Map<OWLEntity, String> errorMap = new HashMap<OWLEntity, String>();

    public EntityFindAndReplaceURIRenamer(OWLOntologyManager mngr, Collection<OWLEntity> entities, Set<OWLOntology> ontologies, String pattern, String newText) {
        this.mngr = mngr;
        this.entities = entities;
        this.ontologies = ontologies;
        this.pattern = pattern;
        this.newText = newText;

        generateNameMap();
    }

    
        private void generateNameMap() {
        for (OWLEntity entity : entities){
            String newURIStr = entity.getURI().toString().replaceAll("(?i)" + pattern, newText);
            try {
                URI newURI = new URI(newURIStr);
                if (!newURI.isAbsolute()){
                    throw new URISyntaxException(newURIStr, "URI must be absolute");
                }
                uriMap.put(entity, newURI);
            }
            catch (URISyntaxException e) {
                errorMap.put(entity, newURIStr);
            }
        }
    }


    public boolean hasErrors(){
        return !errorMap.isEmpty();
    }


    public Map<OWLEntity, String> getErrors(){
        return Collections.unmodifiableMap(errorMap);
    }


    public List<OWLOntologyChange> getChanges(){
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        // perform the rename across all loaded ontologies
        for(OWLOntology ont : ontologies) {
            Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
            for (OWLEntity entity : entities){
                axioms.addAll(ont.getReferencingAxioms(entity));
            }

            OWLObjectDuplicator duplicator = new OWLObjectDuplicator(uriMap, mngr.getOWLDataFactory());
            fillListWithTransformChanges(changes, axioms, ont, duplicator);
        }

        return changes;
    }


    private static void fillListWithTransformChanges(List<OWLOntologyChange> changes,
                                                     Set<OWLAxiom> axioms, OWLOntology ont,
                                                     OWLObjectDuplicator duplicator) {
        for(OWLAxiom ax : axioms) {
            changes.add(new RemoveAxiom(ont, ax));
            OWLAxiom dupAx = duplicator.duplicateObject(ax);
            changes.add(new AddAxiom(ont, dupAx));
        }
    }
}
