/*******************************************************************************
* Copyright (c) 2018 - 2024 Composent, Inc.. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
*   Dirk Fauth <dirk.fauth@googlemail.com> - Migration to Jakarta RS
******************************************************************************/
package org.eclipse.ecf.provider.jakartars.server;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;

import org.eclipse.ecf.provider.jakartars.JakartaRSConstants;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.asyncproxy.AsyncReturnUtil;
import org.eclipse.ecf.remoteservice.util.AsyncUtil;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

public class JakartaRSServerJacksonJaxbJsonProvider extends JacksonJsonProvider {

	private RSARemoteServiceRegistration reg;

	public JakartaRSServerJacksonJaxbJsonProvider(RSARemoteServiceRegistration reg) {
		this.reg = reg;
	}

	@Override
	public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
		Object writeValue = value;
		// By default set the genericClazz String to
		// the genericType.getTypeName
		String genericClazz = genericType.getTypeName();
		// If genericType instanceof ParameterizedType
		if (genericType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) genericType;
			Type ownerType = pt.getRawType();
			genericClazz = ownerType.getTypeName();
		}
		if (AsyncUtil.isOSGIAsync(reg.getReference()) && AsyncReturnUtil.isAsyncType(genericClazz)) {
			if (value != null) {
				// Set the httpHeader to the genericClazz
				httpHeaders.add(JakartaRSConstants.JAKARTARS_RESPHEADER_ASYNC_TYPE, genericClazz);
			}
			genericType = null;
		}
		// Pass to super for serialization with modified type and genericType
		super.writeTo(writeValue, type, genericType, annotations, mediaType, httpHeaders, entityStream);
	}
}
