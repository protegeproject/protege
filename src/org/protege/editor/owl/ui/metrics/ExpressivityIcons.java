package org.protege.editor.owl.ui.metrics;

import static org.semanticweb.owl.util.DLExpressivityChecker.Construct.AL;
import static org.semanticweb.owl.util.DLExpressivityChecker.Construct.C;
import static org.semanticweb.owl.util.DLExpressivityChecker.Construct.D;
import static org.semanticweb.owl.util.DLExpressivityChecker.Construct.E;
import static org.semanticweb.owl.util.DLExpressivityChecker.Construct.F;
import static org.semanticweb.owl.util.DLExpressivityChecker.Construct.H;
import static org.semanticweb.owl.util.DLExpressivityChecker.Construct.I;
import static org.semanticweb.owl.util.DLExpressivityChecker.Construct.N;
import static org.semanticweb.owl.util.DLExpressivityChecker.Construct.O;
import static org.semanticweb.owl.util.DLExpressivityChecker.Construct.Q;
import static org.semanticweb.owl.util.DLExpressivityChecker.Construct.S;
import static org.semanticweb.owl.util.DLExpressivityChecker.Construct.U;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import org.protege.editor.owl.ui.OWLIcons;
import org.semanticweb.owl.util.DLExpressivityChecker;


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
