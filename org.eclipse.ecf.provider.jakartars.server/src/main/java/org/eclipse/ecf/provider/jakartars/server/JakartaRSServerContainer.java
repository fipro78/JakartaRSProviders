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

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.core.identity.URIID;
import org.eclipse.ecf.remoteservice.AbstractRSAContainer;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.osgi.framework.BundleContext;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Configurable;
import jakarta.ws.rs.ext.WriterInterceptor;

public abstract class JakartaRSServerContainer extends AbstractRSAContainer {

	protected static final String SLASH = "/";
	protected final String servletPathPrefix;
	protected BundleContext context;

	protected int jacksonPriority = JakartaRSServerContainerInstantiator.JACKSON_DEFAULT_PRIORITY;

	protected boolean includeRemoteServiceId;
	
	protected String configType;

	public JakartaRSServerContainer(String configType, URIID containerID, BundleContext context, int jacksonPriority,
			boolean includeRemoteServiceId) {
		super(containerID);
		this.configType = configType;
		this.context = context;
		String path = getURI().getPath();
		this.servletPathPrefix = (path == null) ? SLASH : path;
		this.jacksonPriority = jacksonPriority;
		this.includeRemoteServiceId = includeRemoteServiceId;
	}

	protected URI getURI() {
		return ((URIID) getID()).toURI();
	}

	protected String getUrlContext() {
		URI u = getURI();
		return u.getScheme() + "://" + u.getHost() + ":" + u.getPort();
	}

	protected void registerService(Configurable<?> configurable, RSARemoteServiceRegistration registration) {
		configurable.register(registration.getService());
	}

	protected void registerExtensions(Configurable<?> configurable, final RSARemoteServiceRegistration registration) {
		configurable.register(new JakartaRSServerWriterInterceptor(registration), WriterInterceptor.class);
	}

	@SuppressWarnings("rawtypes")
	protected abstract Configurable createConfigurable(RSARemoteServiceRegistration registration);

	protected String trimLeadingSlashes(String input) {
		while (!"/".equals(input) && input.startsWith(SLASH+SLASH) && input.length() > 1) {
			input = input.substring(1);
		}
		return input;
	}
	
	protected String trimTrailingSlashes(String input) {
		while (!"/".equals(input) && input.endsWith("/") && input.length() > 1) {
			input = input.substring(0, input.length() - 1);
		}
		return input;
	}
	
	protected String trimLeadingAndTrailingSlashes(String input) {
		return trimLeadingSlashes(trimTrailingSlashes(input));
	}
	
	protected String getContainerPathPrefix() {
		String servletAliasPrefix = (this.servletPathPrefix == null || "".equals(this.servletPathPrefix)) ? SLASH
				: this.servletPathPrefix;
		if (!servletAliasPrefix.endsWith(SLASH)) {
			servletAliasPrefix += "/";
		}
		return trimLeadingAndTrailingSlashes(servletAliasPrefix);
	}
	
	protected String getRegistrationPathPrefix(RSARemoteServiceRegistration reg) {
		// get path from service class
		String regPathPrefix = "";
		Annotation[] declaredAnnotations = reg.getService().getClass().getDeclaredAnnotations();
		for (Annotation a : declaredAnnotations) {
			if (a.annotationType().equals(Path.class)) {
				regPathPrefix = ((Path)a).value();
			}
		}
		
		return trimLeadingAndTrailingSlashes(regPathPrefix);
	}
	
	protected String getServletAlias(RSARemoteServiceRegistration reg) {
		String alias = getContainerPathPrefix()
				+ (this.includeRemoteServiceId ? String.valueOf(reg.getServiceId()) : getRegistrationPathPrefix(reg));
		return trimLeadingAndTrailingSlashes(alias);
	}

	@Override
	protected Map<String, Object> exportRemoteService(RSARemoteServiceRegistration reg) {
		String servletAlias = getServletAlias(reg);
		return createExtraExportProperties(servletAlias, reg);
	}

	protected String getExportedEndpointId(String servletAlias, RSARemoteServiceRegistration reg) {
		URI ourURI = getURI();
		String path = ourURI.getPath();
		// Fix for https://github.com/ECF/JaxRSProviders/issues/10
		String suffix = "";
		// If there is some path then
		if (!"".equals(path) && servletAlias.startsWith(path)) {
			// Then if the servletAlias starts with, then we remove
			suffix = servletAlias.substring(path.length());
		} else
			// otherwise we use the servletAlias unmodified
			suffix = servletAlias;
		return ourURI.toString() + suffix;
	}

	protected Map<String, Object> createExtraExportProperties(String servletAlias, RSARemoteServiceRegistration reg) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put(Constants.ENDPOINT_ID, getExportedEndpointId(servletAlias, reg));
		return result;
	}

	protected String getPackageName(Object serviceObject) {
		String className = serviceObject.getClass().getName();
		int lastDot = className.lastIndexOf(".");
		return (lastDot > 0) ? className.substring(0, lastDot) : null;
	}
}
