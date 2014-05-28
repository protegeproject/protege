package org.protege.editor.owl.model.entity;

import com.google.common.collect.ImmutableList;
import org.obolibrary.obo2owl.Obo2OWLConstants;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.vocab.DublinCoreVocabulary;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;

import java.util.Comparator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public class AnnotationPropertyComparator implements Comparator<OWLAnnotationProperty> {

    private Comparator<? super OWLAnnotationProperty> delegate;

    private ImmutableList<IRI> annotationPropertyIRIOrdering;

    public AnnotationPropertyComparator(ImmutableList<IRI> annotationPropertyIRIOrdering, Comparator<? super OWLAnnotationProperty> delegate) {
        this.annotationPropertyIRIOrdering = checkNotNull(annotationPropertyIRIOrdering);
        this.delegate = checkNotNull(delegate);
    }

    /**
     * Gets the system default ordering for annotation properties.
     *
     * @param delegate A delegate that is used for comparing properties that are not specified with the default ordering.
     *                 Not {@code null}.
     * @return The comparator.
     */
    public static AnnotationPropertyComparator withDefaultOrdering(Comparator<? super OWLAnnotationProperty> delegate) {
        return new AnnotationPropertyComparator(DEFAULT_ORDERING, checkNotNull(delegate));
    }

    private static final ImmutableList<IRI> DEFAULT_ORDERING = ImmutableList.<IRI>builder()
            .add(
                    // Labels
                    OWLRDFVocabulary.RDFS_LABEL.getIRI(),
                    SKOSVocabulary.PREFLABEL.getIRI(),
                    DublinCoreVocabulary.TITLE.getIRI(),

                    // OBO stuff that's important
                    // Id.  I can't find a constant for this.
                    IRI.create("http://www.geneontology.org/formats/oboInOwl#id"),
                    Obo2OWLConstants.Obo2OWLVocabulary.hasAlternativeId.getIRI(),
                    Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasOboNamespace.getIRI(),

                    // Definitions
                    Obo2OWLConstants.Obo2OWLVocabulary.IRI_IAO_0000115.getIRI(),
                    SKOSVocabulary.DEFINITION.getIRI(),
                    SKOSVocabulary.NOTE.getIRI(),

                    OWLRDFVocabulary.RDFS_COMMENT.getIRI(),

                    // Other labels
                    SKOSVocabulary.ALTLABEL.getIRI(),

                    OWLRDFVocabulary.RDFS_SEE_ALSO.getIRI(),
                    OWLRDFVocabulary.RDFS_IS_DEFINED_BY.getIRI(),

                    // Important OBO annotations.  Ordering derived from documents shared by Chris M.
                    Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasExactSynonym.getIRI(),
                    Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasNarrowSynonym.getIRI(),
                    Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasBroadSynonym.getIRI(),

                    Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasDbXref.getIRI(),
                    Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_Subset.getIRI()


            ).build();


    @Override
    public int compare(OWLAnnotationProperty o1, OWLAnnotationProperty o2) {
        IRI iri1 = o1.getIRI();
        IRI iri2 = o2.getIRI();
        int index1 = getIndex(iri1);
        int index2 = getIndex(iri2);
        int indexDiff = index1 - index2;
        if (indexDiff != 0) {
            return indexDiff;
        }
        return delegate.compare(o1, o2);
    }

    private int getIndex(IRI iri1) {
        int index = annotationPropertyIRIOrdering.indexOf(iri1);
        if (index == -1) {
            return Integer.MAX_VALUE;
        } else {
            return index;
        }
    }
}
