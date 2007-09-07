package org.protege.editor.owl.ui;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLObjectRenderer;
import org.semanticweb.owl.model.OWLObject;

import java.util.Comparator;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 8, 2006<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
public class OWLObjectComparator<E extends OWLObject> implements Comparator<E> {

    private OWLModelManager owlModelManager;

    public OWLObjectComparator(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }

    public int compare(OWLObject o1, OWLObject o2) {
        final OWLObjectRenderer objRen = owlModelManager.getOWLObjectRenderer();
        final OWLModelManagerEntityRenderer entityRen = owlModelManager.getOWLEntityRenderer();
        String ren1 = objRen.render(o1, entityRen);
        String ren2 = objRen.render(o2, entityRen);
        return ren1.compareToIgnoreCase(ren2);
    }
}
