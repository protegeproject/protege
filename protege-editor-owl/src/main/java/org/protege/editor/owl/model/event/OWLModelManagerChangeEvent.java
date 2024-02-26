package org.protege.editor.owl.model.event;

import static com.google.common.base.MoreObjects.toStringHelper;

import org.protege.editor.owl.model.OWLModelManager;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 19, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLModelManagerChangeEvent {

    private OWLModelManager source;

    private EventType type;


    public OWLModelManagerChangeEvent(OWLModelManager source, EventType type) {
        this.source = source;
        this.type = type;
    }


    public OWLModelManager getSource() {
        return source;
    }


    public EventType getType() {
        return type;
    }

    public boolean isType(EventType type) {
        return this.type.equals(type);
    }


    @Override
    public String toString() {
        return toStringHelper("OWLModelManagerChangeEvent")
                .addValue(source)
                .addValue(type)
                .toString();
    }
}
