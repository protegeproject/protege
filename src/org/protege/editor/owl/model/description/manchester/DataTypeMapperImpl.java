package org.protege.editor.owl.model.description.manchester;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owl.vocab.XSDVocabulary;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: May 4, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DataTypeMapperImpl implements DataTypeMapper {

    private Map<String, URI> datatypeMap;


    public DataTypeMapperImpl() {
        datatypeMap = new HashMap<String, URI>();
        for (URI uri : XSDVocabulary.ALL_DATATYPES) {
            datatypeMap.put(uri.getFragment(), uri);
        }
    }


    public URI getOWLDataTypeURI(String key) {
        return datatypeMap.get(key);
    }
}

