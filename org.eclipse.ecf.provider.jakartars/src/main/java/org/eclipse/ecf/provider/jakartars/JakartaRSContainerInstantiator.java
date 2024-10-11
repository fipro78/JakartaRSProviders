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
package org.eclipse.ecf.provider.jakartars;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.ws.rs.core.Configuration;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.URIID;
import org.eclipse.ecf.core.provider.IRemoteServiceContainerInstantiator;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.provider.RemoteServiceContainerInstantiator;

public abstract class JakartaRSContainerInstantiator extends RemoteServiceContainerInstantiator
		implements IRemoteServiceContainerInstantiator {

	public static final String CONFIG_PARAM = "configuration";

	protected static final String[] jakartaIntents = new String[] { "jakartars" };

	public static final String JACKSON_PRIORITY_PROP = "jacksonPriority";

	public static final int JACKSON_DEFAULT_PRIORITY = Integer
			.valueOf(System.getProperty(JakartaRSContainerInstantiator.class.getName() + ".jacksonPriority", String.valueOf(jakarta.ws.rs.Priorities.USER)));

	private JakartaRSDistributionProvider distprovider;

	private JakartaRSNamespace jakartaRSNamespace;

	protected JakartaRSContainerInstantiator(String serverConfigTypeName) {
		this.exporterConfigs.add(serverConfigTypeName);
	}

	protected JakartaRSContainerInstantiator(String serverConfigTypeName, String clientConfigTypeName) {
		this(serverConfigTypeName);
		this.exporterConfigToImporterConfigs.put(serverConfigTypeName,
				Arrays.asList(new String[] { clientConfigTypeName }));
	}

	@Override
	public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters)
			throws ContainerCreateException {
		Configuration configuration = getConfigurationFromParams(description, parameters);
		if (configuration == null)
			configuration = (this.distprovider == null) ? null : this.distprovider.getConfiguration();
		return createInstance(description, parameters, configuration);
	}

	public abstract IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
			Configuration configuration) throws ContainerCreateException;

	protected URIID createJakartaRSID() {
		try {
			return createJakartaRSID(new URI("uuid:" + UUID.randomUUID().toString()));
		} catch (URISyntaxException e) {
			throw new IDCreateException("Could not create random JakartaRSID", e);
		}
	}

	private JakartaRSNamespace getJakartaRSNamespace() {
		synchronized (this) {
			if (this.jakartaRSNamespace == null) {
				this.jakartaRSNamespace = new JakartaRSNamespace();
			}
			return this.jakartaRSNamespace;
		}
	}
	
	protected URIID createJakartaRSID(String uri) {
		return (URIID) getJakartaRSNamespace().createInstance(uri);
	}

	protected URIID createJakartaRSID(URI uri) {
		return (URIID) getJakartaRSNamespace().createInstance(uri);
	}

	protected Configuration getConfigurationFromParams(ContainerTypeDescription description,
			Map<String, ?> parameters) {
		return getParameterValue(parameters, CONFIG_PARAM, Configuration.class, null);
	}

	protected Integer getJacksonPriority(Map<String, ?> parameters) {
		return getParameterValue(parameters, JACKSON_PRIORITY_PROP, Integer.class, JACKSON_DEFAULT_PRIORITY);
	}

	public String[] getSupportedIntents(ContainerTypeDescription description) {
		List<String> results = new ArrayList<String>(Arrays.asList(super.getSupportedIntents(description)));
		results.addAll(Arrays.asList(jakartaIntents));
		results.addAll(Arrays.asList(getJakartaRSComponentIntents()));
		// remove basic intent
		return removeSupportedIntent(Constants.OSGI_BASIC_INTENT,
				(String[]) results.toArray(new String[results.size()]));
	}

	protected String[] getJakartaRSComponentIntents() {
		return this.distprovider.getJakartaRSComponentIntents();
	}
	
	void setDistributionProvider(JakartaRSDistributionProvider jakartaRSDistributionProvider) {
		this.distprovider = jakartaRSDistributionProvider;
	}

}