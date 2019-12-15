## Load Balancing with Zuul

### Netflix Zuul
As an edge service, Zuul provides a lot of different functions. The main pieces we will talk about are the dynamic proxy and security.

Zuul will serve as our API gateway. This, along with the service discovery of Eureka, makes setting up new services a breeze. It will register with a Eureka server and automatically set up dynamic routing based on other services that are also registered with Eureka to provide access to our APIs through one singular point. Zuul will also be configured with Spring Security in order to provide edge security across all our APIs.

### Maven Configuration
First, we'll add Zuul Server and Eureka dependency to our pom.xml:

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### Communication with Eureka
Secondly, we'll add the necessary properties in Zuul's application.properties file:

```
server.port=4444
spring.application.name=zuul-server
eureka.instance.preferIpAddress=true
eureka.client.registerWithEureka=true
eureka.client.fetchRegistry=true
eureka.client.serviceUrl.defaultZone=${EUREKA_URI:http://localhost:1111/eureka}
```

Here we're telling Zuul to register itself as a service in Eureka Server and run on port 4444.

Next, we'll annotate the main class with `@EnableZuulProxy` and `@EnableDiscoveryClient`. `@EnableZuulProxy` indicates this as Zuul Server and `@EnableDiscoveryClient` indicates this as Eureka Client.

```
@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
public class ZuulGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulGatewayApplication.class, args);
	}

}
```

We'll point our browser to http://localhost:4444/actuator/routes. This should show up all the routes available for Zuul that are discovered by Eureka:

```
{
	"/accounts-service/**": "accounts-service",
	"/accounts-web-service/**": "accounts-web-service"
}
```

### Load Balancing with Zuul

When Zuul receives a request, it picks up one of the physical locations available and forwards requests to the actual service instance. The whole process of caching the location of the service instances and forwarding the request to the actual location is provided out of the box with no additional configurations needed.

Internally, Zuul uses Netflix Ribbon to look up for all instances of the service from the service discovery (Eureka Server).

Let's observe this behavior when multiple instances are brought up.

### Registering Multiple Instances
We'll start by running two instances (9000 and 9001 ports).

```
java -jar -Dserver.port=9000 accounts-service-0.0.1-SNAPSHOT.jar

java -jar -Dserver.port=9001 accounts-web-Service-0.0.1-SNAPSHOT.jar
```

Another two instances are running on 2222 and 3333 port.

Once all the instances are up, we can observe in logs that physical locations of the instances are registered in DynamicServerListLoadBalancer and the route is mapped to Zuul Controller which takes care of forwarding requests to the actual instance:

```
INFO 1748 --- [nio-3333-exec-1] c.netflix.config.ChainedDynamicProperty  : Flipping property: ACCOUNTS-SERVICE.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
INFO 1748 --- [nio-3333-exec-1] c.n.u.concurrent.ShutdownEnabledTimer    : Shutdown hook installed for: NFLoadBalancer-PingTimer-ACCOUNTS-SERVICE
INFO 1748 --- [nio-3333-exec-1] c.netflix.loadbalancer.BaseLoadBalancer  : Client: ACCOUNTS-SERVICE instantiated a LoadBalancer: DynamicServerListLoadBalancer:{NFLoadBalancer:name=ACCOUNTS-SERVICE,current list of Servers=[],Load balancer stats=Zone stats: {},Server stats: []}ServerList:null
INFO 1748 --- [nio-3333-exec-1] c.n.l.DynamicServerListLoadBalancer      : Using serverListUpdater PollingServerListUpdater
INFO 1748 --- [nio-3333-exec-1] c.netflix.config.ChainedDynamicProperty  : Flipping property: ACCOUNTS-SERVICE.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
INFO 1748 --- [nio-3333-exec-1] c.n.l.DynamicServerListLoadBalancer      : DynamicServerListLoadBalancer for client ACCOUNTS-SERVICE initialized: DynamicServerListLoadBalancer:{NFLoadBalancer:name=ACCOUNTS-SERVICE,current list of Servers=[192.168.56.1:2222, 192.168.56.1:9000],Load balancer stats=Zone stats: {defaultzone=[Zone:defaultzone;	Instance count:2;	Active connections count: 0;	Circuit breaker tripped count: 0;	Active connections per server: 0.0;]
},Server stats: [[Server:192.168.56.1:9000;	Zone:defaultZone;	Total Requests:0;	Successive connection failure:0;	Total blackout seconds:0;	Last connection made:Thu Jan 01 05:30:00 IST 1970;	First connection made: Thu Jan 01 05:30:00 IST 1970;	Active Connections:0;	total failure count in last (1000) msecs:0;	average resp time:0.0;	90 percentile resp time:0.0;	95 percentile resp time:0.0;	min resp time:0.0;	max resp time:0.0;	stddev resp time:0.0]
, [Server:192.168.56.1:2222;	Zone:defaultZone;	Total Requests:0;	Successive connection failure:0;	Total blackout seconds:0;	Last connection made:Thu Jan 01 05:30:00 IST 1970;	First connection made: Thu Jan 01 05:30:00 IST 1970;	Active Connections:0;	total failure count in last (1000) msecs:0;	average resp time:0.0;	90 percentile resp time:0.0;	95 percentile resp time:0.0;	min resp time:0.0;	max resp time:0.0;	stddev resp time:0.0]
]}ServerList:org.springframework.cloud.netflix.ribbon.eureka.DomainExtractingServerList@4869a77d
INFO 1748 --- [nio-3333-exec-1] c.j.controller.WebAccountsController     : web-service byNumber() found: 123456789 [Keri Lee]: $50153.85
INFO 1748 --- [erListUpdater-0] c.netflix.config.ChainedDynamicProperty  : Flipping property: ACCOUNTS-SERVICE.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
```

### Guides
The following guides illustrate how to use some features concretely:

* [Routing and Filtering](https://spring.io/guides/gs/routing-and-filtering/)
