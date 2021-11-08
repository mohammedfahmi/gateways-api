package com.musala.gatewaysapi.hateoas.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;

@Getter
public class GetResultEvent extends ApplicationEvent {
    private HttpServletResponse response;
    private UriComponentsBuilder uriBuilder;
    private Integer totalEntityCount;

    public GetResultEvent(Object source, UriComponentsBuilder  uriBuilder, HttpServletResponse response, Integer totalEntityCount ) {
        super(source);
        this.response = response;
        this.uriBuilder = uriBuilder;
        this.totalEntityCount = totalEntityCount;
    }
}
