package org.protege.editor.owl.ui.renderer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 12/06/2014
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class OWLEntityRendererImpl_TestCase {

    @Mock
    private OWLEntity entity;

    private IRI iri;

    private OWLEntityRendererImpl renderer;

    @Before
    public void setUp() {
        renderer = new OWLEntityRendererImpl();
        renderer.initialise();
    }

    @Test
    public void shouldProvideFragment() {
        iri = IRI.create("http://www.semanticweb.org/test/ontology#Abc");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("Abc"));
    }

    @Test
    public void shouldProviderFragmentContainingBrackets() {
        iri = IRI.create("http://www.semanticweb.org/test/ontology#Abc(xyz)");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("'Abc(xyz)'"));
    }


    @Test
    public void shouldProvideLastPathElementIfThereIsNotFragment() {
        iri = IRI.create("http://www.semanticweb.org/test/ontology/Abc");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("Abc"));
    }

    @Test
    public void shouldProvideLastPathElementContainingBracketsIfThereIsNotFragment() {
        iri = IRI.create("http://www.semanticweb.org/test/ontology/Abc(xyz)");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("'Abc(xyz)'"));
    }

    @Test
    public void shouldRenderOWLThingWithOWLPrefixName() {
        iri = OWLRDFVocabulary.OWL_THING.getIRI();
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("owl:Thing"));
    }


    @Test
    public void shouldRenderOWLNothingWithOWLPrefixName() {
        iri = OWLRDFVocabulary.OWL_NOTHING.getIRI();
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("owl:Nothing"));
    }


    @Test
    public void shouldRenderOWLTopObjectPropertyWithOWLPrefixName() {
        iri = OWLRDFVocabulary.OWL_TOP_OBJECT_PROPERTY.getIRI();
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("owl:topObjectProperty"));
    }


    @Test
    public void shouldRenderOWLBottomObjectPropertyWithOWLPrefixName() {
        iri = OWLRDFVocabulary.OWL_BOTTOM_OBJECT_PROPERTY.getIRI();
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("owl:bottomObjectProperty"));
    }

    @Test
    public void shouldRenderOWLTopDataPropertyWithOWLPrefixName() {
        iri = OWLRDFVocabulary.OWL_TOP_DATA_PROPERTY.getIRI();
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("owl:topDataProperty"));
    }

    @Test
    public void shouldRenderOWLBottomDataPropertyWithOWLPrefixName() {
        iri = OWLRDFVocabulary.OWL_BOTTOM_DATA_PROPERTY.getIRI();
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("owl:bottomDataProperty"));
    }

    @Test
    public void shouldRenderRDFSLabelWithRDFSPrefixName() {
        iri = OWLRDFVocabulary.RDFS_LABEL.getIRI();
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("rdfs:label"));
    }

    @Test
    public void shouldRenderRDFSCommentWithRDFSPrefixName() {
        iri = OWLRDFVocabulary.RDFS_COMMENT.getIRI();
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("rdfs:comment"));
    }

    @Test
    public void shouldRenderRDFSSeeAlsoWithRDFSPrefixName() {
        iri = OWLRDFVocabulary.RDFS_SEE_ALSO.getIRI();
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("rdfs:seeAlso"));
    }

    @Test
    public void shouldRenderRDFSIsDefinedByWithRDFSPrefixName() {
        iri = OWLRDFVocabulary.RDFS_IS_DEFINED_BY.getIRI();
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("rdfs:isDefinedBy"));
    }

    @Test
    public void shouldRenderOWLBackwardCompatibleWithWithOWLPrefixName() {
        iri = OWLRDFVocabulary.OWL_BACKWARD_COMPATIBLE_WITH.getIRI();
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("owl:backwardCompatibleWith"));
    }


    @Test
    public void shouldRenderOWLDeprecatedWithOWLPrefixName() {
        iri = OWLRDFVocabulary.OWL_DEPRECATED.getIRI();
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("owl:deprecated"));
    }

    @Test
    public void shouldRender_XMLLiteral_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("rdf:XMLLiteral"));
    }


    @Test
    public void shouldRender_RDFSLiteral_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2000/01/rdf-schema#Literal");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("rdfs:Literal"));
    }


    @Test
    public void shouldRender_PlainLiteral_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("rdf:PlainLiteral"));
    }


    @Test
    public void shouldRender_real_WithOWLPrefixName() {
        iri = IRI.create("http://www.w3.org/2002/07/owl#real");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("owl:real"));
    }


    @Test
    public void shouldRender_rational_WithOWLPrefixName() {
        iri = IRI.create("http://www.w3.org/2002/07/owl#rational");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("owl:rational"));
    }


    @Test
    public void shouldRender_string_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#string");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:string"));
    }


    @Test
    public void shouldRender_normalizedString_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#normalizedString");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:normalizedString"));
    }


    @Test
    public void shouldRender_token_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#token");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:token"));
    }


    @Test
    public void shouldRender_language_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#language");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:language"));
    }


    @Test
    public void shouldRender_Name_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#Name");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:Name"));
    }


    @Test
    public void shouldRender_NCName_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#NCName");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:NCName"));
    }


    @Test
    public void shouldRender_NMTOKEN_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#NMTOKEN");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:NMTOKEN"));
    }


    @Test
    public void shouldRender_decimal_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#decimal");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:decimal"));
    }


    @Test
    public void shouldRender_integer_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#integer");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:integer"));
    }


    @Test
    public void shouldRender_nonNegativeInteger_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#nonNegativeInteger");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:nonNegativeInteger"));
    }


    @Test
    public void shouldRender_nonPositiveInteger_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#nonPositiveInteger");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:nonPositiveInteger"));
    }


    @Test
    public void shouldRender_positiveInteger_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#positiveInteger");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:positiveInteger"));
    }


    @Test
    public void shouldRender_negativeInteger_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#negativeInteger");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:negativeInteger"));
    }


    @Test
    public void shouldRender_long_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#long");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:long"));
    }


    @Test
    public void shouldRender_int_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#int");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:int"));
    }


    @Test
    public void shouldRender_short_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#short");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:short"));
    }


    @Test
    public void shouldRender_byte_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#byte");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:byte"));
    }


    @Test
    public void shouldRender_unsignedLong_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#unsignedLong");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:unsignedLong"));
    }


    @Test
    public void shouldRender_unsignedInt_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#unsignedInt");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:unsignedInt"));
    }


    @Test
    public void shouldRender_unsignedShort_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#unsignedShort");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:unsignedShort"));
    }


    @Test
    public void shouldRender_unsignedByte_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#unsignedByte");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:unsignedByte"));
    }


    @Test
    public void shouldRender_double_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#double");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:double"));
    }


    @Test
    public void shouldRender_float_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#float");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:float"));
    }


    @Test
    public void shouldRender_boolean_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#boolean");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:boolean"));
    }


    @Test
    public void shouldRender_hexBinary_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#hexBinary");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:hexBinary"));
    }


    @Test
    public void shouldRender_base64Binary_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#base64Binary");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:base64Binary"));
    }


    @Test
    public void shouldRender_anyURI_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#anyURI");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:anyURI"));
    }


    @Test
    public void shouldRender_dateTime_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#dateTime");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:dateTime"));
    }


    @Test
    public void shouldRender_dateTimeStamp_WithXSDPrefixName() {
        iri = IRI.create("http://www.w3.org/2001/XMLSchema#dateTimeStamp");
        when(entity.getIRI()).thenReturn(iri);
        assertThat(renderer.getShortForm(entity), is("xsd:dateTimeStamp"));
    }




}
