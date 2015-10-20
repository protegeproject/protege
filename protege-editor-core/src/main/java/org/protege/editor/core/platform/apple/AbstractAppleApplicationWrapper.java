package org.protege.editor.core.platform.apple;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 19, 2008<br><br>
 *
 * A wrapper for the apple Application class and its event handling.
 * This uses reflection to handle the events such that it can be compiled/run on any system.
 *
 */
public abstract class AbstractAppleApplicationWrapper {

    private Logger logger = LoggerFactory.getLogger(AbstractAppleApplicationWrapper.class);

    private Object application;
    private Object listener;
    
    /*
     * OS X specific classes
     */
    private Class<?> applicationClass;
    private Class<?> applicationListenerClass;
    private Class<?> applicationEventClass;
    
    /*
     * Application Methods
     */
    
    private Method getApplicationMethod;
    private Method addApplicationListenerMethod;    
    /*
     * Application Listener Methods
     */

    private Method handleAboutMethod;
    private Method handleOpenFileMethod;
    private Method handlePreferencesMethod;
    private Method handleQuitMethod;
    /*
     * Application Event Methods
     */

    private Method getFileNameMethod;
    private Method setHandledMethod;



    public AbstractAppleApplicationWrapper() {
        try {
            getClassesAndInterfaces();
            
            application = getApplicationMethod.invoke(null, new Object [] { });

            addApplicationListenerMethod.invoke(application, getListener());
        }
        catch (Exception e) {
            logger.error("An error occurred during OS X settings initialization.", e);
        }
    }
    
    private void getClassesAndInterfaces() throws ClassNotFoundException, SecurityException, NoSuchMethodException {
        /*
         * OS X specific classes
         */
        applicationClass = Class.forName("com.apple.eawt.Application");
        applicationListenerClass = Class.forName("com.apple.eawt.ApplicationListener");
        applicationEventClass = Class.forName("com.apple.eawt.ApplicationEvent");
        
        /*
         * Application Methods
         */
        
        getApplicationMethod = applicationClass.getMethod("getApplication", new Class[] { });
        addApplicationListenerMethod = applicationClass.getMethod("addApplicationListener", applicationListenerClass);
        addApplicationListenerMethod = applicationClass.getMethod("addApplicationListener", new Class[] { applicationListenerClass });
        /*
         * Application Listener Methods
         */


        handleAboutMethod = applicationListenerClass.getMethod("handleAbout", applicationEventClass);
        handleOpenFileMethod = applicationListenerClass.getMethod("handleOpenFile", new Class[] { applicationEventClass });
        handlePreferencesMethod = applicationListenerClass.getMethod("handlePreferences", applicationEventClass);
        handleQuitMethod = applicationListenerClass.getMethod("handleQuit", applicationEventClass);
        

        /*
         * Application Event Methods
         */
        getFileNameMethod = applicationEventClass.getMethod("getFilename", new Class[] { });
        setHandledMethod = applicationEventClass.getMethod("setHandled", boolean.class);
    }
    
    
    
    private Object getListener() {
        if (listener != null) {
            return listener;
        }
        InvocationHandler handler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
                if (method.equals(handleAboutMethod)){
                    Object applicationEvent = args[0];
                    setHandledMethod.invoke(applicationEvent, handleAboutRequest());
                }
                if (method.equals(handleOpenFileMethod)) {
                    Object event = args[0];
                    String fileName = (String) getFileNameMethod.invoke(event, new Object[] { });
                    editFile(fileName);
                }
                else if (method.equals(handlePreferencesMethod)){
                    Object applicationEvent = args[0];
                    setHandledMethod.invoke(applicationEvent, handlePreferencesRequest());
                }
                else if (method.equals(handleQuitMethod)){
                    Object applicationEvent = args[0];
                    setHandledMethod.invoke(applicationEvent, handleQuitRequest());
                }
                // behave like a good java object
                else if (method.getName().equals("equals")) {
                    return proxy == args[0];
                }
                else if (method.getName().equals("hashCode")) {
                    return 42;
                }
                else if (method.getName().equals("toString")) {
                    return "OSX Application Listener";
                }
                return null;
            }
        };
        listener = Proxy.newProxyInstance(getClass().getClassLoader(), 
                                          new Class[] { applicationListenerClass }, handler);
        return listener;
    }
    
    /*
     * Protege Interfaces
     */

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected final void setEnabledPreferencesMenu(boolean enabled) {
        try {
            Class applicationClass = Class.forName("com.apple.eawt.Application");
            final Method setEnabledPreferencesMenu = applicationClass.getMethod("setEnabledPreferencesMenu", boolean.class);
            setEnabledPreferencesMenu.invoke(application, enabled);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /*
     * Abstract methods for interaction with Protege
     */
    
    protected abstract void editFile(String fileName) throws Exception;

    protected abstract boolean handlePreferencesRequest();


    protected abstract boolean handleAboutRequest();


    /**
     * A quit notification has taken place.
     * This gives us a chance to revoke the quit (possibly asking the user).
     * A quitting application should tidy up and return true.
     *
     * @return true if the application can now quit, false if the quit is cancelled.
     */
    protected abstract boolean handleQuitRequest();
}
