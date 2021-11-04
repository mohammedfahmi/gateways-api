package com.musala.gatewaysapi.hateoas.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;

@Getter
public class PaginatedResultsEvent extends ApplicationEvent {
    private HttpServletResponse response;
    private UriComponentsBuilder uriBuilder;
    private Integer page;
    private Integer totalPages;
    private Integer size;

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
