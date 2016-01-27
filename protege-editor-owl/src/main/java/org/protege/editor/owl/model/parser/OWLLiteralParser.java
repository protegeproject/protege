package org.protege.editor.owl.model.parser;

import com.google.common.base.Optional;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.function.Function;
import java.util.regex.Pattern;


/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public class OWLLiteralParser {

    private static final String FLOAT_PATTERN = "(\\+|-)?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)([Ee](\\+|-)?[0-9]+)?(f|F)?|(\\+|-)?INF|NaN";
    public static final LiteralParser FLOAT_LITERAL_PARSER = new LiteralParser(Pattern.compile(FLOAT_PATTERN), OWL2Datatype.XSD_FLOAT, s -> s);

    private static final String BOOLEAN_PATTERN = "true|false";
    private static final LiteralParser BOOLEAN_LITERAL_PARSER = new LiteralParser(Pattern.compile(BOOLEAN_PATTERN), OWL2Datatype.XSD_BOOLEAN, String::toLowerCase);
    private static final LiteralParser INTEGER_LITERAL_PARSER = new LiteralParser(OWL2Datatype.XSD_INTEGER);
    private static final LiteralParser DECIMAL_LITERAL_PARSER = new LiteralParser(OWL2Datatype.XSD_DECIMAL);

    private OWLDataFactory dataFactory;

    public OWLLiteralParser(OWLDataFactory dataFactory) {
        this.dataFactory = dataFactory;
    }

    public OWLLiteral parseLiteral(String value) {
        if(value == null) {
            throw new NullPointerException("value must not be null");
        }
        String normalisedValue = value.trim();
        return parse(normalisedValue,
                BOOLEAN_LITERAL_PARSER,
                INTEGER_LITERAL_PARSER,
                DECIMAL_LITERAL_PARSER,
                FLOAT_LITERAL_PARSER);


    }

    private OWLLiteral parse(String value, LiteralParser ... parsers) {
        for(LiteralParser parser : parsers) {
            Optional<OWLLiteral> lit = parser.parse(value, dataFactory);
            if(lit.isPresent()) {
                return lit.get();
            }
        }
        return dataFactory.getOWLLiteral(value, OWL2Datatype.RDF_PLAIN_LITERAL);
    }

    private static class LiteralParser {

        private final Pattern pattern;

        private final OWL2Datatype datatype;

        private final Function<String, String> normalisation;

        private LiteralParser(OWL2Datatype datatype) {
            this(datatype.getPattern(), datatype, s -> s);
        }

        protected LiteralParser(Pattern pattern, OWL2Datatype datatype, Function<String, String> normalisation) {
            this.pattern = pattern;
            this.datatype = datatype;
            this.normalisation = normalisation;
        }

        public Optional<OWLLiteral> parse(String value, OWLDataFactory dataFactory) {
            String normalisedValue = normalisation.apply(value);
            if(pattern.matcher(normalisedValue).matches()) {
                return Optional.of(dataFactory.getOWLLiteral(normalisedValue, datatype));
            }
            else {
                return Optional.absent();
            }
        }
    }
}
