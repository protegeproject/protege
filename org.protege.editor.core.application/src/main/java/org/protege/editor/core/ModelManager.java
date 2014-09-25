package org.protege.editor.core;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 16, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface ModelManager extends Disposable {

    public boolean isDirty();


    public <T extends Disposable> void put(Object key, T object);


    public <T extends Disposable> T get(Object key);
}
