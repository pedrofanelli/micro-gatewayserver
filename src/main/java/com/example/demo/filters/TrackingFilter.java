package com.example.demo.filters;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class TrackingFilter implements GlobalFilter {

	
}
