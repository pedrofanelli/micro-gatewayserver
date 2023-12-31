package com.example.demo.filters;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * A correlation ID is a unique ID that gets carried across all the microservices that are 
 * executed when carrying out a customer request. A correlation ID allows us to trace the chain of events that 
 * occur as a call goes through a series of microservice calls.
 */
@Component
public class FilterUtils {

	public static final String CORRELATION_ID = "tmx-correlation-id";
	public static final String AUTH_TOKEN     = "tmx-auth-token";
	public static final String USER_ID        = "tmx-user-id";
	public static final String ORG_ID         = "tmx-org-id";
	public static final String PRE_FILTER_TYPE = "pre";
	public static final String POST_FILTER_TYPE = "post";
	public static final String ROUTE_FILTER_TYPE = "route";
	
	/**
	 * Extrae el valor almacenado en el Header CORRELATION_ID si es que existe
	 * 
	 * @param requestHeaders
	 * @return
	 */
	public String getCorrelationId(HttpHeaders requestHeaders){
		if (requestHeaders.get(CORRELATION_ID) != null) {
			List<String> header = requestHeaders.get(CORRELATION_ID);
			return header.stream().findFirst().get();
		} else {
			return null;
		}
	}
	
	/**
	 * Setea en el Header de una request el valor del Correlation ID pasado como parámetro
	 * 
	 * @param exchange
	 * @param correlationId
	 * @return
	 */
	public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
		return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
	}
	
	public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
		return exchange.mutate().request(
							exchange.getRequest().mutate()
							.header(name, value)
							.build())
						.build();	
	}
}
