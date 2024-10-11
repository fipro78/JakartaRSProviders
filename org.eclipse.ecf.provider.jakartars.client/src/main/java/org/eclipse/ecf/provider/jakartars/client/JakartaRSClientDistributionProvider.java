/*******************************************************************************
* Copyright (c) 2016 - 2024 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
*   Dirk Fauth <dirk.fauth@googlemail.com> - Migration to Jakarta RS
******************************************************************************/
package org.eclipse.ecf.provider.jakartars.client;

import java.util.Map;

import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseFilter;

import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.provider.jakartars.JakartaRSDistributionProvider;

public abstract class JakartaRSClientDistributionProvider extends JakartaRSDistributionProvider {

	protected JakartaRSClientDistributionProvider() {
	}

	protected JakartaRSClientDistributionProvider(String name, IContainerInstantiator instantiator, String description,
			boolean server) {
		super(name, instantiator, description, server);
	}

	protected JakartaRSClientDistributionProvider(String name, IContainerInstantiator instantiator, String description) {
		super(name, instantiator, description);
	}

	protected JakartaRSClientDistributionProvider(String name, IContainerInstantiator instantiator) {
		super(name, instantiator);
	}

	@SuppressWarnings("rawtypes")
	protected void bindClientRequestFilter(ClientRequestFilter instance, Map serviceProps) {
		super.bindJakartaComponent(instance, serviceProps);
	}

	protected void unbindClientRequestFilter(ClientRequestFilter instance) {
		super.removeJakartaComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	protected void bindClientResponseFilter(ClientResponseFilter instance, Map serviceProps) {
		super.bindJakartaComponent(instance, serviceProps);
	}

	protected void unbindClientResponseFilter(ClientResponseFilter instance) {
		super.removeJakartaComponent(instance);
	}

}
