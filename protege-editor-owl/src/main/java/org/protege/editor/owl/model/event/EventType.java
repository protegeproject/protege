package org.protege.editor.owl.model.event;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 05-Jul-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public enum EventType {


    ACTIVE_ONTOLOGY_CHANGED,

    ONTOLOGY_VISIBILITY_CHANGED,

    ENTITY_RENDERER_CHANGED,

    ENTITY_RENDERING_CHANGED,

    REASONER_CHANGED,

    ABOUT_TO_CLASSIFY,
    
    ONTOLOGY_CLASSIFIED,

    ONTOLOGY_CREATED,

    ONTOLOGY_LOADED,

    ONTOLOGY_RELOADED,

    ONTOLOGY_SAVED
}
