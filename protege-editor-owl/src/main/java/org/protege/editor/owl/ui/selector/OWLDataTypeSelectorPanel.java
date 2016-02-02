package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.util.OWLDataTypeUtils;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 27-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 *
 */
public class OWLDataTypeSelectorPanel extends AbstractSelectorPanel<OWLDatatype> {
    private static final long serialVersionUID = 1925753640367589134L;

    private AbstractOWLViewComponent vc;

    private OWLObjectList<OWLDatatype> list;
    
    private Map<ChangeListener, ListSelectionListener> selListenerWrappers = new HashMap<ChangeListener, ListSelectionListener>();

    private class UpdateDatatypeListListener implements OWLOntologyChangeListener {
        public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
            if (datatypesMightHaveChanged(changes)) {
                rebuildDatatypeList();
            }
        }
        
        private boolean datatypesMightHaveChanged(List<? extends OWLOntologyChange> changes) {
            for (OWLOntologyChange change : changes) {
                if (change instanceof OWLAxiomChange) {
                    for (OWLEntity e : change.getAxiom().getSignature()) {
                        if (e instanceof OWLDatatype && !e.isBuiltIn()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        
    }


    private class ActiveOntologyChangedListener implements OWLModelManagerListener {
        public void handleChange(OWLModelManagerChangeEvent event) {
            switch (event.getType()) {
            case ACTIVE_ONTOLOGY_CHANGED:
                rebuildDatatypeList();
                break;
            }
        }
    }

    private class OWLDatatypeListView extends AbstractOWLViewComponent {
        private static final long serialVersionUID = -2407766608313199261L;
        private OWLOntologyChangeListener ontologyChangeListener = new UpdateDatatypeListListener();
        private OWLModelManagerListener p4Listener = new ActiveOntologyChangedListener();


        protected void initialiseOWLView() throws Exception {
            setLayout(new BorderLayout());

            list = new OWLObjectList<>(getOWLEditorKit());
            List<OWLDatatype> datatypes = getDatatypeList();
            OWLDatatype[] objects = datatypes.toArray(new OWLDatatype[datatypes.size()]);
            list.setListData(objects);
            list.setSelectedIndex(0);

            add(ComponentFactory.createScrollPane(list));
            getOWLModelManager().addOntologyChangeListener(ontologyChangeListener);
            getOWLModelManager().addListener(p4Listener);
        }


        protected void disposeOWLView() {
            getOWLModelManager().removeOntologyChangeListener(ontologyChangeListener);
            getOWLModelManager().removeListener(p4Listener);
        }
    }


    public OWLDataTypeSelectorPanel(OWLEditorKit editorKit) {
        super(editorKit);
    }


    public OWLDataTypeSelectorPanel(OWLEditorKit editorKit, boolean editable) {
        super(editorKit, editable);
    }


    public OWLDataTypeSelectorPanel(OWLEditorKit editorKit, boolean editable, boolean autoCreateUI) {
        super(editorKit, editable, autoCreateUI);
    }


    public void setSelection(OWLDatatype dt) {
        list.setSelectedValue(dt, true);
    }


    public void setSelection(Set<OWLDatatype> ranges) {
        list.setSelectedValues(ranges, true);
    }


    public OWLDatatype getSelectedObject(){
        return list.getSelectedValue();
    }


    public Set<OWLDatatype> getSelectedObjects() {
        return new HashSet<OWLDatatype>(list.getSelectedOWLObjects());
    }


    protected ViewComponentPlugin getViewComponentPlugin() {
        return new ViewComponentPluginAdapter(){

            public String getLabel() {
                return "Datatypes";
            }


            public Workspace getWorkspace() {
                return getOWLEditorKit().getWorkspace();
            }


            public ViewComponent newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
                vc = new OWLDatatypeListView();
                vc.setup(this);
                return vc;
            }
        };
    }


    public void dispose() {
        vc.dispose();
    }



    public void addSelectionListener(ChangeListener listener) {
        list.addListSelectionListener(wrapListener(listener));
    }


    public void removeSelectionListener(ChangeListener listener) {
        list.removeListSelectionListener(wrapListener(listener));
    }


    private ListSelectionListener wrapListener(final ChangeListener listener) {
        ListSelectionListener l = selListenerWrappers.get(listener);
        if (l == null){
            l = event -> listener.stateChanged(new ChangeEvent(list));
            selListenerWrappers.put(listener, l);
        }
        return l;
    }
    
    private List<OWLDatatype> getDatatypeList() {
        OWLOntologyManager mngr = getOWLModelManager().getOWLOntologyManager();
        List<OWLDatatype> datatypeList = new ArrayList<>(new OWLDataTypeUtils(mngr).getKnownDatatypes(getOWLModelManager().getActiveOntologies()));
        Collections.sort(datatypeList, getOWLModelManager().getOWLObjectComparator());
        return datatypeList;
    }
    
    private void rebuildDatatypeList() {
        OWLDatatype selected = list.getSelectedValue();
        List<OWLDatatype> datatypes = getDatatypeList();
        list.setListData(datatypes.toArray(new OWLDatatype[datatypes.size()]));
        if (datatypes.contains(selected)) {
            list.setSelectedValue(selected, true);
        }
    }
}
