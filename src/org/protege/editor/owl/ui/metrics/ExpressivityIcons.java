package org.protege.editor.owl.ui.metrics;

import org.protege.editor.owl.ui.OWLIcons;
import org.semanticweb.owl.util.DLExpressivityChecker;
import static org.semanticweb.owl.util.DLExpressivityChecker.Construct.*;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 29-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ExpressivityIcons {

    private static Map<DLExpressivityChecker.Construct, Icon> map;


    static {
        map = new HashMap();
        map.put(AL, loadIcon("AL.png"));
        map.put(C, loadIcon("C.png"));
        map.put(U, loadIcon("U.png"));
        map.put(E, loadIcon("E.png"));
        map.put(N, loadIcon("N.png"));
        map.put(Q, loadIcon("Q.png"));
        map.put(H, loadIcon("H.png"));
        map.put(I, loadIcon("I.png"));
        map.put(O, loadIcon("O.png"));
        map.put(F, loadIcon("F.png"));
        map.put(S, loadIcon("S.png"));
        map.put(D, loadIcon("Datatype.png"));
    }


    public static Icon getIcon(DLExpressivityChecker.Construct construct) {
        return map.get(construct);
    }


    private static Icon loadIcon(String name) {
        return OWLIcons.getIcon(name);
    }
}
