package org.protege.editor.owl.model.axiom;

import com.google.common.base.Optional;
import org.protege.editor.owl.model.HasActiveOntology;
import org.semanticweb.owlapi.model.*;

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
        if(!(subject.get() instanceof OWLEntity)) {
            return getDefaultLocation(hasActiveOntology);
        }

        OWLEntity entity = (OWLEntity) subject.get();
        return getDefiningImportsClosureRootOntology(hasActiveOntology, entity);
    }

    private OWLOntology getDefiningImportsClosureRootOntology(HasActiveOntology hasActiveOntology, OWLEntity entity) {
        OWLOntology activeOntology = hasActiveOntology.getActiveOntology();
        List<OWLOntology> sortedImportsClosure = importsClosureProvider.getTopologicallySortedImportsClosure(activeOntology);
        for(OWLOntology ont : sortedImportsClosure) {
            Set<? extends OWLAxiom> definingAxioms = subjectDefinitionExtractor.getDefiningAxioms(entity, ont);
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
