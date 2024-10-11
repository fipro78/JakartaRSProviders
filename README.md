OSGi Remote Services JakartaRS Distribution Providers
===========================

ECF Distribution Providers based upon the JakartaRS specification that supports [OSGi R7 Remote Services](https://osgi.org/specification/osgi.cmpn/7.0.0/service.remoteservices.html).

Implementation based on [Jersey](https://jersey.github.io/) is provided.

This implementation is an adoption of the [OSGi Remote Services JaxRS Distribution Providers](https://github.com/ECF/JaxRSProviders), with the following modifications:

- switch from `javax` to `jakarta` namespace
- changed the project layout from PDE to Bndtools/Maven
- no usage of the OSGi HttpService anymore
- usage of the [Whiteboard Specification for Jakarta™ RESTful Web Services](https://docs.osgi.org/specification/osgi.cmpn/8.1.0/service.jakartars.html)
and the [OSGi Technology Whiteboard Implementation for Jakarta RESTful Web Services](https://github.com/osgi/jakartarest-osgi) reference implementation.
- the `ecf.jakartars.server.pathPrefix` system property and service property are not supported anymore (see below)
- the `org.osgi.service.http.port` system property is not supported anymore (see below)
- the `@Path` annotation on the service interface is not required anymore
- it is not required anymore that a service needs to be an _Immediate Component_ because the services are taken up by the Jakarta Rest Whiteboard
- the bundles are not lazily activated anymore

## Service Deployment

The JaxRS Distribution Provided used the OSGi HttpService in the back to deploy a service as a `Servlet`. The OSGi HttpService was deprecated because of the `javax` namespace usage.

The JakartaRS Distribution Provider in this repository uses the [Whiteboard Specification for Jakarta™ RESTful Web Services](https://docs.osgi.org/specification/osgi.cmpn/8.1.0/service.jakartars.html)
and the [OSGi Technology Whiteboard Implementation for Jakarta RESTful Web Services](https://github.com/osgi/jakartarest-osgi) reference implementation. It basically means that a service that is a JakartaRS Whiteboard Service is at the same time distributed as a Remote Service.

Because of this, the `ecf.jakartars.server.pathPrefix` system property and service property are not supported anymore. They are not known by the JakartaRS Whiteboard, and therefore a deployment with these path prefixes would not work. Therefore the `@Path` annotation on the service interface is not needed anymore. The endpoint is determined by the `jersey.port` property of the `JakartarsWhiteboardComponent` configuration and the `@Path` annotation on the service implementation.

## Limitations

The current implementation is probably not 100% accurate at the moment and therefore comes with the following limitations:
- Only a deployment of Jakarta REST services on a Jetty are supported (`JakartarsWhiteboardComponent`). The usage of the Jakarta Servlet Whiteboard Specification is not yet implemented. The reason is the inspection of the configuration in `JerseyServerDistributionProvider`.
- Starting order  
The implementation currently requires a special starting order to some extend.
    - The bundle that contains the configuration for the `JakartarsWhiteboardComponent` needs to be started early, so the Jetty server and the `JerseyServerDistributionProvider` can take it up at the right time.
    - The bundles that contain the services that should be deployed as REST services and Remote Services need to be started at the end. Otherwise the Remote Service Implementation does not take them up.
- Although the service interface does not need the `@Path` annotation on the interface, the Jakarta RS annotations on the methods are still required to be able to created the proxy calls on the client side.
- On the client side you need to configure the following system properties to make the Jakarta SPI mechanisms work
    - `-Djakarta.ws.rs.client.ClientBuilder=org.glassfish.jersey.client.JerseyClientBuilder`
    - `-Djakarta.ws.rs.ext.RuntimeDelegate=org.glassfish.jersey.internal.RuntimeDelegateImpl`
