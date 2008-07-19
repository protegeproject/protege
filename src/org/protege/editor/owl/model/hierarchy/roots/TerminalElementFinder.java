package org.protege.editor.owl.model.hierarchy.roots;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class encapsulates a relation, <i>r: X &rarr; X</i>.  Its purpose is to calculate the set of terminal 
 * elements of the transitive closure of the relation r.  We will  define the notion of a terminal
 * element below. 
 * <p>
 * The transitive closure of <i>r</i> is an ordering and we denote it as &le;.  There is a natural 
 * equivalence relation defined by
 * <center> 
 *       x &asymp; y  &hArr;  x &le; y and y &le; x.
 * </center>
 * An element x in X is said to be terminal if for all y with
 * <center>
 *      x &le; y
 * </center>
 * we have x &asymp; y.  This definition is different than the more natural definition that x in X
 * is terminal if for all y and z with
 * <center>
 *      x &asymp; y and y &le; z
 * </center>
 * we have x &asymp; z.  Coincidentally the second definition is both more difficult to calculate and
 * (whew...) not what the gui wants.
 * <p>
 * We want to use  the algorithm in this class in an incremental fashion so at any given time so this routine
 * contain state that includes the current set of all terminal elements. The algorithm calculates whether an 
 * element x in X is terminal as follows:
 * <ol>
 *  <li> it recursively generates all paths  <i>x, x<sub>1</sub>, x<sub>2</sub>, x<sub>3</sub>, ...</i>
 *       where <i>x<sub>i+1</sub> &isin; r(x<sub>i</sub>)</i> and 
 *       <i>x<sub>i</sub> &ne; x<sub>j</sub></i> for <i>i&ne; j</i>.  It is an assumption of the algorithm that 
 *       these paths won't be that long.  
 *       When the path
 *       <center><i>
 *           x, x<sub>1</sub>, ..., x<sub>n</sub>
 *       </i></center>
 *       cannot be extended any further it looks at all  <i>x<sub>i</sub> &ge; x<sub>n</sub></i> and generates the 
 *       assertion that 
 *       <center><i>
 *           x<sub>i</sub>, x<sub>i+1</sub>, ..., x<sub>n</sub>
 *       </i></center>
 *       are equivalent.
 *    </li>
 *    <li> Once all the paths with a given prefix
 *       <center><i>
 *           x, x<sub>1</sub>, ..., x<sub>i</sub>
 *       </i></center>
 *       have been found we can apply the following logic:
 *       <ol>
 *          <li> If any <i>y &isin; r(x<sub>i</sub>)</i> is non-terminal then <i>x<sub>i</sub></i> is non-terminal
 *          <li> If any <i>y &isin; r(x<sub>i</sub>)</i> is in a different equivalence class 
 *               than <i>x<sub>i</sub></i> then <i>x<sub>i</sub></i> is non-terminal
 *          <li> If all <i>y &isin; r(x<sub>i</sub>)</i> are in the same equivalence class 
 *                as <i>x<sub>i</sub></i> and each of the <i>x, x<sub>1</sub>, ...,x<sub>i-1</sub></i>
 *                 are in a different equivalence class than <i>x<sub>i</sub></i> and <i>x<sub>i</sub></i> has not been
 *                 marked  as terminal or non-terminal then <i>x<sub>i</sub></i> can be marked as terminal. 
 *       </ol>
 *       In each of these cases we apply the marking on x_i to its entire equivalence class.
 *       </li>
 *       <li> Finally, anything left over where the status is still unknown is marked as TERMINAL.
 * </ol>
 * @author Timothy Redmond
 *
 */
public class TerminalElementFinder<X> {
    private static Logger log = Logger.getLogger(TerminalElementFinder.class);

    private Relation<X> r;
    private Set<X> terminalElements = new HashSet<X>();
    private EquivalenceRelation<X> equivalence = new EquivalenceRelation<X>();
    
    public TerminalElementFinder(Relation<X> r) {
        this.r = r;
    }
    
    public void findTerminalElements(Set<X> candidates) {
        clear();
        appendTerminalElements(candidates);
        finish();
    }
    
    public void clear() {
        terminalElements = new HashSet<X>();
        equivalence.clear();
    }
    
    /*
     * don't call this when the order relation has changed.  This only adds
     * elements to the terminal elements.  Instead form the unison of the current
     * terminal elements and the new candidates and call findTerminalElements again.
     */
    public void appendTerminalElements(Set<X> candidates) {
        for (X candidate : candidates) {
            if (log.isDebugEnabled()) {
                log.debug("calling build equivs at " + candidate + " with null path");
            }
            buildEquivalenceMapping(candidate, null);
            if (log.isDebugEnabled()) {
                log.debug("Call to build equivs completed at " + candidate);
                equivalence.logEquivalences(log, Level.DEBUG);
            }
        }
    }
        
    public void finish() {
        equivalence.clear();
    }
    
    public Set<X> getTerminalElements() {
        return Collections.unmodifiableSet(terminalElements);
    }
    
    private void buildEquivalenceMapping(X x, Path<X> p) {
        if (p != null && p.contains(x)) {
            equivalence.merge(p.getLoop(x));
            if (log.isDebugEnabled()) {
                log.debug("Found loop");
                logLoop(p, x, Level.DEBUG);
            }
            return;
        }
        Collection<X> relatedToX = r.getR(x);
        if (relatedToX == null || relatedToX.isEmpty()) {
            terminalElements.add(x);
            return;
        }
        Path<X> newPath  = new Path<X>(p, x);
        for  (X y : relatedToX) {
            if (log.isDebugEnabled()) {
                log.debug("calling build equivs at " + y + " with path ");
                logPath(newPath, Level.DEBUG);
            }
            buildEquivalenceMapping(y,  newPath);
            if (log.isDebugEnabled()) {
                log.debug("Call to build equivs completed at " + y);
                equivalence.logEquivalences(log, Level.DEBUG);
            }
        }
        boolean terminal = true;
        for (X y: relatedToX) {
            if (!equivalence.equivalent(x, y)) {
                terminal = false;
                break;
            }
        }
        if (terminal) {
            terminalElements.add(x);
        }
    }
    
    public boolean removeTerminalElement(X x) {
        return terminalElements.remove(x);
    }

    public void addRelation(X x, X y) {
        if (!terminalElements.contains(x)) {
            return;
        }
        Set<X> candidates = new HashSet<X>(terminalElements);
        candidates.add(y);
        findTerminalElements(candidates);
    }
    
    public void removeRelation(X x, X y) {
        Set<X> candidates = new HashSet<X>(terminalElements);
        candidates.add(x);
        findTerminalElements(candidates);
    }
    
    private void logPath(Path<X> p, Level level) {
        if (log.isEnabledFor(level)) {
            log.log(level, "Path Trace");
            for  (Path<X> point = p; point != null; point = point.getNext()) {
                log.log(level, "Pathelement = " + point.getObject());
            }
        }
    }
    
    private void logLoop(Path<X> p, X x, Level level) {
        if (log.isEnabledFor(level)) {
            log.log(level, "Loop:" );
            log.log(level, "Pathelement = " + x);
            for  (Path<X> point = p; !point.getObject().equals(x); point = point.getNext()) {
                log.log(level, "Pathelement = " + point.getObject());
            }
            log.log(level, "Pathelement = " + x);
        }
    }
}
