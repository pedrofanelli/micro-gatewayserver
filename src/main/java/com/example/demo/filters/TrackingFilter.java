package com.example.demo.filters;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * 
 * To create a global filter in the Spring Cloud Gateway, we need to implement the GlobalFilter class and 
 * then override the filter() method. This method contains the business logic that the filter implements.
 * 
 * Ahora, todo lo que pase por el Gateway pasará por este filtro! Entonces generamos un Correlation ID la primera vez que se 
 * ingresa al Gateway y se transferirá en toda la cadena de microservicios, representando el mismo ID. Es una forma de 
 * controlar el flujo entre los microservicios.
 * 
 * Lo que hace es inyectar el Correlation ID al Header a cualquier request que pase por este Gateway. 
 * 
 * Luego, cada microservicio, de forma aislada e independiente, formará sus UserContextFilters, es decir,
 * sus propios filtros, que se encargarán de tomar el Correlation ID que fue agregado por nosotros en 
 * este filtro, y lo agregará al CONTEXTO (un thread-local). Una vez agregado, ese microservicio podrá
 * usar esa variable como quiera. Es una variable aislada, dependiente del thread específico del request.
 * 
 * Al finalizar ese microservicio, la variable se agrega a cualquier request que se realice, al header.
 * 
 * Finalmente, al finalizar todo el circuito, crearemos un filtro en el Gateway, como el actual, pero 
 * para controlar la finalización del circuito.
 * 
 */
@Order(1)
@Component
public class TrackingFilter implements GlobalFilter {

	private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);

	@Autowired
	FilterUtils filterUtils;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) { // se ejecuta cada vez que la request pasa por el filtro
		HttpHeaders requestHeaders = exchange.getRequest().getHeaders(); // extraemos Headers de request
		if (isCorrelationIdPresent(requestHeaders)) {
			logger.debug("tmx-correlation-id found in tracking filter: {}. ", 
					filterUtils.getCorrelationId(requestHeaders)); // logueamos si está presente (parte de cadena de microservicios ya iniciada)
		} else {
			String correlationID = generateCorrelationId(); // lo generamos
			exchange = filterUtils.setCorrelationId(exchange, correlationID); // seteamos en Header, para eso pasamos exchange y el ID
			logger.debug("tmx-correlation-id generated in tracking filter: {}.", correlationID); // logueamos el ID generado
		}
		
		return chain.filter(exchange); //CONTINUA AL SIGUIENTE FILTRO
	}
	
	private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
		if (filterUtils.getCorrelationId(requestHeaders) != null) {
			return true;
		} else {
			return false;
		}
	}

	private String generateCorrelationId() {
		return UUID.randomUUID().toString();
	}
	
	
}
