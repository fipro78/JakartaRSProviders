/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package com.mycorp.examples.student.remoteservice.host;

import jakarta.ws.rs.Path;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsResource;

import com.mycorp.examples.student.StudentService;

// The OSGi Component Property Type annotation to mark this service as a OSGi Jakarta RS Whiteboard service
@JakartarsResource
// The jakarta-rs path annotation for this service
@Path("/studentservice/rs2")
// The OSGi DS (declarative services) component annotation. 
@Component(
	property = { 
		"service.exported.interfaces=*", 
		"service.exported.intents=osgi.async",
		"service.exported.configs=ecf.jakartars.jersey.server",
		"service.exported.intents=jakartars", 
		"osgi.basic.timeout=1000000" })
public class StudentServiceImpl2 extends AbstractStudentService implements StudentService {

}
