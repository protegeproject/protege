package org.protege.editor.owl.model.annotation;

import org.protege.editor.owl.model.user.UserNameProvider;
import org.protege.editor.owl.model.util.DateFormatter;
import org.semanticweb.owlapi.model.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
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

    private final Provider<IRI> userNameAnnotationPropertyIriProvider;

    private final UserNameProvider userNameProvider;

    private final Provider<IRI> dateAnnotationPropertyIRIProvider;

    private final DateFormatter dateFormatter;

    @Inject
    public SimpleEntityCreationMetadataProvider(
            Provider<IRI> userNameAnnotationPropertyIriProvider,
            UserNameProvider userNameProvider,
            Provider<IRI> dateAnnotationPropertyIRIProvider,
            DateFormatter dateFormatter) {
        this.userNameProvider = checkNotNull(userNameProvider);
        this.userNameAnnotationPropertyIriProvider = userNameAnnotationPropertyIriProvider;
        this.dateFormatter = checkNotNull(dateFormatter);
        this.dateAnnotationPropertyIRIProvider = dateAnnotationPropertyIRIProvider;
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
                                    df.getOWLAnnotationProperty(userNameAnnotationPropertyIriProvider.get()),
                                    entity.getIRI(),
                                    df.getOWLLiteral(userName.get())
                            )
                    ));
            changes.add(
                    new AddAxiom(
                            targetOntology,
                            df.getOWLAnnotationAssertionAxiom(
                                    df.getOWLAnnotationProperty(dateAnnotationPropertyIRIProvider.get()),
                                    entity.getIRI(),
                                    df.getOWLLiteral(dateFormatter.formatDate(date))
                            )
                    )
            );
        }
        return changes;
    }
}
