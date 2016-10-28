package org.protege.editor.owl.ui.selector;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.event.ChangeListener;

import org.protege.editor.core.ui.menu.PopupMenuId;
import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.AssertedClassSubHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.protege.editor.owl.ui.tree.OWLTreeDragAndDropHandler;
import org.protege.editor.owl.ui.view.AbstractOWLEntityHierarchyViewComponent;
import org.protege.editor.owl.ui.view.cls.ToldOWLClassHierarchyViewComponent;
import org.semanticweb.owlapi.model.OWLClass;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: May 2, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLClassSelectorPanel extends AbstractHierarchySelectorPanel<OWLClass> {

    private static final long serialVersionUID = -7010322785054275542L;
    
    private MyToldOWLClassHierarchyViewComponent vc;



    public OWLClassSelectorPanel(OWLEditorKit editorKit) {
        this(editorKit, true);
    }

    public OWLClassSelectorPanel(OWLEditorKit editorKit, boolean editable) {
        this(editorKit, editable, editorKit.getModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider());
    }

    public OWLClassSelectorPanel(OWLEditorKit editorKit, boolean editable, OWLObjectHierarchyProvider<OWLClass> hp) {
        super(editorKit, editable, hp);
    }
    
    private class MyToldOWLClassHierarchyViewComponent extends ToldOWLClassHierarchyViewComponent {
    	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void performExtraInitialisation() throws Exception {
    		if (isEditable()){
                super.performExtraInitialisation();
            }

    		JButton searchbutton = new JButton("Search");
    		
    		addAction(new DisposableAction("Search", searchbutton.getIcon()) {
    			
    			private static final long serialVersionUID = 1L;

    			public void actionPerformed(ActionEvent event) {

    				Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();
    				if(focusOwner == null) {
    					return;
    				}                		        
    				OWLClass cls = getOWLWorkspace().searchForClass(focusOwner);
    				if (cls != null) {
    					setSelectedEntity(cls);
    				}
    			}

    			public void dispose() {
    				
    			}
    		}, "A", "A");
    		
    		getTree().setDragAndDropHandler(new OWLTreeDragAndDropHandler<OWLClass>() {
                public boolean canDrop(Object child, Object parent) {
                    return false;
                }
                public void move(OWLClass child, OWLClass fromParent, OWLClass toParent) {}
                public void add(OWLClass child, OWLClass parent) {}
            });
            getAssertedTree().setPopupMenuId(new PopupMenuId("[NCIAssertedClassHierarchy]")); 
        }
       
        protected OWLObjectHierarchyProvider<OWLClass> getHierarchyProvider() {
            return OWLClassSelectorPanel.this.getHierarchyProvider();
        }
        
        public void refreshTree() {
        	this.getTree().reload();
        }
    };

    protected ViewComponentPlugin getViewComponentPlugin() {

        return new ViewComponentPluginAdapter() {
            public String getLabel() {
                return "Asserted Class Hierarchy";
            }


            public Workspace getWorkspace() {
                return getOWLEditorKit().getWorkspace();
            }


            public ViewComponent newInstance() throws ClassNotFoundException, IllegalAccessException,
                    InstantiationException {
                vc = new MyToldOWLClassHierarchyViewComponent(); 
                vc.setup(this);
                return vc;
            }


            public Color getBackgroundColor() {
                return OWLSystemColors.getOWLClassColor();
            }
        };
    }
    
    public void setTreeRoot(OWLClass root) {
    	AssertedClassSubHierarchyProvider sap =  (AssertedClassSubHierarchyProvider) getHierarchyProvider();
    	sap.setRoot(root);
    	vc.refreshTree();
    	
    	
    	
    }

    public void setSelection(OWLClass cls) {
        vc.setSelectedEntity(cls);
    }


    public void setSelection(Set<OWLClass> clses) {
        vc.setSelectedEntities(clses);
    }


    public OWLClass getSelectedObject() {
        return vc.getSelectedEntity();
    }

    public Set<OWLClass> getSelectedObjects() {
        return vc.getSelectedEntities();
    }

    public void dispose() {
        vc.dispose();
    }

    public void addSelectionListener(ChangeListener listener) {
        vc.addChangeListener(listener);
    }

    public void removeSelectionListener(ChangeListener listener) {
        vc.removeChangeListener(listener);
    }
}
