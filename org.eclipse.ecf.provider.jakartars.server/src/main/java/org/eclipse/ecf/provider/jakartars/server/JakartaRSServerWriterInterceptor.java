/*******************************************************************************
* Copyright (c) 2020 - 2024 Composent, Inc. and others. All rights reserved. This
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import org.eclipse.ecf.provider.jakartars.JakartaRSConstants;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.asyncproxy.AsyncReturnUtil;
import org.eclipse.ecf.remoteservice.util.AsyncUtil;

public class JakartaRSServerWriterInterceptor implements WriterInterceptor {

	private final RSARemoteServiceRegistration registration;
	
	public JakartaRSServerWriterInterceptor(RSARemoteServiceRegistration reg) {
		this.registration = reg;
	}
	
	@Override
	public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
		Object value = context.getEntity();
		// By default set the genericClazz String to
		// the genericType.getTypeName
		Type genericType = context.getGenericType();
		String genericClazz = genericType.getTypeName();
		// If genericType instanceof ParameterizedType
		if (genericType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) genericType;
			Type ownerType = pt.getRawType();
			genericClazz = ownerType.getTypeName();
		}
		MultivaluedMap<String,Object> httpHeaders = context.getHeaders();
		if (AsyncUtil.isOSGIAsync(registration.getReference()) && AsyncReturnUtil.isAsyncType(genericClazz)) {
			if (value != null) {
				// Set the httpHeader to the genericClazz
				httpHeaders.add(JakartaRSConstants.JAKARTARS_RESPHEADER_ASYNC_TYPE, genericClazz);
			}
			// this is what changes the subsequent processing
			context.setGenericType(null);
		}
		context.proceed();
	}

}
