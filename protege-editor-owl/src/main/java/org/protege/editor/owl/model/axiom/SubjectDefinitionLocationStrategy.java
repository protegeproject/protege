package org.protege.editor.owl.model.axiom;

import com.google.common.base.Optional;
import org.protege.editor.owl.model.HasActiveOntology;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.List;
import java.util.Set;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public class SubjectDefinitionLocationStrategy implements FreshAxiomLocationStrategy {

    private TopologicallySortedImportsClosureProvider importsClosureProvider;

    private SubjectDefinitionExtractor subjectDefinitionExtractor;

    private AxiomSubjectProvider axiomSubjectProvider;

    public SubjectDefinitionLocationStrategy(
            TopologicallySortedImportsClosureProvider importsClosureProvider,
            AxiomSubjectProvider axiomSubjectProvider,
            SubjectDefinitionExtractor subjectDefinitionExtractor) {
        this.importsClosureProvider = importsClosureProvider;
        this.axiomSubjectProvider = axiomSubjectProvider;
        this.subjectDefinitionExtractor = subjectDefinitionExtractor;
    }

    @Override
    public OWLOntology getFreshAxiomLocation(OWLAxiom axiom, HasActiveOntology hasActiveOntology) {
        Optional<OWLObject> subject = axiomSubjectProvider.getAxiomSubject(axiom);
        if(!subject.isPresent()) {
            return getDefaultLocation(hasActiveOntology);
        }
        return getDefiningImportsClosureRootOntology(hasActiveOntology, subject.get());
    }

    private OWLOntology getDefiningImportsClosureRootOntology(HasActiveOntology hasActiveOntology, OWLObject subject) {
        OWLOntology activeOntology = hasActiveOntology.getActiveOntology();
        List<OWLOntology> sortedImportsClosure = importsClosureProvider.getTopologicallySortedImportsClosure(activeOntology);
        for(OWLOntology ont : sortedImportsClosure) {
            Set<? extends OWLAxiom> definingAxioms = subjectDefinitionExtractor.getDefiningAxioms(subject, ont);
            if(!definingAxioms.isEmpty()) {
                return ont;
            }
        }
        return getDefaultLocation(hasActiveOntology);
    }




    private OWLOntology getDefaultLocation(HasActiveOntology hasActiveOntology) {
        return hasActiveOntology.getActiveOntology();
    }
}
