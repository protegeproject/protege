package org.protege.editor.owl.model.hierarchy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.DataProperty;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Ontology;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.SubDataPropertyOf;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 25/01/16
 */
public class DataPropertyHierarchyProvider_TestCase {

    private OWLDataProperty superProperty;

    private OWLDataProperty subProperty;

    private Set<OWLOntology> ontologies;

    private OWLDataPropertyHierarchyProvider hierarchyProvider;

    @Before
    public void setUp() throws Exception {
        PrefixManager pm = new DefaultPrefixManager();
        pm.setDefaultPrefix("http://www.ontologies.com/ontology/");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = Ontology(
                manager,
                SubDataPropertyOf(
                        subProperty = DataProperty("subProperty", pm),
                        superProperty = DataProperty("superProperty", pm)
                )
        );
        ontologies = Collections.singleton(ontology);
        hierarchyProvider = new OWLDataPropertyHierarchyProvider(manager);
        hierarchyProvider.setOntologies(ontologies);
    }

    @Test
    public void shouldReturnSuperProperty() {
        Collection<OWLDataProperty> supers = hierarchyProvider.getParents(subProperty);
        assertThat(supers, contains(superProperty));
    }

    @Test
    public void shouldReturnSubProperty() {
        Collection<OWLDataProperty> subs = hierarchyProvider.getChildren(superProperty);
        assertThat(subs, contains(subProperty));
    }
    
}
