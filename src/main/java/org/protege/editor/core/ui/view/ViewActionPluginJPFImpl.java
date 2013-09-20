package org.protege.editor.core.ui.view;


import javax.swing.AbstractAction;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.action.ToolBarActionPluginJPFImpl;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 28, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewActionPluginJPFImpl extends ToolBarActionPluginJPFImpl implements ViewActionPlugin {

    public static final String EXTENSION_POINT_ID = "ViewAction";

    public static final String VIEW_ID_PARAM = "viewId";


    private View view;


    public ViewActionPluginJPFImpl(EditorKit editorKit, View view, IExtension extension) {
        super(editorKit, extension);
        this.view = view;
    }


    public String getViewId() {
        return getPluginProperty(VIEW_ID_PARAM);
    }


    public ProtegeAction newInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        ViewAction viewAction = (ViewAction) super.newInstance();
        viewAction.putValue(AbstractAction.NAME, getName());
        viewAction.putValue(AbstractAction.SHORT_DESCRIPTION, getToolTipText());
        viewAction.setEditorKit(getEditorKit());
        viewAction.setView(view);
        viewAction.setEditorKit(getEditorKit());
        return viewAction;
    }


    public View getView() {
        return view;
    }
}
