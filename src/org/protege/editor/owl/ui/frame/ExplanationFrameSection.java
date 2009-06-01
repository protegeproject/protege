package org.protege.editor.owl.ui.frame;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.protege.editor.owl.ui.ontology.wizard.create.CreateOntologyWizard;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLEntityCollector;
import uk.ac.manchester.cs.bhig.util.Tree;
import uk.ac.manchester.cs.owl.explanation.ordering.DefaultExplanationOrderer;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationTree;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Oct-2007<br><br>
 */
public class ExplanationFrameSection extends AbstractOWLFrameSection<OWLAxiom, OWLAxiom, OWLAxiom>{

    private static Logger log = Logger.getLogger(ExplanationFrameSection.class);


    private Set<OWLAxiom> axioms;

    private OWLAxiom entailedAxiom;

    private boolean addedEntailedAxiom;

    private Set<OWLAxiom> added;

    private List<OWLAxiom> orderedAxioms;

    private Map<OWLAxiom, Integer> indentMap;

    private List<MListButton> additionalButtons;


    public ExplanationFrame getFrame() {
        return (ExplanationFrame) super.getFrame();
    }


    private Map<String, String> nameReplacementMap;

    public ExplanationFrameSection(OWLEditorKit editorKit, int explanationNumber, OWLAxiom entailedAxiom, Set<OWLAxiom> axioms,  OWLFrame<? extends OWLAxiom> owlFrame) {
        super(editorKit, "Explanation " + explanationNumber, "Explanation", owlFrame);
        this.axioms = new HashSet<OWLAxiom>(axioms);
        orderedAxioms = new ArrayList<OWLAxiom>(axioms);
        indentMap = new HashMap<OWLAxiom, Integer>();
        this.entailedAxiom = entailedAxiom;
        added = new HashSet<OWLAxiom>();
        resetIndents();
        sortAxioms();

        additionalButtons = new ArrayList<MListButton>();
        additionalButtons.add(new ExtractOntology());
        additionalButtons.add(new CopyToClipboard());
        nameReplacementMap = new HashMap<String, String>();
        fillReplacementMap();
    }


    public Set<OWLAxiom> getAxioms() {
        return new HashSet<OWLAxiom>(axioms);
    }


    public OWLAxiom getEntailedAxiom() {
        return entailedAxiom;
    }


    private void fillReplacementMap() {
        nameReplacementMap.clear();
        Set<OWLEntity> entities = new HashSet<OWLEntity>();
        OWLEntityCollector collector = new OWLEntityCollector();
        for(OWLAxiom ax : orderedAxioms) {
            ax.accept(collector);
        }
        entities.addAll(collector.getObjects());
        int clsCount = 0;
        int opCount = 0;
        int dpCount = 0;
        int indCount = 0;
       for(OWLEntity ent : entities) {
            if(ent.isOWLClass()) {
                OWLClass cls = ent.asOWLClass();
                if (!cls.isOWLThing() && !cls.isOWLNothing()) {
                    nameReplacementMap.put(getOWLModelManager().getRendering(ent), "C" + clsCount);
                    clsCount++;
                }
            }
            else if(ent.isOWLObjectProperty()) {
                StringBuilder sb = new StringBuilder("prop");
                char ch = (char) (97 + opCount);
                sb.append(ch);
                nameReplacementMap.put(getOWLModelManager().getRendering(ent), sb.toString());
                opCount++;
            }
            else if(ent.isOWLDataProperty()) {
                nameReplacementMap.put(getOWLModelManager().getRendering(ent), "dp" + dpCount);
                dpCount++;
            }
            else if(ent.isOWLIndividual()) {
                nameReplacementMap.put(getOWLModelManager().getRendering(ent), "i" + indCount);
                indCount++;
            }
        }
    }

