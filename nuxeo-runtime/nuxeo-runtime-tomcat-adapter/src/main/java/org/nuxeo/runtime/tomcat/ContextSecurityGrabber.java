/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     matic
 */
package org.nuxeo.runtime.tomcat;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.NamingContextListener;
import org.apache.catalina.core.StandardContext;
import org.apache.naming.ContextAccessController;

/**
 * Grab security token and source context for setting write access onto naming context during container startup.
 *
 * @author matic
 * @since 5.5
 */
public class ContextSecurityGrabber implements LifecycleListener {

    final NamingContextListener namingContextListener = new NamingContextListener();

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        final String type = event.getType();
        final StandardContext source = (StandardContext) event.getSource();
        if (source.getNamingContextListener() == null) {
            namingContextListener.setName(source.getName());
            source.setNamingContextListener(namingContextListener);
        }
        namingContextListener.lifecycleEvent(event);
        if (Lifecycle.CONFIGURE_START_EVENT.equals(type)) {
            final Object token = event.getLifecycle();
            ContextAccessController.setWritable(namingContextListener.getName(), token);
        }
    }

}
