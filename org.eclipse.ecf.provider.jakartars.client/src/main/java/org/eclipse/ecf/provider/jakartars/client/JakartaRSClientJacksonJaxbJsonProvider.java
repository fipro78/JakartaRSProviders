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
package org.eclipse.ecf.provider.jakartars.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;

import org.eclipse.ecf.provider.jakartars.JakartaRSConstants;
import org.eclipse.ecf.remoteservice.asyncproxy.AsyncReturnUtil;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistration;
import org.eclipse.ecf.remoteservice.util.AsyncUtil;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

public class JakartaRSClientJacksonJaxbJsonProvider extends JacksonJsonProvider {

	private final RemoteServiceClientRegistration reg;
	private final ClassLoader cl;

	public JakartaRSClientJacksonJaxbJsonProvider(RemoteServiceClientRegistration reg, ClassLoader cl) {
		this.reg = reg;
		this.cl = cl;
	}

	@SuppressWarnings("unchecked")
	protected Class<Object> getClass(String clazzName) throws IOException {
		try {
			return (Class<Object>) Class.forName(clazzName, true, this.cl);
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			throw new IOException("Could not load class=" + clazzName + ": " + e.getMessage());
		}
	}

	@Override
	public Object readFrom(Class<Object> returnClass, Type returnType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> headers, InputStream ins) throws IOException {
		// First get the JAKARTARS_RESPHEADER_ASYNC_TYPE from headers.
		// This is set by ServerJacksonJaxbJsonProvider
		String asyncType = headers.getFirst(JakartaRSConstants.JAKARTARS_RESPHEADER_ASYNC_TYPE);
		// Then check that asyncType is not null, we are an OSGi Async service, and
		// that the given returnClass is async type
		boolean isAsync = asyncType != null && AsyncUtil.isOSGIAsync(reg.getReference())
				 && AsyncReturnUtil.isAsyncType(returnClass);
		Class<Object> serReturnClass = returnClass;
		Type serReturnType = returnType;
		if (isAsync) {
			// Make sure that the returnType is ParameterizedType
			if (returnType instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) returnType;
				// Then get Type arguments
				Type[] tas = pt.getActualTypeArguments();
				// The return type is the first one
				if (tas != null && tas.length > 0) {
					serReturnType = tas[0];
					serReturnClass = getClass(serReturnType.getTypeName());
				}
			}
		}
		// Then deserialized using superclass with the updated returnClass and
		// returnType
		Object returnValue = super.readFrom(serReturnClass, serReturnType, annotations, mediaType, headers, ins);
		// Convert to appropriate async type (e.g. CompletableFuture and return)
		return (isAsync && returnValue != null) ? AsyncReturnUtil.convertReturnToAsync(returnValue, returnClass)
				: returnValue;
	}
}
