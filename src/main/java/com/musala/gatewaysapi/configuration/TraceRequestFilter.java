package com.musala.gatewaysapi.configuration;

import org.springframework.boot.actuate.trace.http.HttpExchangeTracer;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.web.trace.servlet.HttpTraceFilter;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Component
public class TraceRequestFilter extends HttpTraceFilter {
    /**
     * Create a new {@link HttpTraceFilter} instance.
     *
     * @param repository the trace repository
     * @param tracer     used to trace exchanges
     */
    public TraceRequestFilter(HttpTraceRepository repository, HttpExchangeTracer tracer) {
        super(repository, tracer);
    }

    /**
     *
     * @param request  the incoming http request
     * @return - Boolean - if true don't add the trace
     * @throws ServletException
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().contains("actuator");
    }
}
