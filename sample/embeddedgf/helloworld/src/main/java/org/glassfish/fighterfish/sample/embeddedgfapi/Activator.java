/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.glassfish.fighterfish.sample.embeddedgfapi;

import org.glassfish.embeddable.GlassFish;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    @Override
    @SuppressWarnings("unchecked")
    public void start(BundleContext context) throws Exception {
        ServiceTracker st = new ServiceTracker(context,
                GlassFish.class.getName(), null);
        GlassFish gf = (GlassFish) st.waitForService(0);
        System.out.println(gf.getStatus());
        st.close();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
