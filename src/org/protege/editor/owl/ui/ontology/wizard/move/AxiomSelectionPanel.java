package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.AssertedClassHierarchyProvider2;
import org.protege.editor.owl.model.hierarchy.OWLDataPropertyHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectPropertyHierarchyProvider;
import org.protege.editor.owl.model.selection.axioms.AnnotationAxiomsStrategy;
import org.protege.editor.owl.model.selection.axioms.AxiomSelectionStrategy;
import org.protege.editor.owl.model.selection.axioms.AxiomTypeStrategy;
import org.protege.editor.owl.model.selection.axioms.EntityReferencingAxiomsStrategy;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.frame.AnnotationURIList;
import org.protege.editor.owl.ui.ontology.wizard.merge.SelectOntologiesPage;
import org.protege.editor.owl.ui.ontology.wizard.merge.SelectTargetOntologyPage;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.selector.*;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 20, 2008
 */
public class AxiomSelectionPanel extends AbstractOWLWizardPanel {

    public static final String ID = "AxiomSelectionPanel";

    private JList list;

    private JList axiomTypeSelector;

    private AnnotationURIList annotationURISelector;

    private JComponent helperPanel;

    private AbstractSelectorPanel entitySelector;
//    private OWLEntitySelector entitySelector;


    // only used when an entity ref strategy in play
    private ChangeListener selListener = new ChangeListener(){
        public void stateChanged(ChangeEvent changeEvent) {
            handleHelperSelectionUpdate();
        }
    };

