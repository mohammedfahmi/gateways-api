package com.musala.gatewaysapi.hateoas.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;

@Getter
public class SaveResultEvent extends ApplicationEvent {
    private HttpServletResponse response;
    private UriComponentsBuilder uriBuilder;

    public SaveResultEvent(Object source, UriComponentsBuilder  uriBuilder, HttpServletResponse response) {
        super(source);
        this.response = response;
        this.uriBuilder = uriBuilder;
    }
}
