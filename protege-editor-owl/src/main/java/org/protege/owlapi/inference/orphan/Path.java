package org.protege.owlapi.inference.orphan;

import java.util.ArrayList;
import java.util.List;

public class Path<X> {
    private Path<X> next;
    private X object;

    public Path(X x) {
        this.object = x;
    }
    
    public Path(Path<X> p, X x) {
        next = p;
        object = x;
    }
    
    public Path<X> getNext() {
        return next;
    }

    public X getObject() {
        return object;
    }
    
    public boolean contains(X x) {
        Path<X> point = this;
        do {
            if (point.getObject().equals(x)) {
                return true;
            }
        } while ((point = point.getNext()) != null);
        return false;
    }
    
    public List<X> getLoop(X x) {
        List<X> result = new ArrayList<>();
        result.add(x);
        Path<X> point = this;
        do {
            if (point.getObject().equals(x)) {
                break;
            }
            else {
                result.add(point.getObject());
            }
        } while ((point = point.getNext()) != null);
        return result;
    }
    
    public String toString() {
        String objectVal = object == null ? "null" : object.toString();
        if (next == null) {
            return objectVal;
        }
        return objectVal + " -> " + next.toString();
    }
}
