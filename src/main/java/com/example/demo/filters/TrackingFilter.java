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
