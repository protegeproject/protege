package org.protege.editor.owl.model.namespace;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface NamespaceManagerListener {

    public void mappingAdded(String prefix, String namespace);


    public void mappingRemoved(String prefix, String namespace);
}