    // only used when an axiom type strategy in play
    private ListSelectionListener selListener2 = new ListSelectionListener(){
        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            handleHelperSelectionUpdate();
        }
    };

    private TreeSelectionListener treeListener = new TreeSelectionListener(){
        public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
            handleHelperSelectionUpdate();
        }
    };
    private static final EmptyBorder EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);


    public AxiomSelectionPanel(OWLEditorKit eKit) {
        super(ID, "Select the axioms that will be moved", eKit);
    }


    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());

        helperPanel = new JPanel(new BorderLayout());
        helperPanel.setOpaque(false);

        list = new JList();
        list.setCellRenderer(new OWLCellRenderer(getOWLEditorKit()));
        JPanel axiomListPane = new JPanel(new BorderLayout(6, 6));
        axiomListPane.setOpaque(false);
        axiomListPane.add(new JLabel("Select axioms to move"), BorderLayout.NORTH);
        axiomListPane.add(new JScrollPane(list), BorderLayout.CENTER);
        JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                              helperPanel, axiomListPane);
        splitpane.setBackground(Color.WHITE); // make the divider white
        splitpane.setOpaque(false);
        splitpane.setBorder(EMPTY_BORDER);
        parent.add(splitpane, BorderLayout.CENTER);
    }


    public void aboutToDisplayPanel() {
        AxiomSelectionStrategy strategy = getStrategy();

        setInstructions(strategy.getName());

        clearHelperPanel();
        if (strategy instanceof EntityReferencingAxiomsStrategy){
            Class type = ((EntityReferencingAxiomsStrategy) strategy).getType();
//            entitySelector = OWLEntitySelector.createEntitySelector(type, getOWLEditorKit());
//            entitySelector.addSelectionListener(treeListener);

            entitySelector = createEntitySelector(type);

            if (entitySelector != null){
                helperPanel.add(entitySelector);
            }
            else{
                final JLabel errLabel = new JLabel("Type does not correspond to a entitySelector");
                errLabel.setBackground(Color.RED);
                helperPanel.add(errLabel, BorderLayout.CENTER);
            }
        }
        else if (strategy instanceof AxiomTypeStrategy){
            helperPanel.add(new JScrollPane(createAxiomTypeSelector()), BorderLayout.CENTER);
        }
        else if (strategy instanceof AnnotationAxiomsStrategy){
            final JScrollPane scroller = new JScrollPane(createAnnotationURISelector());
            helperPanel.add(scroller, BorderLayout.CENTER);
        }

        handleHelperSelectionUpdate();
    }

    /** Return an entity selector.
     * Only show the entities referenced by the ontologies selected
     */
    private AbstractSelectorPanel createEntitySelector(Class type) {
        AbstractSelectorPanel selector = null;

        final OWLOntologyManager mngr = getOWLModelManager().getOWLOntologyManager();
        if (type.equals(OWLClass.class)){
            OWLObjectHierarchyProvider<OWLClass> hp = new AssertedClassHierarchyProvider2(mngr);
            hp.setOntologies(getOntologies());
            selector = new OWLClassSelectorPanel(getOWLEditorKit(), false, hp);
        }
        else if (type.equals(OWLObjectProperty.class)){
            OWLObjectHierarchyProvider<OWLObjectProperty> hp = new OWLObjectPropertyHierarchyProvider(mngr);
            hp.setOntologies(getOntologies());
            selector = new OWLObjectPropertySelectorPanel(getOWLEditorKit(), false, hp);
        }
        else if (type.equals(OWLDataProperty.class)){
            OWLObjectHierarchyProvider<OWLDataProperty> hp = new OWLDataPropertyHierarchyProvider(mngr);
            hp.setOntologies(getOntologies());
            selector = new OWLDataPropertySelectorPanel(getOWLEditorKit(), false, hp);
        }
        else if (type.equals(OWLIndividual.class)){
            selector = new OWLIndividualSelectorPanel(getOWLEditorKit(), false, getOntologies());
        }

        if (selector != null){
            selector.addSelectionListener(selListener);
        }
        return selector;
    }

    private void clearHelperPanel() {
        helperPanel.removeAll();
        if (axiomTypeSelector != null){
            axiomTypeSelector.removeListSelectionListener(selListener2);
            axiomTypeSelector = null;
        }
        if (entitySelector != null){
            entitySelector.removeSelectionListener(selListener);
            entitySelector = null;
        }
        if (annotationURISelector != null){
            annotationURISelector.removeListSelectionListener(selListener2);
            annotationURISelector = null;
        }
    }

    private JComponent createAnnotationURISelector() {
        annotationURISelector = new AnnotationURIList(getOWLEditorKit());
        annotationURISelector.rebuildAnnotationURIList();
        annotationURISelector.setSelectedURI(OWLRDFVocabulary.RDFS_LABEL.getURI());
        annotationURISelector.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        annotationURISelector.addListSelectionListener(selListener2);
        return annotationURISelector;
    }


    private JComponent createAxiomTypeSelector() {
        axiomTypeSelector = new JList(AxiomType.AXIOM_TYPES.toArray());
        axiomTypeSelector.addListSelectionListener(selListener2);
        return axiomTypeSelector;
    }


    private void handleHelperSelectionUpdate() {
        if (entitySelector != null){
            final EntityReferencingAxiomsStrategy strategy = ((EntityReferencingAxiomsStrategy) getStrategy());
            strategy.setEntities(entitySelector.getSelectedObjects());
        }
        else if (axiomTypeSelector != null){
            final AxiomTypeStrategy strategy = (AxiomTypeStrategy) getStrategy();
            Set<AxiomType<? extends OWLAxiom>> selectedTypes = new HashSet<AxiomType<? extends OWLAxiom>>();
            for (Object o : axiomTypeSelector.getSelectedValues()){
                selectedTypes.add((AxiomType)o);
            }
            strategy.setTypes(selectedTypes);
        }
        else if (annotationURISelector != null){
            final AnnotationAxiomsStrategy strategy = (AnnotationAxiomsStrategy) getStrategy();
            strategy.setURIs(annotationURISelector.getSelectedURIs());
        }

        list.setListData(getStrategy().getAxioms(getOntologies()).toArray());
    }

    private AxiomSelectionStrategy getStrategy() {
        return ((AxiomSelectionStrategyPanel)getWizardModel().getPanel(AxiomSelectionStrategyPanel.ID)).getSelectionStrategy();
    }

    private Set<OWLOntology> getOntologies() {
        return ((SelectOntologiesPage)getWizardModel().getPanel(SelectOntologiesPage.ID)).getOntologies();
    }

    public Object getBackPanelDescriptor() {
        return AxiomSelectionStrategyPanel.ID;
    }

    public Object getNextPanelDescriptor() {
        return SelectTargetOntologyPage.ID;
    }

    public Set<OWLAxiom> getSelectedAxioms() {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (Object o : list.getSelectedValues()){
            axioms.add((OWLAxiom)o);
        }
        return axioms;
    }
}
