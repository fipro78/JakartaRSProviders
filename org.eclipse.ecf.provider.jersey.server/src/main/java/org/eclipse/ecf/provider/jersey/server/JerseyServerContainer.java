/*******************************************************************************
* Copyright (c) 2018 - 2024 Composent, Inc. and Erdal Karaca. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
*   Erdal Karaca - initial API and implementation
*   Dirk Fauth <dirk.fauth@googlemail.com> - Migration to Jakarta RS
******************************************************************************/
package org.eclipse.ecf.provider.jersey.server;

import java.lang.reflect.InvocationHandler;

import jakarta.ws.rs.core.Configurable;

import org.eclipse.ecf.core.identity.URIID;
import org.eclipse.ecf.provider.jakartars.server.JakartaRSServerContainer;
import org.eclipse.ecf.provider.jakartars.server.JakartaRSServerInvocationHandlerProvider;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Invocable;
import org.glassfish.jersey.server.spi.internal.ResourceMethodInvocationHandlerProvider;
import org.osgi.framework.BundleContext;

public class JerseyServerContainer extends JakartaRSServerContainer {

	public static final int BINDING_DEFAULT_PRIORITY = 
			Integer.valueOf(
					System.getProperty(
							JakartaRSServerContainer.class.getName() + ".bindingPriority", 
							String.valueOf(jakarta.ws.rs.Priorities.USER)));

	private ResourceConfig originalConfiguration;
	private int bindingPriority = BINDING_DEFAULT_PRIORITY;

	public JerseyServerContainer(
			String configType, 
			URIID containerID, 
			BundleContext context,
			ResourceConfig configuration, 
			int jacksonPriority, 
			int bindingPriority, 
			boolean includeRemoteServiceId) {
		
		super(configType, containerID, context, jacksonPriority, includeRemoteServiceId);
		this.originalConfiguration = configuration;
		this.bindingPriority = bindingPriority;
	}

	public class JerseyBinder extends AbstractBinder {

		class JerseyResourceMethodInvocationHandlerProvider 
				extends JakartaRSServerInvocationHandlerProvider
				implements ResourceMethodInvocationHandlerProvider {
			
			public JerseyResourceMethodInvocationHandlerProvider(RSARemoteServiceRegistration registration) {
				super(registration);
			}

			@Override
			public InvocationHandler create(Invocable invocable) {
				return createInvocationHandler(invocable.getHandlingMethod());
			}
		}

		private JerseyResourceMethodInvocationHandlerProvider provider;

		public JerseyBinder(RSARemoteServiceRegistration registration) {
			this.provider = new JerseyResourceMethodInvocationHandlerProvider(registration);
		}

		@Override
		protected void configure() {
			bind(provider).to(ResourceMethodInvocationHandlerProvider.class);
		}
	}

	@Override
	protected Configurable<?> createConfigurable(RSARemoteServiceRegistration registration) {
		return new ResourceConfig(this.originalConfiguration);
	}

	@Override
	protected void registerExtensions(Configurable<?> configurable, RSARemoteServiceRegistration registration) {
		super.registerExtensions(configurable, registration);
		configurable.register(new JerseyBinder(registration), bindingPriority);
	}

	@Override
	protected void unexportRemoteService(RSARemoteServiceRegistration registration) {
		// do nothing 
	}

}