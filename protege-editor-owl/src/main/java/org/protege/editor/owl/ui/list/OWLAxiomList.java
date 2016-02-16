package org.protege.editor.owl.ui.list;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 14-Sep-2007<br><br>
 */
public class OWLAxiomList extends MList {
	private static final long serialVersionUID = 2024889684812090240L;

	private OWLOntologyManager manager;

    private OWLEditorKit editorKit;


    public OWLAxiomList(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        this.manager = editorKit.getModelManager().getOWLOntologyManager();
        setCellRenderer(new AxiomListItemRenderer());
    }


    public void setAxioms(Set<Set<OWLAxiom>> axiomSet, Set<OWLOntology> ontologies) {
        List<Object> items = new ArrayList<>();
        int count = 1;
        for (Set<OWLAxiom> axioms : axiomSet) {
            items.add("Explanation " + count + ": ");
            count++;
            for (OWLAxiom ax : axioms) {
                for (OWLOntology ont : ontologies) {
                    if (ont.containsAxiom(ax)) {
                        items.add(new AxiomListItem(ax, ont));
                    }
                }
            }
        }
        setListData(items.toArray());
        setFixedCellHeight(24);
    }


    protected void handleDelete() {
        super.handleDelete();
    }


    protected Border createPaddingBorder(JList list, Object value, int index, boolean isSelected,
                                         boolean cellHasFocus) {
        if (value instanceof AxiomListItem) {
            return BorderFactory.createMatteBorder(1, 20, 1, 1, list.getBackground());
        }
        else {
            return super.createPaddingBorder(list, value, index, isSelected, cellHasFocus);
        }
    }


    private class AxiomListItemRenderer implements ListCellRenderer {

        private OWLCellRenderer ren;


        public AxiomListItemRenderer() {
            ren = new OWLCellRenderer(editorKit);
        }


        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            if (value instanceof AxiomListItem) {
                AxiomListItem item = ((AxiomListItem) value);
                ren.setOntology(item.ontology);
                ren.setHighlightKeywords(true);
                ren.setWrap(false);
                return ren.getListCellRendererComponent(list, item.axiom, index, isSelected, cellHasFocus);
            }
            else {
                return ren.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        }
    }


    private class AxiomListItem implements MListItem {

        private OWLAxiom axiom;

        private OWLOntology ontology;


        public AxiomListItem(OWLAxiom axiom, OWLOntology ontology) {
            this.axiom = axiom;
            this.ontology = ontology;
        }


        public boolean isEditable() {
            return false;
        }


        public void handleEdit() {
        }


        public boolean isDeleteable() {
            return ontology.containsAxiom(axiom);
        }


        public boolean handleDelete() {
            try {
                manager.applyChange(new RemoveAxiom(ontology, axiom));
                return true;
            }
            catch (OWLOntologyChangeException e) {
                throw new OWLRuntimeException(e);
            }
        }


        public String getTooltip() {
            if (ontology.containsAxiom(axiom)) {
                return "Asserted in " + ontology.getOntologyID();
            }
            else {
                return "Deleted.  (Was asserted in " + ontology.getOntologyID() + ")";
            }
        }
    }
}
