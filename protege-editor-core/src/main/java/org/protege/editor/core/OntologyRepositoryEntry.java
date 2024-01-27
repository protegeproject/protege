package org.protege.editor.core;

import java.net.URI;

import org.protege.editor.core.editorkit.EditorKit;


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
