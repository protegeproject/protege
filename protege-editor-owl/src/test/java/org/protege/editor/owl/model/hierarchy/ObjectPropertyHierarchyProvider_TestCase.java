package org.protege.editor.owl.model.hierarchy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectProperty;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Ontology;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.SubObjectPropertyOf;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 25/01/16
 */
@RunWith(MockitoJUnitRunner.class)
public class ObjectPropertyHierarchyProvider_TestCase {

    private OWLObjectProperty superProperty;

    private OWLObjectProperty subProperty;

    private Set<OWLOntology> ontologies;

    private OWLObjectPropertyHierarchyProvider hierarchyProvider;

    @Before
    public void setUp() throws Exception {
        PrefixManager pm = new DefaultPrefixManager();
        pm.setDefaultPrefix("http://www.ontologies.com/ontology/");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = Ontology(
                manager,
                SubObjectPropertyOf(
                        subProperty = ObjectProperty("subProperty", pm),
                        superProperty = ObjectProperty("superProperty", pm)
                )
        );
        ontologies = Collections.singleton(ontology);
        hierarchyProvider = new OWLObjectPropertyHierarchyProvider(manager);
        hierarchyProvider.setOntologies(ontologies);
    }

    @Test
    public void shouldReturnSuperProperty() {
        Collection<OWLObjectProperty> supers = hierarchyProvider.getParents(subProperty);
        assertThat(supers, contains(superProperty));
    }

    @Test
    public void shouldReturnSubProperty() {
        Collection<OWLObjectProperty> subs = hierarchyProvider.getChildren(superProperty);
        assertThat(subs, contains(subProperty));
    }
}
