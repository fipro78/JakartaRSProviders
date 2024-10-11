/*******************************************************************************
* Copyright (c) 2015 - 2024 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.internal.jakartars;

import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.BundleStarter;
import org.eclipse.ecf.provider.jakartars.JakartaRSNamespace;
import org.osgi.annotation.bundle.Header;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

@Header(name = Constants.BUNDLE_ACTIVATOR, value = "${@class}")
public class Activator implements BundleActivator {

	private static BundleContext context;
	
	private static final String[] dependencies = { 
			"com.fasterxml.jackson.core.jackson-annotations",
			"com.fasterxml.jackson.core.jackson-core", 
			"com.fasterxml.jackson.core.jackson-databind",
			"jakarta.ws.rs-api" };

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		BundleStarter.startDependents(bundleContext, dependencies, Bundle.RESOLVED | Bundle.STARTING);
		bundleContext.registerService(Namespace.class, new JakartaRSNamespace(), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
