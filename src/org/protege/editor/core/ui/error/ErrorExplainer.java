package org.protege.editor.core.ui.error;

import org.xml.sax.SAXParseException;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Aug 29, 2008<br><br>
 *
 * This class tries to provide a human understandable explanation for a given exception.
 * It does this by a series of explanation factories which can be searched for the most appropriate.
 * It may also search for the most informative cause of the given Throwable object.
 */
public class ErrorExplainer {

    private Map<Class<? extends Throwable>, ErrorExplanationFactory> factories =
            new HashMap<Class<? extends Throwable>, ErrorExplanationFactory>();

    public ErrorExplainer() {
        addExplanationFactory(FileNotFoundException.class, new ErrorExplanationFactory<FileNotFoundException>(){
            public <T extends FileNotFoundException> ErrorExplanation<T> createExplanation(T throwable) {
                return new ErrorExplanation<T>(throwable, "File not found: " + throwable.getMessage());
            }
        });
        addExplanationFactory(SAXParseException.class, new ErrorExplanationFactory<SAXParseException>(){
            public <T extends SAXParseException> ErrorExplanation<T> createExplanation(T throwable) {
                return new ErrorExplanation<T>(throwable, "XML error at line " + throwable.getLineNumber() + ", column " + throwable.getColumnNumber() + "\n" + throwable.getMessage());
            }
        });
    }


    public <T extends Throwable> ErrorExplanation<? extends Throwable> getErrorExplanation(T throwable, boolean traverseCauses){
        Throwable cause = throwable;
        // Works through an error and its causes trying to find an explainer to match
        do {
            // This factory is not going to be even vaguely related to the type of the original exception
            // if we have moved on to its causes
            // The explanation only cares about the cause - it forgets the original exception
            // so we can return an explanation for any throwable cause
            ErrorExplanationFactory fac = getFactory(cause);
            if (fac != null){
                return fac.createExplanation(cause);
            }
            cause = cause.getCause();
        }while (traverseCauses && cause != null);

        return new ErrorExplanation<T>(throwable, throwable.getMessage()); // return the error itself
    }


    private <T extends Throwable> ErrorExplanationFactory<T> getFactory(T throwable){
        Class<? extends Throwable> cls = getBestMatchJavaClass(throwable.getClass(), factories.keySet());
        if (cls != null){
            return factories.get(cls);
        }
        return null;
    }


    public <T extends Throwable> void addExplanationFactory(Class<T> cls, ErrorExplanationFactory<T> fac){
        factories.put(cls, fac);
    }


    public void clearExplanationFactories(){
        factories.clear();
    }


    /**
     * Tries to find if the given class or a most specific superclass is contained in the given set
     * @param cls the class to test against
     * @param clses the set of classes to check
     * @return cls if it exists in clses else
     *         one of the most specific superclasses of cls if contained in clses else
     *         null if neither cls or any of its superclasses appear in clses
     */
    private static <T> Class<? extends T> getBestMatchJavaClass(Class cls, Set<Class<? extends T>> clses){
        Class<? extends T> mostSpecificCls = null;
        for (Class candidate : clses){
            if (candidate.isAssignableFrom(cls)){
                if (mostSpecificCls == null || mostSpecificCls.isAssignableFrom(candidate)){
                    mostSpecificCls = candidate;
                }
            }
        }
        return mostSpecificCls;
    }


    /**
     * The factory can be implemented in any way the coder wishes. It might be sensible to parse the
     * message from the exception itself (for example, to pull out a line number) or it can use
     * contingent knowledge to help make the exception more understandable
     */
    public static interface ErrorExplanationFactory<O extends Throwable>{

        // can generate explanations for any subclass of O
        public <T extends O> ErrorExplanation<T> createExplanation(T throwable);
    }


    public static class ErrorExplanation<O extends Throwable> {

        private O cause;

        private String message;


        public ErrorExplanation(O cause, String message) {
            this.cause = cause;
            this.message = message;
        }


        public O getCause() {
            return cause;
        }


        public String getMessage() {
            return message;
        }
    }



//    public static void main(String[] args) {
//        Set<Class<? extends Collection>> clses = new HashSet<Class<? extends Collection>>();
//        clses.add(ArrayList.class);
//        clses.add(List.class);
//        clses.add(HashSet.class);
//
//        System.out.println(ErrorExplainer.getBestMatchJavaClass(Collection.class, clses));
//
//        System.out.println(ErrorExplainer.getBestMatchJavaClass(TestList.class, clses));
//        System.out.println(ErrorExplainer.getBestMatchJavaClass(ArrayList.class, clses));
//
//        System.out.println(ErrorExplainer.getBestMatchJavaClass(LinkedHashSet.class, clses));
//        clses.add(LinkedHashSet.class);
//        System.out.println(ErrorExplainer.getBestMatchJavaClass(LinkedHashSet.class, clses));
//
//        ErrorExplainer explainer = new ErrorExplainer();
//        System.out.println(explainer.getErrorExplanation(new FileNotFoundException("monkey.owl"), true).getMessage());
//        System.out.println(explainer.getErrorExplanation(new IOException("IO Exception here handled by default"), true).getMessage());
//
//        explainer.clearExplanationFactories();
//        explainer.addExplanationFactory(IOException.class, new ErrorExplanationFactory<IOException>(){
//            public <T extends IOException> ErrorExplanation<T> createExplanation(T throwable) {
//                return new ErrorExplanation<T>(throwable, "IO Exception: " + throwable.getMessage());
//            }
//        });
//        System.out.println(explainer.getErrorExplanation(new FileNotFoundException("monkey.owl handle by IO exception "), true).getMessage());
//        System.out.println(explainer.getErrorExplanation(new IOException("IO Exception here"), true).getMessage());
//
//    }
//
//    public class TestList extends ArrayList{
//    }

}
