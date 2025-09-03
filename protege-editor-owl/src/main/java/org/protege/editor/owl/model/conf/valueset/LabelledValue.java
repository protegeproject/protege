package org.protege.editor.owl.model.conf.valueset;

import com.google.common.base.Objects;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import javax.annotation.Nonnull;

import java.util.Optional;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 30 Aug 2017
 *
 * Represents a labelled value.  The value can be any object.
 */
public class LabelledValue {

    @Nonnull
    private final Object value;

    @Nonnull
    private final String label;

    /**
     * Constructs a labelled value.
     * @param value The value.
     * @param label The label for the value.
     */
    public LabelledValue(@Nonnull Object value,
                         @Nonnull String label) {
        this.value = checkNotNull(value);
        this.label = checkNotNull(label);
    }

    /**
     * Gets the value.
     */
    @Nonnull
    public Object getValue() {
        return value;
    }

    /**
     * Get the value label.
     */
    @Nonnull
    public String getLabel() {
        return label;
    }

    /**
     * Converts the value to an annotation if possible.  If the value is an OWL annotation value (IRI, Literal or
     * Anonymous Individual) then this is returned.  If the value is an entity then the IRI of the entity is returned.
     * If the value is a String then this is returned as a xsd:string Literal.  If the value is a Boolean then this is
     * returned as a xsd:boolean Literal.  If the value is an Integer then this is returned as an Integer Literal. If
     * the value is a double then this is returned as a xsd:decimal Literal.  If the value is a float then this is returned
     * as a xsd:decimal literal.
     * @return The converted value.
     */
    @Nonnull
    public Optional<OWLAnnotationValue> toOWLAnnotationValue(@Nonnull OWLDataFactory dataFactory) {
        if(value instanceof OWLAnnotationValue) {
            return Optional.of((OWLAnnotationValue) value);
        }
        else if(value instanceof OWLEntity) {
            return Optional.of(((OWLEntity) value).getIRI());
        }
        else if(value instanceof String) {
            return Optional.of(dataFactory.getOWLLiteral((String) value));
        }
        else if(value instanceof Boolean) {
            return Optional.of(dataFactory.getOWLLiteral((Boolean) value));
        }
        else if(value instanceof Integer) {
            return Optional.of(dataFactory.getOWLLiteral((Integer) value));
        }
        else if(value instanceof Double) {
            return Optional.of(dataFactory.getOWLLiteral(Double.toString((Double) value), OWL2Datatype.XSD_DECIMAL));
        }
        else if(value instanceof Float) {
            return Optional.of(dataFactory.getOWLLiteral(Double.toString((Float) value), OWL2Datatype.XSD_DECIMAL));
        }
        else {
            return Optional.empty();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value, label);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LabelledValue)) {
            return false;
        }
        LabelledValue other = (LabelledValue) obj;
        return this.value.equals(other.value)
                && this.label.equals(other.label);
    }


    @Override
    public String toString() {
        return toStringHelper("LabelledValue")
                .add("value", value)
                .add("label", label)
                .toString();
    }
}
