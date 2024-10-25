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

import java.net.URI;
import java.util.Map;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.jakartars.server.JakartaRSServerContainerInstantiator;
import org.eclipse.ecf.provider.jakartars.server.JakartaRSServerDistributionProvider;
import org.eclipse.ecf.remoteservice.provider.IRemoteServiceDistributionProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import jakarta.ws.rs.container.CompletionCallback;
import jakarta.ws.rs.container.ConnectionCallback;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Configurable;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.WriterInterceptor;

// TODO ecf configuration with implementations that work with jetty and servlet whiteboard

@Component(
	service = IRemoteServiceDistributionProvider.class,
	configurationPid="JakartarsWhiteboardComponent",
	configurationPolicy=ConfigurationPolicy.REQUIRE)
public class JerseyServerDistributionProvider 
		extends JakartaRSServerDistributionProvider
		implements IRemoteServiceDistributionProvider {

	public static final String JERSEY_SERVER_CONFIG = "ecf.jakartars.jersey.server";
	public static final String BINDING_PRIORITY = "bindingPriority";
	public static final String JACKSON_PRIORITY = "jacksonPriority";
	
	public JerseyServerDistributionProvider() {
		this(JERSEY_SERVER_CONFIG, "Jersey Jakarta-RS Server Distribution Provider");
	}

	protected JerseyServerDistributionProvider(String serverConfigType, String description) {
		super();
		setName(serverConfigType);
		setDescription(description);
		setServer(true);
	}

	protected boolean supportsOSGIConfidentialIntent() {
		return true;
	}

	protected boolean supportsOSGIPrivateIntent() {
		return true;
	}

	protected boolean supportsOSGIAsyncIntent() {
		return true;
	}

	@Activate
	protected void activate(BundleContext context, Map<String, Object> properties) {
		setInstantiator(new JakartaRSServerContainerInstantiator(getName()) {
			@Override
			public IContainer createInstance(
					ContainerTypeDescription description, 
					Map<String, ?> parameters,
					Configuration configuration) throws ContainerCreateException {
				
				URI uri = getUri(parameters, getName());
				checkOSGIIntents(description, uri, parameters);
				return new JerseyServerContainer(
						description.getName(), 
						createJakartaRSID(uri), 
						context,
						(ResourceConfig) configuration, 
						getJacksonPriority(parameters),
						getParameterValue(parameters, BINDING_PRIORITY, Integer.class, JerseyServerContainer.BINDING_DEFAULT_PRIORITY),
						getIncludeRemoteServiceId(parameters, getName()));
			}

			@Override
			protected String getSystemPort(Map<String, ?> params, String configName, boolean https) {
				Object jerseyPort = properties.get("jersey.port");
				if (jerseyPort != null) {
					return jerseyPort.toString();
				}
				return super.getSystemPort(params, configName, https);
			}
			
			@Override
			protected String getSystemPathPrefix(Map<String, ?> params, String configName) {
				Object contextPath = properties.get("jersey.context.path");
				if (contextPath != null) {
					String result = contextPath.toString();
					if (!result.startsWith("/")) {
						result = "/" + result;
					}
					return result;
				}
				return super.getSystemPathPrefix(params, configName);
			}
			
			@Override
			protected boolean supportsOSGIConfidentialIntent(ContainerTypeDescription description) {
				return JerseyServerDistributionProvider.this.supportsOSGIConfidentialIntent();
			}

			@Override
			protected boolean supportsOSGIPrivateIntent(ContainerTypeDescription description) {
				return JerseyServerDistributionProvider.this.supportsOSGIConfidentialIntent();
			}

			@Override
			protected boolean supportsOSGIAsyncIntent(ContainerTypeDescription description) {
				return JerseyServerDistributionProvider.this.supportsOSGIAsyncIntent();
			}
		});
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Configurable createConfigurable() {
		return new ResourceConfig();
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE,policyOption = ReferencePolicyOption.GREEDY)
	protected void bindMessageBodyWriter(MessageBodyWriter instance, Map serviceProps) {
		super.bindMessageBodyWriter(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindMessageBodyWriter(MessageBodyWriter instance) {
		super.unbindMessageBodyWriter(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE,policyOption = ReferencePolicyOption.GREEDY)
	protected void bindMessageBodyReader(MessageBodyReader instance, Map serviceProps) {
		super.bindMessageBodyReader(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindMessageBodyReader(MessageBodyReader instance) {
		super.unbindMessageBodyReader(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE,policyOption = ReferencePolicyOption.GREEDY)
	protected void bindContextResolver(ContextResolver instance, Map serviceProps) {
		super.bindContextResolver(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindContextResolver(ContextResolver instance) {
		super.unbindContextResolver(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE,policyOption = ReferencePolicyOption.GREEDY)
	protected void bindExceptionMapper(ExceptionMapper instance, Map serviceProps) {
		super.bindExceptionMapper(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindExceptionMapper(ExceptionMapper instance) {
		super.unbindExceptionMapper(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE,policyOption = ReferencePolicyOption.GREEDY)
	protected void bindFeature(Feature instance, Map serviceProps) {
		super.bindFeature(instance, serviceProps);
	}

	protected void unbindFeature(Feature instance) {
		super.unbindFeature(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE,policyOption = ReferencePolicyOption.GREEDY)
	protected void bindReaderInterceptor(ReaderInterceptor instance, Map serviceProps) {
		super.bindReaderInterceptor(instance, serviceProps);
	}

	protected void unbindReaderInterceptor(ReaderInterceptor instance) {
		super.unbindReaderInterceptor(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE,policyOption = ReferencePolicyOption.GREEDY)
	protected void bindWriterInterceptor(WriterInterceptor instance, Map serviceProps) {
		super.bindWriterInterceptor(instance, serviceProps);
	}

	protected void unbindWriterInterceptor(WriterInterceptor instance) {
		super.unbindWriterInterceptor(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE,policyOption = ReferencePolicyOption.GREEDY)
	protected void bindCompletionCallback(CompletionCallback instance, Map serviceProps) {
		super.bindCompletionCallback(instance, serviceProps);
	}

	protected void unbindCompletionCallback(CompletionCallback instance) {
		super.unbindCompletionCallback(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE,policyOption = ReferencePolicyOption.GREEDY)
	protected void bindConnectionCallback(ConnectionCallback instance, Map serviceProps) {
		super.bindConnectionCallback(instance, serviceProps);
	}

	protected void unbindConnectionCallback(ConnectionCallback instance) {
		super.unbindConnectionCallback(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE,policyOption = ReferencePolicyOption.GREEDY)
	protected void bindContainerRequestFilter(ContainerRequestFilter instance, Map serviceProps) {
		super.bindContainerRequestFilter(instance, serviceProps);
	}

	protected void unbindContainerRequestFilter(ContainerRequestFilter instance) {
		super.unbindContainerRequestFilter(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE,policyOption = ReferencePolicyOption.GREEDY)
	protected void bindContainerResponseFilter(ContainerResponseFilter instance, Map serviceProps) {
		super.bindContainerResponseFilter(instance, serviceProps);
	}

	protected void unbindContainerResponseFilter(ContainerResponseFilter instance) {
		super.unbindContainerResponseFilter(instance);
	}

}
