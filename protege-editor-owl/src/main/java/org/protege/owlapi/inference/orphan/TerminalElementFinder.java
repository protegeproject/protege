package org.protege.owlapi.inference.orphan;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsulates a relation, <i>r: X &rarr; X</i>.  Its purpose is to calculate the set of terminal 
 * elements of the transitive closure of the relation r.  We will  define the notion of a terminal
 * element below. 
 * <p>
 * The transitive closure of <i>r</i> is an ordering and we denote it as &le; (e.g. &forall; x&forall; y&isin; r(x). x&le; y).  
 * There is a natural 
 * equivalence relation defined by
 * <center> 
 *       x &asymp; y  &hArr;  x &le; y and y &le; x.
 * </center>
 * An element x in X is said to be terminal if for all y with
 * <center>
 *      y &isin; r(x)
 * </center>
 * we have x &asymp; y.  This is different from the more natural definition that x is terminal if for all y with
 * <center>
 *          x &le; y 
 * </center>
 * we have x &asymp; y.  So for instance in the case where we have 
 * <center>
 *          r(x) = {y}, r(y) = {x, z}
 * </center>
 * x would be terminal by the first definition but not by the second defintion.  Conversations with 
 * users revealed that the second more natural definition of terminal was not what the 
 * user wanted to see.
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
 *       cannot be extended any further it looks at all  <i>x<sub>i</sub> &isin; r(x<sub>n</sub></i>) and generates the 
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
 *       have been found, we know that we have calculated all the nodes that are equivalent to x<sub>i</sub>.
 *       We can therefore apply the following logic:
 *       <ol>
 *          <li> If there are no <i>y &isin; r(x<sub>i</sub>)</i> then <i>x<sub>i</sub></i> is terminal.
 *          <li> If any <i>y &isin; r(x<sub>i</sub>)</i> is in a different equivalence class 
 *               than <i>x<sub>i</sub></i> then <i>x<sub>i</sub></i> is non-terminal.
 *          <li> If all <i>y &isin; r(x<sub>i</sub>)</i> are in the same equivalence class 
 *                as <i>x<sub>i</sub></i> then <i>x<sub>i</sub></i> can be marked as terminal. 
 *       </ol>
 * </ol>
 * @author Timothy Redmond
 *
 */
public class TerminalElementFinder<X extends Comparable<? super X>> {

    private static Logger logger = LoggerFactory.getLogger(TerminalElementFinder.class);

    private Relation<X> r;
    private Set<X> terminalElements = new HashSet<>();
    private EquivalenceRelation<X> equivalence = new EquivalenceRelation<>();
    private Set<X> equivalenceAlreadyCalculated = new HashSet<>();
    
    public TerminalElementFinder(Relation<X> r) {
        this.r = r;
    }
    
    public void findTerminalElements(Set<X> candidates) {
        clear();
        appendTerminalElements(candidates);
        finish();
    }
    
    public void clear() {
        terminalElements = new HashSet<>();
        equivalence.clear();
    }
    
    /*
     * don't call this when the order relation has changed.  This only adds
     * elements to the terminal elements.  Instead form the unison of the current
     * terminal elements and the new candidates and call findTerminalElements again.
     */
    public void appendTerminalElements(Set<X> candidates) {
        equivalenceAlreadyCalculated.clear();
        for (X candidate : candidates) {
            if (logger.isDebugEnabled()) {
                logger.debug("calling build equivs at {} with null path ", candidate);
            }
            buildEquivalenceMapping(candidate, null);
            if (logger.isDebugEnabled()) {
                logger.debug("Call to build equivs completed at {}", candidate);
                equivalence.logEquivalences(logger);
            }
        }
        equivalenceAlreadyCalculated.clear();
    }
        
    public void finish() {
        equivalence.clear();
    }
    
    public Set<X> getTerminalElements() {
        return Collections.unmodifiableSet(terminalElements);
    }
    
    private void buildEquivalenceMapping(X x, Path<X> p) {
        if (equivalenceAlreadyCalculated.contains(x)) {
            return;
        }
        if (p != null && p.contains(x)) {
            equivalence.merge(p.getLoop(x));
            if (logger.isDebugEnabled()) {
                logger.debug("Found loop");
                logLoop(p, x);
            }
            return;
        }
        Collection<X> relatedToX = r.getR(x);
        if (relatedToX == null || relatedToX.isEmpty()) {
            terminalElements.add(x);
            equivalenceAlreadyCalculated.add(x);
            return;
        }
        Path<X> newPath  = new Path<>(p, x);
        for  (X y : relatedToX) {
            if (logger.isDebugEnabled()) {
                logger.debug("calling build equivs at {} with path ", y );
                logPath(newPath);
            }
            buildEquivalenceMapping(y, newPath);
            if (logger.isDebugEnabled()) {
                logger.debug("Call to build equivs completed at {}", y);
                equivalence.logEquivalences(logger);
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
        equivalenceAlreadyCalculated.add(x);
    }
    
    public boolean removeTerminalElement(X x) {
        return terminalElements.remove(x);
    }

    public void addRelation(X x, X y) {
        if (!terminalElements.contains(x)) {
            return;
        }
        Set<X> candidates = new HashSet<>(terminalElements);
        candidates.add(y);
        findTerminalElements(candidates);
    }
    
    public void removeRelation(X x, X y) {
        Set<X> candidates = new HashSet<>(terminalElements);
        candidates.add(x);
        findTerminalElements(candidates);
    }
    
    private void logPath(Path<X> p) {
        if (logger.isDebugEnabled()) {
            logger.debug("Path Trace");
            for  (Path<X> point = p; point != null; point = point.getNext()) {
                logger.debug("Pathelement = {}", point.getObject());
            }
        }
    }
    
    private void logLoop(Path<X> p, X x) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loop:" );
            logger.debug("Pathelement = {}", x);
            for  (Path<X> point = p; !point.getObject().equals(x); point = point.getNext()) {
                logger.debug("Pathelement = {}", point.getObject());
            }
            logger.debug("Pathelement = {}", x);
        }
    }
}
