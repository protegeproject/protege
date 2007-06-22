package org.protege.editor.owl.model;

import java.net.URI;
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
 * Date: 31-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * The missing import handler is called by the system as a last resort if
 * it cannot obtain a physical URI of an ontology (and hence cannot load
 * the ontology).
 */
public interface MissingImportHandler {

    /**
     * Given an ontology URI, the missing import handler
     * sets up the appropriate libraries/mappings so that
     * the system can obtain a physical URI for the given
     * ontology.
     * @param ontologyURI The ontology URI that identifies the
     *                    ontology that the system is trying to obtain a physical
     *                    URI for.
     * @return A physical URI that locates the specified ontology.
     *         <b>Must NOT be <code>null</code></b>
     */
    public URI getPhysicalURI(URI ontologyURI);
}
