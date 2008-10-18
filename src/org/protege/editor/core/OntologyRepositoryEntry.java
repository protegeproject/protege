package org.protege.editor.core;

import org.protege.editor.core.editorkit.EditorKit;

import java.net.URI;
import java.util.Map;
/*
 * Copyright (C) 2008, University of Manchester
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
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 18-Oct-2008<br><br>
 */
public interface OntologyRepositoryEntry {

    /**
     * Gets a short human readable name for this entry
     * @return A short human readable name
     */
    String getOntologyShortName();


    /**
     * Gets the URI of the ontology that is described by this entry.
     * @return The ontology URI.
     */
    URI getOntologyURI();


    /**
     * Gets the physical URI of the ontology that is described by this entry.
     * @return The physical URI.
     */
    URI getPhysicalURI();


    /**
     * Gets associated metadata.
     * @param key The key that describes the metadata
     * @return The metadata or <code>null</code> if there is no metadata associated with this key.
     */
    String getMetaData(Object key);


    /**
     * Gets the editor kit factory ID that should be used to load this ontology.
     * @return The ID of the editor kit factory
     */
    String getEditorKitId();


    /**
     * If this ontology gets loaded, this method will be called before loading starts in order to
     * provided the opportunity to configure an editorkit (e.g. for specifying an ontology mapper/redirector
     * etc. etc.).  Any editor kit configuration changes made MUST be reverted in the restoreEditorKit() method.
     * @param editorKit The editor kit that will be used to load the ontology.
     */
    void configureEditorKit(EditorKit editorKit);


    /**
     * This method will be called after loading to enable any changes or configuration of the editor kit to be
     * reverted/rolled back.
     * @param editorKit The editor kit.
     */
    void restoreEditorKit(EditorKit editorKit);
}
