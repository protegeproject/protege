package org.protege.editor.owl.model.annotation;

import org.protege.editor.owl.model.HasActiveOntology;
import org.protege.editor.owl.model.user.UserNameProvider;
import org.protege.editor.owl.model.util.DateFormatter;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.DublinCoreVocabulary;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/11/15
 */
public class SimpleEntityCreationMetadataProvider implements EntityCreationMetadataProvider {

    private final UserNameProvider userNameProvider;

    private final DateFormatter dateFormatter;

    public SimpleEntityCreationMetadataProvider(UserNameProvider userNameProvider, DateFormatter dateFormatter) {
        this.userNameProvider = checkNotNull(userNameProvider);
        this.dateFormatter = checkNotNull(dateFormatter);
    }

    @Override
    public List<OWLOntologyChange> getEntityCreationMetadataChanges(OWLEntity entity, OWLOntology targetOntology, OWLDataFactory df) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        Optional<String> userName = userNameProvider.getUserName();
        Date date = new Date();
        if(userName.isPresent()) {
            changes.add(
                    new AddAxiom(
                            targetOntology,
                            df.getOWLAnnotationAssertionAxiom(
                                    df.getOWLAnnotationProperty(DublinCoreVocabulary.CREATOR.getIRI()),
                                    entity.getIRI(),
                                    df.getOWLLiteral(userName.get())
                            )
                    ));
            changes.add(
                    new AddAxiom(
                            targetOntology,
                            df.getOWLAnnotationAssertionAxiom(
                                    df.getOWLAnnotationProperty(DublinCoreVocabulary.DATE.getIRI()),
                                    entity.getIRI(),
                                    df.getOWLLiteral(dateFormatter.formatDate(date))
                            )
                    )
            );
        }
        return changes;
    }
}
