package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.io.OWLObjectRenderer;
import org.semanticweb.owl.model.OWLAxiom;

import java.util.HashSet;
import java.util.Set;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 14-Oct-2007<br><br>
 */
public class ExplanationFrame extends AbstractOWLFrame<OWLAxiom> {

    private OWLEditorKit owlEditorKit;

    private String subClassAxiomSymbol = "subClassOf";

    private String equivalentClassesAxiomSymbol = "equivalentTo";

    private String disjointClassesAxiomSymbol = "disjointWith";

    private boolean obfuscateNames = true;

    private boolean useOrdering = true;

    private OWLObjectRenderer renderer = null;


    public ExplanationFrame(OWLEditorKit owlEditorKit) {
        super(owlEditorKit.getModelManager().getOWLOntologyManager());
        this.owlEditorKit = owlEditorKit;
        addSection(new ExplanationFrameSection(owlEditorKit, 0, null, new HashSet<OWLAxiom>(), this));
    }

    public void setExplanation(OWLAxiom axiom, Set<Set<OWLAxiom>> explanations) {
        clearSections();
        int count = 1;
        for(Set<OWLAxiom> explanation : explanations) {
            ExplanationFrameSection section = new ExplanationFrameSection(owlEditorKit, count, axiom, explanation, this);
            addSection(section);
            count++;
        }
        setRootObject(axiom);
    }


    public void setRenderer(OWLObjectRenderer renderer) {
        this.renderer = renderer;
    }


    public OWLObjectRenderer getRenderer() {
        return renderer;
    }


    public String getSubClassAxiomSymbol() {
        return subClassAxiomSymbol;
    }

    public String getEquivalentClassesAxiomSymbol() {
        return equivalentClassesAxiomSymbol;
    }


    public void setSubClassAxiomSymbol(String subClassAxiomSymbol) {
        this.subClassAxiomSymbol = subClassAxiomSymbol;
        fireContentChanged();
    }


    public void setEquivalentClassesAxiomSymbol(String equivalentClassesAxiomSymbol) {
        this.equivalentClassesAxiomSymbol = equivalentClassesAxiomSymbol;
        fireContentChanged();
    }


    public String getDisjointClassesAxiomSymbol() {
        return disjointClassesAxiomSymbol;
    }


    public void setDisjointClassesAxiomSymbol(String disjointClassesAxiomSymbol) {
        this.disjointClassesAxiomSymbol = disjointClassesAxiomSymbol;
        fireContentChanged();
    }


    public boolean isUseOrdering() {
        return useOrdering;
    }


    public void setUseOrdering(boolean useOrdering) {
        this.useOrdering = useOrdering;
        refill();
    }


    public boolean isObfuscateNames() {
        return obfuscateNames;
    }


    public void setObfuscateNames(boolean obfuscateNames) {
        this.obfuscateNames = obfuscateNames;
        fireContentChanged();
    }
}
