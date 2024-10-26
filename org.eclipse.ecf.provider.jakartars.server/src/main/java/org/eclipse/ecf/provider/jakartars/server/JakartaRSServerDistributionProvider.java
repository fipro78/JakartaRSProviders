/*******************************************************************************
* Copyright (c) 2015 - 2024 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
*   Dirk Fauth <dirk.fauth@googlemail.com> - Migration to Jakarta RS
******************************************************************************/
package org.eclipse.ecf.provider.jakartars.server;

import java.util.Map;

import jakarta.ws.rs.container.CompletionCallback;
import jakarta.ws.rs.container.ConnectionCallback;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;

import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.provider.jakartars.JakartaRSDistributionProvider;

public abstract class JakartaRSServerDistributionProvider extends JakartaRSDistributionProvider {

	protected JakartaRSServerDistributionProvider() {
		super();
	}

	protected JakartaRSServerDistributionProvider(String name, IContainerInstantiator instantiator) {
		super(name, instantiator);
	}

	protected JakartaRSServerDistributionProvider(String name, IContainerInstantiator instantiator, String description) {
		super(name, instantiator, description);
	}

	protected JakartaRSServerDistributionProvider(String name, IContainerInstantiator instantiator, String description,
			boolean server) {
		super(name, instantiator, description, server);
	}

	@SuppressWarnings("rawtypes")
	protected void bindCompletionCallback(CompletionCallback instance, Map serviceProps) {
		super.bindJakartaComponent(instance, serviceProps);
	}
	
	protected void unbindCompletionCallback(CompletionCallback instance) {
		super.unbindJakartaComponent(instance);
	}
	
	@SuppressWarnings("rawtypes")
	protected void bindConnectionCallback(ConnectionCallback instance, Map serviceProps) {
		super.bindJakartaComponent(instance, serviceProps);
	}
	
	protected void unbindConnectionCallback(ConnectionCallback instance) {
		super.unbindJakartaComponent(instance);
	}
	
	@SuppressWarnings("rawtypes")
	protected void bindContainerRequestFilter(ContainerRequestFilter instance, Map serviceProps) {
		super.bindJakartaComponent(instance, serviceProps);
	}

	protected void unbindContainerRequestFilter(ContainerRequestFilter instance) {
		super.unbindJakartaComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	protected void bindContainerResponseFilter(ContainerResponseFilter instance, Map serviceProps) {
		super.bindJakartaComponent(instance, serviceProps);
	}

	protected void unbindContainerResponseFilter(ContainerResponseFilter instance) {
		super.unbindJakartaComponent(instance);
	}
}
