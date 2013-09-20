package org.protege.editor.owl.model.io;


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 16-Sep-2008<br><br>
 *
 * A listener that is informed of load and save events on an
 * OWLModel.
 */
public abstract class IOListener {

    public abstract void beforeSave(IOListenerEvent event);

    public abstract void afterSave(IOListenerEvent event);

    public abstract void beforeLoad(IOListenerEvent event);

    public abstract void afterLoad(IOListenerEvent event);

}
