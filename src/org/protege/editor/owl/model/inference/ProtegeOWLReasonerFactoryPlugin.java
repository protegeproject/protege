package org.protege.editor.owl.model.inference;

import org.protege.editor.core.plugin.ProtegePlugin;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface ProtegeOWLReasonerFactoryPlugin extends ProtegePlugin<ProtegeOWLReasonerFactory> {

    public static final String REASONER_PLUGIN_TYPE_ID = "org.protege.editor.owl.inference.reasonerfactory";


    /**
     * Gets a <code>String</code> that represents the reasoner ID.
     */
    public String getId();


    /**
     * Gets the name of the reasoner.  This should be
     * human readable, because it is generally used for
     * menu labels etc.
     */
    public String getName();
}
