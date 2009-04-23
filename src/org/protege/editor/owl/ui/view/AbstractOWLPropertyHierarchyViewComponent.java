package org.protege.editor.owl.ui.view;

import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 23, 2008
 */
public abstract class AbstractOWLPropertyHierarchyViewComponent<O extends OWLProperty> extends AbstractOWLEntityHierarchyViewComponent<O>
        implements Findable<O>, Deleteable {

    final protected OWLEntity updateView() {
        OWLProperty selProp = null;
        if (isOWLDataPropertyView()){
            selProp = updateView((O)getOWLWorkspace().getOWLSelectionModel().getLastSelectedDataProperty());
        }
        else if (isOWLObjectPropertyView()){
            selProp = updateView((O)getOWLWorkspace().getOWLSelectionModel().getLastSelectedObjectProperty());
        }
        if (selProp != null) {
            updateRegisteredActions();
        }
        else {
            disableRegisteredActions();
        }
        return selProp;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Findable
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public java.util.List<O> find(String match) {
        if (isOWLDataPropertyView()){
            return new ArrayList<O>((Set<O>)getOWLModelManager().getEntityFinder().getMatchingOWLDataProperties(match));
        }
        else if (isOWLObjectPropertyView()){
            return new ArrayList<O>((Set<O>)getOWLModelManager().getEntityFinder().getMatchingOWLObjectProperties(match));
        }
        return Collections.emptyList();
    }
}
