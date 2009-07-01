package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.util.OWLDataTypeUtils;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 27-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 * Should extend AbstractSelectorPanel
 */
public class OWLDataTypeSelectorPanel extends AbstractSelectorPanel<OWLDatatype> {

    private AbstractOWLViewComponent vc;

    private OWLObjectList<OWLDatatype> list;


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
        return (OWLDatatype)list.getSelectedValue();
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
                vc = new AbstractOWLViewComponent(){

                    protected void initialiseOWLView() throws Exception {
                        setLayout(new BorderLayout());

                        // Add the built in datatypes
                        final OWLOntologyManager mngr = getOWLModelManager().getOWLOntologyManager();
                        java.util.List<OWLDatatype> datatypeList = new ArrayList<OWLDatatype>(new OWLDataTypeUtils(mngr).getKnownDatatypes(getOWLModelManager().getActiveOntologies()));
                        Collections.sort(datatypeList, getOWLModelManager().getOWLObjectComparator());

                        list = new OWLObjectList<OWLDatatype>(getOWLEditorKit());
                        list.setListData(datatypeList.toArray());
                        list.setSelectedIndex(0);

                        add(ComponentFactory.createScrollPane(list));
                    }


                    protected void disposeOWLView() {
                        // do nothing
                    }
                };
                vc.setup(this);
                return vc;
            }
        };
    }


    public void dispose() {
        vc.dispose();
    }


// wrap the listeners and just pass the changes straight through


    private Map<ChangeListener, ListSelectionListener> selListenerWrappers =
            new HashMap<ChangeListener, ListSelectionListener>();


    public void addSelectionListener(ChangeListener listener) {
        list.addListSelectionListener(wrapListener(listener));
    }


    public void removeSelectionListener(ChangeListener listener) {
        list.removeListSelectionListener(wrapListener(listener));
    }


    private ListSelectionListener wrapListener(final ChangeListener listener) {
        ListSelectionListener l = selListenerWrappers.get(listener);
        if (l == null){
            l = new ListSelectionListener(){
                public void valueChanged(ListSelectionEvent event) {
                    listener.stateChanged(new ChangeEvent(list));
                }
            };
            selListenerWrappers.put(listener, l);
        }
        return l;
    }
}
