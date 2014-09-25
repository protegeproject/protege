package org.protege.editor.core.ui.action;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 22, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An <code>ToolBarActionPlugin</code> is the base class
 * for all editorkit toolbar and view toolbar actions. A
 * toolbar action plugin, describes the icon, tooltip text,
 * group.
 */
public interface ToolBarActionPlugin extends ProtegeActionPlugin {

    public String getGroup();


    public String getGroupIndex();
}
