package org.protege.editor.owl.ui.hierarchy;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLHierarchyManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLPropertyHierarchyProvider;
import org.protege.editor.owl.ui.view.AbstractOWLEntityHierarchyViewComponent;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 10/03/15
 */
public class OWLPropertyHierarchyViewComponent extends AbstractOWLEntityHierarchyViewComponent<OWLEntity> {

    private OWLPropertyHierarchyProvider hierarchyProvider;

    @Override
    protected OWLObjectHierarchyProvider<OWLEntity> getHierarchyProvider() {
        if(hierarchyProvider == null) {
            OWLModelManager modelManager = getOWLModelManager();
            OWLHierarchyManager hierarchyManager = modelManager.getOWLHierarchyManager();
            hierarchyProvider = new OWLPropertyHierarchyProvider(
                    modelManager.getOWLOntologyManager(),
                    hierarchyManager.getOWLObjectPropertyHierarchyProvider(),
                    hierarchyManager.getOWLDataPropertyHierarchyProvider(),
                    hierarchyManager.getOWLAnnotationPropertyHierarchyProvider());
        }
        return hierarchyProvider;
    }

    @Override
    protected void performExtraInitialisation() throws Exception {
        getTree().setOWLObjectComparator(new Comparator<OWLObject>() {
            @Override
            public int compare(OWLObject o1, OWLObject o2) {
                if(o1 instanceof OWLObjectProperty) {
                    if(!(o2 instanceof OWLObjectProperty)) {
                        return -1;
                    }
                }
                else if(o1 instanceof OWLDataProperty) {
                    if(o2 instanceof OWLObjectProperty) {
                        return 1;
                    }
                    else {
                        return -1;
                    }
                }
                else {
                    if(o2 instanceof OWLObjectProperty) {
                        return 1;
                    }
                    else if(o2 instanceof OWLDataProperty) {
                        return 1;
                    }
                }
                String s1 = getOWLModelManager().getRendering(o1);
                String s2 = getOWLModelManager().getRendering(o2);
                return s1.compareToIgnoreCase(s2);
            }
        });
    }

    /**
     * Request that the view is updated to display the current selection.
     *
     * @return The OWLEntity that the view is displaying.  This
     * list is typically used to generate the view header text to give the
     * user an indication of what the view is displaying.
     */
    @Override
    protected OWLObject updateView() {
        return getTree().getSelectedOWLObject();
    }

    @Override
    public List<OWLEntity> find(String match) {
        return Collections.emptyList();
    }

    @Override
    protected Optional<OWLObjectHierarchyProvider<OWLEntity>> getInferredHierarchyProvider() {
        return Optional.empty();
    }
}