    public String replaceNames(String s) {
        List<String> sortedNames = new ArrayList<String>(nameReplacementMap.keySet());
        Collections.sort(sortedNames, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        });
        for(String rep : sortedNames) {
            s = s.replaceAll("\\b" + rep + "\\b", nameReplacementMap.get(rep));
        }
        return s;
    }

    public int getIndent(OWLAxiom axiom) {
        if(!indentMap.containsKey(axiom)) {
            return 0;
        }
        return indentMap.get(axiom);
    }

    public void resetIndents() {
        for(OWLAxiom ax : orderedAxioms) {
            indentMap.put(ax, 0);
        }
    }

    public void increaseIndent(OWLAxiom axiom) {
        int indent = indentMap.get(axiom);
        indent++;
        indentMap.put(axiom, indent);
        getFrame().fireContentChanged();
    }

    public void decreaseIndent(OWLAxiom axiom) {
        int indent = indentMap.get(axiom);
        indent--;
        if(indent < 0) {
            indent = 0;
        }
        indentMap.put(axiom, indent);
        getFrame().fireContentChanged();
    }

    protected void clear() {
        addedEntailedAxiom = false;
        added.clear();
    }


    public boolean canAdd() {
        return false;
    }


    protected OWLAxiom createAxiom(OWLAxiom object) {
        return object;
    }


    public OWLFrameSectionRowObjectEditor<OWLAxiom> getObjectEditor() {
        return null;
    }



    protected void refill(OWLOntology ontology) {
        if(!addedEntailedAxiom) {
            addRow(new ExplanationFrameSectionRow(getOWLEditorKit(), this, null, getRootObject(), entailedAxiom));
            addedEntailedAxiom = true;
        }
        sortAxioms();
        for(OWLAxiom ax : orderedAxioms) {
            if(!added.contains(ax)) {
                added.add(ax);
                addRow(new ExplanationFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
            }
        }

    }

    public Comparator<OWLFrameSectionRow<OWLAxiom, OWLAxiom, OWLAxiom>> getRowComparator() {
        return null;
    }

    public boolean moveUp(OWLAxiom axiom) {
        int originalIndex = orderedAxioms.indexOf(axiom);
        if(originalIndex < 1) {
            return false;
        }
        orderedAxioms.remove(axiom);
        orderedAxioms.add(originalIndex - 1, axiom);
        reset();
        return true;
    }

    public boolean moveDown(OWLAxiom axiom) {
        if(axiom.equals(entailedAxiom)) {
            return false;
        }
        int originalIndex = orderedAxioms.indexOf(axiom);
        originalIndex++;
        if(originalIndex > orderedAxioms.size() - 1) {
            return false;
        }
        orderedAxioms.remove(axiom);
        orderedAxioms.add(originalIndex, axiom);
        reset();
        return true;
    }



    private void sortAxioms() {

        if (((ExplanationFrame) getFrame()).isUseOrdering()) {
            resetIndents();
            if(entailedAxiom == null) {
                return;
            }
            orderedAxioms.clear();
            DefaultExplanationOrderer orderer = new DefaultExplanationOrderer();
            ExplanationTree tree = orderer.getOrderedExplanation(entailedAxiom, axioms);
            add(tree);
            orderedAxioms.remove(0);
        }
    }

    private void add(Tree<OWLAxiom> tree) {
        orderedAxioms.add(tree.getUserObject());
        indentMap.put(tree.getUserObject(), tree.getPathToRoot().size() - 1);
        for(Tree<OWLAxiom> child : tree.getChildren()) {
            add(child);
        }
    }


    public List<MListButton> getAdditionalButtons() {
        return additionalButtons;
    }

    public void extractOntology() {
        CreateOntologyWizard wizard = new CreateOntologyWizard((Frame) SwingUtilities.getAncestorOfClass(Frame.class, getOWLEditorKit().getWorkspace()), getOWLEditorKit());
        int ret = wizard.showModalDialog();
        if (ret == Wizard.FINISH_RETURN_CODE) {
            try {
                OWLOntology ont = getOWLModelManager().createNewOntology(wizard.getOntologyID(), wizard.getLocationURI());
                getOWLModelManager().getOWLOntologyManager().setOntologyFormat(ont, wizard.getFormat());
                List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
                for(OWLAxiom ax : orderedAxioms) {
                    changes.add(new AddAxiom(ont, ax));
                }
                getOWLModelManager().applyChanges(changes);

            }
            catch (OWLOntologyCreationException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyToClipboard() {
        StringBuilder sb = new StringBuilder();
        for(OWLAxiom ax : orderedAxioms) {
            for(int i = 0; i < getIndent(ax); i++) {
                sb.append("\t");
            }
            String ren = getOWLModelManager().getRendering(ax);
            ren = ren.replace('\n', ' ');
            ren = ren.replaceAll("\\s+", " ");
            sb.append(ren);
            sb.append("\n");
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);

    }

    private class ExtractOntology extends MListButton {

        public ExtractOntology() {
            super("Extract ontology", new Color(107, 71, 162));
            setActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    extractOntology();
                    log.info(e.getSource());
                }
            });

        }


        public void paintButtonContent(Graphics2D g) {
            Rectangle bounds = getBounds();
            g.drawOval(bounds.x + 2, bounds.y + 2, bounds.width - 4, bounds.height - 4);
        }
    }


    private class CopyToClipboard extends MListButton {


        public CopyToClipboard() {
            super("Copy to clipboard", Color.DARK_GRAY);
            setActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    copyToClipboard();
                }
            });
        }


        public void paintButtonContent(Graphics2D g) {
            Rectangle bounds = getBounds();
            int dim = bounds.width - 12;
            g.drawRect(bounds.x + 4, bounds.y + 4, dim, dim);
            g.drawRect(bounds.x + 8, bounds.y + 8, dim, dim);
        }
    }

}
