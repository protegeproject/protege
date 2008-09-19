package org.protege.editor.core.apple;

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
public class AppleApplicationWrapper {


// all this equivalent to this code:
//        Application application = new Application();
//        application.addApplicationListener(new ApplicationAdapter(){
//            public void handleQuit(ApplicationEvent event) {
//                if (handleQuitRequest()){
//                    event.setHandled(true);
//                }
//            }
//        });

    public AppleApplicationWrapper() {
        try {
            Class applicationClass = Class.forName("com.apple.eawt.Application");
            Class applicationListenerClass = Class.forName("com.apple.eawt.ApplicationListener");
            Class applicationEventClass = Class.forName("com.apple.eawt.ApplicationEvent");

            final Method handleQuit = applicationListenerClass.getMethod("handleQuit", applicationEventClass);
            final Method setHandled = applicationEventClass.getMethod("setHandled", boolean.class);
            final Method addApplicationListener = applicationClass.getMethod("addApplicationListener", applicationListenerClass);

            InvocationHandler invocationHandler = new InvocationHandler(){
                public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                    if (method.equals(handleQuit)){
                        if (handleQuitRequest()){
                            Object applicationEvent = objects[0];
                            setHandled.invoke(applicationEvent, true);
                        }
                    }

                    // add further listener methods here

                    return null;
                }
            };

            Object applicationAdapter = Proxy.newProxyInstance(getClass().getClassLoader(),
                                                               new Class[]{applicationListenerClass},
                                                               invocationHandler);


            Object application = applicationClass.newInstance();
            addApplicationListener.invoke(application, applicationAdapter);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * A quit notification has taken place.
     * This gives us a chance to revoke the quit (possibly asking the user).
     * A quitting application should tidy up and return true.
     *
     * @return true if the application can now quit, false if the quit is cancelled.
     */
    protected boolean handleQuitRequest() {
        return true;
    }
}
