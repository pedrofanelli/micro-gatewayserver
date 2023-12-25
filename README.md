# API Gateway with Spring Cloud

At its heart, the Spring Cloud Gateway is a reverse proxy. A reverse proxy is an intermediate server that sits between the client trying to reach a resource and the resource 
itself. The client has no idea it’s even communicating with a server. The reverse proxy takes care of capturing the client’s request and then calls the remote resource on the 
client’s behalf.


In the case of a microservice architecture, Spring Cloud Gateway (our reverse proxy) takes a microservice call from a client and forwards it to the upstream service. The service client thinks it’s only communicating with the gateway. But it is not actually as simple as that. To communicate with the upstream services, the gateway has to know how to MAP the incoming call to the upstream route. The Spring Cloud Gateway has several mechanisms to do this, including:

1. Automated mapping of routes using service discovery

2. Manual mapping of routes using service discovery

1. 
For Automated we need the next config:

`spring.cloud.gateway.discovery.locator.enabled=true`

`spring.cloud.gateway.discovery.locator.lower-case-service-id=true`

Then we can see the info in: localhost:8072/actuator/gateway/routes

2.
For the Manual we set every single endpoint manually, check gateway-server.yml in the config server. In particular the URI, which must be the SAME as the actual service one. To check the routes: localhost:8072/actuator/gateway/routes

With the manual changes we can hit our services without using the actual Eureka ID, but using a custom one.

So we forget of hitting the actual URL of each microservice, we only hit this Gateway Server with a custom URL that is not the real one, and then the gateway takes care of everything.

What's next is the ability to add functionality in the middle, like a proxy.


