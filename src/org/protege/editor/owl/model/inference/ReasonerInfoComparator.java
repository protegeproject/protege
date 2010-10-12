package org.protege.editor.owl.model.inference;

import java.util.Comparator;

public class ReasonerInfoComparator implements
		Comparator<ProtegeOWLReasonerInfo> {

	public int compare(ProtegeOWLReasonerInfo ri1, ProtegeOWLReasonerInfo ri2) {
		boolean nullReasoner1 = ri1.getReasonerFactory() instanceof NoOpReasonerFactory;
		boolean nullReasoner2 = ri2.getReasonerFactory() instanceof NoOpReasonerFactory;
		if (nullReasoner1 && nullReasoner2) {
			return 0;
		}
		else if (nullReasoner1) {
			return 1;
		}
		else if (nullReasoner2) {
			return -1;
		}
		else {
			return ri1.getReasonerName().compareTo(ri2.getReasonerName());
		}
	}

}
