package org.protege.editor.owl.model.entity;

import org.semanticweb.owlapi.model.OWLEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.russet.identitas.Util;

/**
 * Author: Nizal Alshammry<br>
 * Newcastle University<br>
 * ICOS Group<br>
 * Date: 29-April-2019<br><br>
 * <p/>
 * n.k.e.alshammry2@newcastle.ac.uk<br>
 * https://github.com/Nizal-Shammry<br>
 */

public class RandomProlong extends AbstractIDGenerator implements AutoIDGenerator {

	private static final Logger logger = LoggerFactory.getLogger(RandomProlong.class);

	private String nextId;

    public String getNextID(Class<? extends OWLEntity> type) throws AutoIDException {
        try {
			nextId = Util.randomProlong();
		} catch (Exception e) {
			logger.error("An error occurred whilst attempting to get the next ID", e);
		}

        return getPrefix(type) + nextId.toString() + getSuffix(type);
    }
   
	protected long getRawID(Class<? extends OWLEntity> type) throws AutoIDException {
		// When implement an interface (AbstractIDGenerator) you must implement all methods
		throw new UnsupportedOperationException("Unexpected Error");
	}

}
