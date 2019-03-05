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
package org.glassfish.osgiejb;

import org.glassfish.osgijavaeebase.Extender;
import org.osgi.framework.BundleContext;

/**
 * An extender that registers a deployer capable of deploying/undeploying
 * OSGi/EJB bundles.
 *
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class EJBExtender implements Extender {

    private final BundleContext context;
    private OSGiEJBDeployer deployer;

    public EJBExtender(BundleContext context) {
        this.context = context;
    }

    @Override
    public synchronized void start() {
        deployer = new OSGiEJBDeployer(context);
        deployer.register();
    }

    @Override
    public synchronized void stop() {
        if (deployer != null) {
            deployer.unregister();
            deployer = null;
        }
    }
}
