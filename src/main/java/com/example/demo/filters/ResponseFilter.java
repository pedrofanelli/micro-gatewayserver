package com.example.demo.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import reactor.core.publisher.Mono;

/**
 * POST FILTER dentro del Gateway
 * 
 * Se implementa de una forma un poco distinta al PRE filter.
 * 
 * Obtiene los headers, extrae el Correlation ID, y lo loguea.
 * 
 * Luego lo vuelve a agregar y envia.
 */
@Configuration
public class ResponseFilter {

	final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);
    
    @Autowired
	FilterUtils filterUtils;
 
    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            	  HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
            	  String correlationId = filterUtils.getCorrelationId(requestHeaders);
            	  logger.debug("Adding the correlation id to the outbound headers. {}", correlationId);
                  exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, correlationId);
                  logger.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
              }));
        };
    }
}
