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

To reload the routes you can hit /actuator/gateway/refresh

### Predicate and Filter Factories

The real power of the Spring Gateway comes into play when we want to write custom logic that will be applied against all the service calls flowing through the gateway. Most often, we’ll use this custom logic to enforce a consistent set of application policies like security, logging, and tracking among all services.

These application policies are considered **cross-cutting concerns** because we want these strategies to be applied to all the services in our application without having to modify each one to implement them.

In this fashion, the Spring Cloud Gateway Predicate and Filter Factories can be used similarly to Spring aspect classes.

While a servlet filter or Spring aspect is localized to a specific service, using the Gateway and its Predicate and Filter Factories allows us to implement cross-cutting concerns across all the services being routed through the gateway.

Built-in predicates are objects that allow us to check if the requests fulfill a set of conditions before executing or processing the requests. For each route, we can set multi- ple Predicate Factories, which are used and combined via the logical AND.

The built-in Filter Factories allow us to inject policy enforcement points in our code and perform a wide number of actions on all service calls in a consistent fashion. In other words, these filters let us modify the incoming and outgoing HTTP requests and responses. 

The ability to proxy all requests through the gateway lets us simplify our service invocations. But the real power of Spring Cloud Gateway comes into play when we want to write **custom logic** that can be applied against all the service calls flowing through the gateway. Most often, this custom logic is used to enforce a consistent set of application policies like security, logging, and tracking among all the services.

A pre-filter is invoked before the actual request is sent to the target destination. A post-filter is invoked after the target service, and a response is sent back to the client.

Any pre-filters defined in the gateway are invoked as a request enters the gateway. The pre-filters inspect and modify an HTTP request before it gets to the actual service. A pre-filter, however, cannot redirect the user to a different endpoint or service. After the pre-filters are executed against the incoming request by the gateway, the gateway determines the destination (where the service is heading). After the target service is invoked, the gateway post-filters are invoked. The post-filters inspect and modify the response from the invoked service.

We will build 2 filters:

1. A TRACKING FILTER, is a pre-filter that ensures that every request flowing from the gateway has a **correlation ID** associated with it. A correlation ID is a unique ID that gets carried across all the microservices that are executed when carrying out a customer request. A correlation ID allows us to trace the chain of events that occur as a call goes through a series of microservice calls.

2. The target service can either be an organization or the licensing service. Both services receive the correlation ID in the HTTP request header.

3. The RESPONSE FILTER is a post-filter that injects the correlation ID associated with the service call into the HTTP response header sent to the client. This way, the client will have access to the correlation ID associated with the request.

