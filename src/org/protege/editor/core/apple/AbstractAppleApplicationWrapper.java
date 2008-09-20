package org.protege.editor.core.apple;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeManager;

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
    private static Logger log = Logger.getLogger(AbstractAppleApplicationWrapper.class);

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
    private Method removeApplicationListenerMethod;
    
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
            log.error("Could not initialize OS X specific settings");
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
        removeApplicationListenerMethod = applicationClass.getMethod("removeApplicationListener", new Class[] { applicationListenerClass });

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
