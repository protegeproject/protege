package org.protege.editor.owl.model.deprecation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.io.File;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29 Aug 2017
 */
public class GOProfile_TestCase {

    private DeprecationProfile profile;

    @Before
    public void setUp() throws Exception {
        File file = new File("../protege-desktop/src/main/resources/conf/deprecation/go.yaml");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        profile = mapper.readValue(file, DeprecationProfile.class);
    }

    @Test
    public void shouldReadGOProfileYamlLabelPrefix() {
        assertThat(profile.getDeprecatedEntityLabelPrefix(), is("obsolete"));
    }

    @Test
    public void shouldReadGOProfileYamlAnnotationValuePrefix() {
        assertThat(profile.getPreservedAnnotationValuePrefix(), is("OBSOLETE"));
    }

    @Test
    public void shouldReadGOProfileYamlTextualReason() {
        assertThat(profile.getDeprecationTextualReasonAnnotationPropertyIri(),
                   is(Optional.of(OWLRDFVocabulary.RDFS_COMMENT.getIRI())));
    }

    @Test
    public void shouldReadGOProfileYamlReasonProperty() {
        assertThat(profile.getDeprecationTextualReasonAnnotationPropertyIri(), is(Optional.of(OWLRDFVocabulary.RDFS_COMMENT.getIRI())));
    }

    @Test
    public void shouldReadGOProfileYamlAlternateProperty() {
        assertThat(profile.getAlternateEntityAnnotationPropertyIri(),
                   is(Optional.of(IRI.create("http://www.geneontology.org/formats/oboInOwl#consider"))));
    }

    @Test
    public void shouldReadGOProfileYamlPreservedAnnotations() {
        assertThat(profile.getPreservedAnnotationValuePropertiesIris(),
                   hasItem(IRI.create("http://purl.obolibrary.org/obo/IAO_0000115")));
    }
}
