/*******************************************************************************
* Copyright (c) 2018 - 2024 Composent, Inc. and Erdal Karaca. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
*   Dirk Fauth <dirk.fauth@googlemail.com> - Migration to Jakarta RS
******************************************************************************/
package org.eclipse.ecf.provider.jakartars.server;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;

import com.fasterxml.jackson.jakarta.rs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jakarta.rs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

public class JakartaRSServerJacksonFeature implements Feature {

	private final RSARemoteServiceRegistration registration;

	@Deprecated
	public JakartaRSServerJacksonFeature(RSARemoteServiceRegistration reg, int priority) {
		this.registration = reg;
	}

	public JakartaRSServerJacksonFeature(RSARemoteServiceRegistration reg) {
		this.registration = reg;
	}

	@Override
	public boolean configure(final FeatureContext context) {
		if (!context.getConfiguration().isRegistered(JacksonJsonProvider.class)) {
			context.register(JsonParseExceptionMapper.class);
			context.register(JsonMappingExceptionMapper.class);
			context.register(new JakartaRSServerJacksonJaxbJsonProvider(registration));
		}
		return true;
	}
}
