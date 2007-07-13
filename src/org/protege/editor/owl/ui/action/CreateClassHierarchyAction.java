package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.model.hierarchy.tabbed.Edge;
import org.protege.editor.owl.model.hierarchy.tabbed.OWLClassHierarchyCreator;
import org.protege.editor.owl.model.hierarchy.tabbed.TabIndentedHierarchyParser;
import org.protege.editor.owl.ui.hierarchy.creation.CreateClassHierarchyWizard;
import org.semanticweb.owl.model.OWLOntologyChange;

import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class CreateClassHierarchyAction extends ProtegeOWLAction {

    public void actionPerformed(ActionEvent e) {
        CreateClassHierarchyWizard w = new CreateClassHierarchyWizard(getOWLEditorKit());
        if (w.showModalDialog() == Wizard.FINISH_RETURN_CODE) {
            try {
                String hierarchy = w.getHierarchy();
                String prefix = w.getPrefix().trim();
                String suffix = w.getSuffix().trim();
                TabIndentedHierarchyParser parser = new TabIndentedHierarchyParser(4, prefix, suffix);
                List<Edge> edges = parser.parse(new StringReader(hierarchy));
                OWLClassHierarchyCreator creator = new OWLClassHierarchyCreator(getOWLModelManager().getOWLDataFactory(),
                                                                                w.getRootClass(),
                                                                                w.isMakeSiblingClassesDisjoint(),
                                                                                getOWLModelManager().getActiveOntology(),
                                                                                edges);
                List<OWLOntologyChange> changes = creator.createHierarchy();
                w.dispose();
                getOWLModelManager().applyChanges(changes);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
