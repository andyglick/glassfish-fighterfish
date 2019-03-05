/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package org.glassfish.osgi.ee.resources;

import com.sun.enterprise.config.serverbeans.BindableResource;
import com.sun.enterprise.config.serverbeans.Domain;
import com.sun.enterprise.config.serverbeans.ResourceRef;
import com.sun.enterprise.config.serverbeans.Resources;
import org.glassfish.connectors.config.AdminObjectResource;
import org.osgi.framework.BundleContext;

import javax.jms.Queue;
import javax.jms.Topic;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Properties;

/**
 * Resource-Manager to export jms-destinations (JMS-RA admin-object-resources)
 * in GlassFish to OSGi's service-registry
 *
 * @author Jagadish Ramu
 */
public class JMSDestinationResourceManager extends BaseResourceManager
        implements ResourceManager {

    public JMSDestinationResourceManager(Habitat habitat) {
        super(habitat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerResources(BundleContext context) {
        registerJmsResources(context);
    }

    /**
     * registers the admin-object-resource in service-registry
     *
     * @param context bundle-context
     */
    public void registerJmsResources(BundleContext context) {
        Resources resources = getHabitat().getComponent(Domain.class)
                .getResources();
        Collection<AdminObjectResource> administeredObjectResources =
                resources.getResources(AdminObjectResource.class);
        for (AdminObjectResource resource : administeredObjectResources) {
            if (isJmsResource(resource)) {
                ResourceRef resRef = getResourceHelper()
                        .getResourceRef(resource.getJndiName());
                registerResource(resource, resRef, context);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void registerResource(BindableResource resource, ResourceRef resRef,
            BundleContext bundleContext) {
        AdminObjectResource adminObjectResource =
                (AdminObjectResource) resource;
        if (adminObjectResource.getEnabled().equalsIgnoreCase("true")) {
            if (resRef != null && resRef.getEnabled()
                    .equalsIgnoreCase("true")) {
                String defnName = adminObjectResource.getResType();
                Class claz = null;
                Class intf[] = null;

                if (defnName.equals(Constants.QUEUE)) {
                    claz = Queue.class;
                    intf = new Class[]{Queue.class, Invalidate.class};
                } else if (defnName.equals(Constants.TOPIC)) {
                    claz = Topic.class;
                    intf = new Class[]{Topic.class, Invalidate.class};
                } else {
                    throw new RuntimeException(
                            "Invalid Destination [ " + defnName + " ]"
                            + " for jms-resource [ "
                            + resource.getJndiName() + " ]");
                }
                Dictionary properties = new Properties();
                properties.put(Constants.JNDI_NAME, adminObjectResource
                        .getJndiName());
                Object proxy = getProxy(adminObjectResource.getJndiName(), intf,
                        getClassLoader());
                registerResourceAsService(bundleContext, adminObjectResource,
                        claz.getName(), properties, proxy);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handlesResource(BindableResource resource) {
        boolean result = false;
        if (resource instanceof AdminObjectResource) {
            if (isJmsResource((AdminObjectResource) resource)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * determines whether the resource is a JMS-RA's resource
     *
     * @param resource admin-object-resource
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    private boolean isJmsResource(AdminObjectResource resource) {
        boolean result = false;
        String raName = resource.getResAdapter();
        if (raName.equals(Constants.DEFAULT_JMS_ADAPTER)) {
            result = true;
        }
        return result;
    }
}
