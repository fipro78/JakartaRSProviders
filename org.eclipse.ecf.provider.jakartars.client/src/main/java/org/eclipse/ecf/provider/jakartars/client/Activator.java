/*******************************************************************************
* Copyright (c) 2022 - 2024 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jakartars.client;

import org.eclipse.ecf.core.util.BundleStarter;
import org.osgi.annotation.bundle.Header;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

@Header(name = Constants.BUNDLE_ACTIVATOR, value = "${@class}")
public class Activator implements BundleActivator {

	private static final String[] dependencies = { 
			"org.eclipse.ecf.provider.jakartars",
			"com.fasterxml.jackson.jakarta.rs.jackson-jakarta-rs-base",
			"com.fasterxml.jackson.jakarta.rs.jackson-jakarta-rs-json-provider",
			"jakarta.xml.bind-api",
			"org.glassfish.hk2.external.jakarta.inject",
			"org.glassfish.hk2.osgi-resource-locator",
			"org.glassfish.jersey.core.jersey-common",
			"org.glassfish.jersey.media.jersey-media-json-jackson"
			};

	@Override
	public void start(BundleContext context) throws Exception {
		BundleStarter.startDependents(context, dependencies, Bundle.RESOLVED | Bundle.STARTING);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
