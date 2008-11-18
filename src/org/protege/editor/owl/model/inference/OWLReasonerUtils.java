package org.protege.editor.owl.model.inference;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLDescription;

import java.util.HashSet;
import java.util.Set;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 18, 2008<br><br>
 */
public class OWLReasonerUtils {

    private OWLReasoner r;


    public OWLReasonerUtils(OWLReasoner r) {
        this.r = r;
    }


    public Set<Set<OWLDescription>> getMostSpecificSets(Set<Set<OWLDescription>> setOfSets) throws OWLReasonerException {
        Set<Set<OWLDescription>> results = new HashSet<Set<OWLDescription>>();
        for(Set<OWLDescription> descrs : setOfSets) {
            if (results.isEmpty()){
                results.add(descrs);
            }
            else{
                for (Set<OWLDescription> result : results){
                    if (getReasoner().isSubClassOf(descrs.iterator().next(), result.iterator().next())){
                        // if the new set is more specific than an existing one, replace the existing one
                        results.remove(result);
                        results.add(descrs);
                    }
                    else if (!getReasoner().isSubClassOf(result.iterator().next(), descrs.iterator().next())){
                        // if the new set is not more general than an existing one, add to the set
                        results.add(descrs);
                    }
                }
            }
        }
        return results;
    }


    protected OWLReasoner getReasoner() {
        return r;
    }


    public Set<OWLDescription> getMostSpecific(Set<OWLDescription> descriptions) throws OWLReasonerException {
        Set<OWLDescription> results = new HashSet<OWLDescription>();
        for(OWLDescription descr : descriptions) {
            if (results.isEmpty()){
                results.add(descr);
            }
            else{
                for (OWLDescription result : results){
                    if (getReasoner().isSubClassOf(descr, result)){
                        // if the new descr is more specific than an existing one, replace the existing one
                        results.remove(result);
                        results.add(descr);
                    }
                    else if (!getReasoner().isSubClassOf(result, descr)){
                        // if the new set is not more general than an existing one, add to the set
                        results.add(descr);
                    }
                }
            }
        }
        return results;
    }
}
