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
 * 
 * En realidad, me permite sacar el request que pasó por el Gateway, saco de ahí el ID, y formo la Response
 * 
 * NO USO LA DATA DE LO QUE VUELVE!!!
 * 
 * Lo que agrega el INTERCEPTOR dentro de un microservicio se usa si se le pega a OTRO microservicio.
 * 
 * Si nosotros le pegamos a 1 solo microservicio, y este devuelve la data, NO actuará su Interceptor. Es por eso
 * que este filtro funciona aunque el microservicio no le haya pegado a otro micro. Usa la data del request original, que
 * paso por el PRE filter al inicio del circuito.
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
            	  logger.debug("ResponseFilter en Gateway: Adding the correlation id to the outbound headers. {}", correlationId);
                  exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, correlationId);
                  logger.debug("ResponseFilter en Gateway: Completing outgoing request for {}.", exchange.getRequest().getURI());
              }));
        };
    }
}
