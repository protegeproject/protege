package org.protege.editor.core.ui.error;

import org.xml.sax.SAXParseException;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Aug 29, 2008<br><br>
 *
 * This class tries to provide a human understandable explanation for a given exception.
 * It does this by a series of explanation factories which can be searched for the most appropriate.
 * It may also search for the most informative cause of the given Throwable object.
 */
@SuppressWarnings("unchecked")
public class ErrorExplainer {

    private Map<Class<? extends Throwable>, ErrorExplanationFactory> factories =
            new HashMap<>();

    public ErrorExplainer() {
        addExplanationFactory(FileNotFoundException.class, new ErrorExplanationFactory<FileNotFoundException>(){
            public <T extends FileNotFoundException> ErrorExplanation<T> createExplanation(T throwable) {
                return new ErrorExplanation<>(throwable, "File not found: " + throwable.getMessage());
            }
        });

        addExplanationFactory(SAXParseException.class, new ErrorExplanationFactory<SAXParseException>(){
            public <T extends SAXParseException> ErrorExplanation<T> createExplanation(T throwable) {
                final String message = "XML error at line " + throwable.getLineNumber() + ", column " +
                                       throwable.getColumnNumber() + "\n" + throwable.getMessage();
                if (throwable.getLineNumber() <= 0 || throwable.getColumnNumber() <= 0){
                    System.out.println("throwable = " + throwable);
                }
                return new ParseErrorExplanation<>(throwable,
                                                    message,
                                                    throwable.getLineNumber()-1, // as they are indexed from 1
                                                    throwable.getColumnNumber()-1);
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

        return new ErrorExplanation<>(throwable, throwable.getMessage()); // return the error itself
    }


    private <T extends Throwable> ErrorExplanationFactory<T> getFactory(T throwable){
        Class<? extends Throwable> cls = getBestMatchJavaClass(throwable.getClass(), factories.keySet());
        if (cls != null){
            return factories.get(cls);
        }
        return null;
    }


    public <T extends Throwable> void addExplanationFactory(Class<T> cls, ErrorExplanationFactory<? super T> fac){
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
    private static <T> Class<? extends T> getBestMatchJavaClass(Class<? extends T> cls, Set<Class<? extends T>> clses){
        Class<? extends T> mostSpecificCls = null;
        for (Class<? extends T> candidate : clses){
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


        public static class ParseErrorExplanation<O extends Throwable> extends ErrorExplanation<O> {

            private int line;

            private int col;


            public ParseErrorExplanation(O cause, String message, int line, int col) {
                super(cause, message);
                this.line = line;
                this.col = col;
            }

            public int getLine(){
                return line;
            }

            public int getColumn(){
                return col;
            }
        }

}
