package com.mycorp.examples.student.remoteservice.host;

import org.eclipse.ecf.osgi.services.remoteserviceadmin.DebugRemoteServiceAdminListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

@Component
public class DebugListener
    extends DebugRemoteServiceAdminListener
    implements RemoteServiceAdminListener {
	// register the DebugRemoteServiceAdminListener via DS
}