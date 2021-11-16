package com.musala.gatewaysapi.hateoas.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;

@Getter
public class PaginatedResultsEvent extends ApplicationEvent {
    private final HttpServletResponse response;
    private final UriComponentsBuilder uriBuilder;
    private final Integer page;
    private final Integer totalPages;
    private final Integer size;

    public PaginatedResultsEvent(Object source, UriComponentsBuilder  uriBuilder, HttpServletResponse response,
                                 Integer page, Integer totalPages, Integer size) {
        super(source);
        this.response = response;
        this.uriBuilder = uriBuilder;
        this.page = page;
        this.totalPages = totalPages;
        this.size = size;
    }
}
