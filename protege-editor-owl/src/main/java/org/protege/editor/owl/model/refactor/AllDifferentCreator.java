package org.protege.editor.owl.model.refactor;

import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toSet;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AllDifferentCreator {

    private final OWLOntology ont;

    private final Set<OWLOntology> ontologies;

    private final OWLDataFactory dataFactory;

    public AllDifferentCreator(@Nonnull OWLDataFactory dataFactory,
                               @Nonnull OWLOntology ont,
                               @Nonnull Set<OWLOntology> ontologies) {
        this.ont = checkNotNull(ont);
        this.ontologies = new HashSet<>(checkNotNull(ontologies));
        this.dataFactory = checkNotNull(dataFactory);
    }


    public List<OWLOntologyChange> getChanges() {
        Set<OWLIndividual> individuals = ontologies.stream()
                                                   .flatMap(o -> o.getIndividualsInSignature().stream())
                                                   .collect(toSet());
        List<OWLOntologyChange> changes = new ArrayList<>();
        if (!individuals.isEmpty()) {
            changes.add(new AddAxiom(ont, dataFactory.getOWLDifferentIndividualsAxiom(individuals)));
        }
        return changes;
    }
}
