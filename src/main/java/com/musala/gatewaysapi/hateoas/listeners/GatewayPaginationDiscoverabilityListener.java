package com.musala.gatewaysapi.hateoas.listeners;

import com.musala.gatewaysapi.hateoas.events.PaginatedResultsEvent;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.StringJoiner;

import static com.musala.gatewaysapi.constants.ApiMapping.API_ROOT;
import static com.musala.gatewaysapi.constants.ApiMapping.GET_ALL_GATEWAYS;

@NoArgsConstructor
@Component
public class GatewayPaginationDiscoverabilityListener
        implements ApplicationListener<PaginatedResultsEvent> {

    public static final int FIRST_PAGE = 0;

    @Override
    public void onApplicationEvent(PaginatedResultsEvent paginatedResultsEvent) {
        final UriComponentsBuilder uriBuilder = paginatedResultsEvent.getUriBuilder();
        uriBuilder.path(API_ROOT+GET_ALL_GATEWAYS);

        StringJoiner linkHeader = new StringJoiner(", ");
        if(hasFirstPage(paginatedResultsEvent.getPage()))
            linkHeader.add(getFirstPageLink(uriBuilder, paginatedResultsEvent));
        if (hasPreviousPage(paginatedResultsEvent.getPage()))
            linkHeader.add(getPrevPageLink(uriBuilder, paginatedResultsEvent));
        if (hasNextPage(paginatedResultsEvent.getPage(), paginatedResultsEvent.getTotalPages()))
            linkHeader.add(getNextPageLink(uriBuilder, paginatedResultsEvent));
        if (hasLastPage(paginatedResultsEvent.getPage(), paginatedResultsEvent.getTotalPages()))
            linkHeader.add(getLastPageLink(uriBuilder, paginatedResultsEvent));

        if (linkHeader.length() > 0)
            paginatedResultsEvent.getResponse().addHeader(HttpHeaders.LINK, linkHeader.toString());
    }

    private String getFirstPageLink(final UriComponentsBuilder uriBuilder, PaginatedResultsEvent paginatedResultsEvent) {
        return createLinkHeader(
                uriBuilder.replaceQueryParam("page", FIRST_PAGE)
                .replaceQueryParam("size", paginatedResultsEvent.getSize()).build().encode().toUriString(),
                "first");
    }

    private String getPrevPageLink(final UriComponentsBuilder uriBuilder, PaginatedResultsEvent paginatedResultsEvent) {
        return createLinkHeader(
                uriBuilder.replaceQueryParam("page", paginatedResultsEvent.getPage() -1)
                        .replaceQueryParam("size", paginatedResultsEvent.getSize()).build().encode().toUriString(),
                "prev");
    }

    private String getNextPageLink(final UriComponentsBuilder uriBuilder, PaginatedResultsEvent paginatedResultsEvent) {
        return createLinkHeader(
                uriBuilder.replaceQueryParam("page", paginatedResultsEvent.getPage() +1)
                        .replaceQueryParam("size", paginatedResultsEvent.getSize()).build().encode().toUriString(),
                "next");
    }

    private String getLastPageLink(final UriComponentsBuilder uriBuilder, PaginatedResultsEvent paginatedResultsEvent) {
        Integer lastPage = paginatedResultsEvent.getTotalPages() -1;
        return createLinkHeader(
                uriBuilder.replaceQueryParam("page", lastPage)
                        .replaceQueryParam("size", paginatedResultsEvent.getSize()).build().encode().toUriString(),
                "last");
    }

    final boolean hasFirstPage(final int page) {
        return hasPreviousPage(page);
    }

    final boolean hasPreviousPage(final int page) {
        return page > FIRST_PAGE;
    }

    final boolean hasNextPage(final int page, final int totalPages) {
        return page < (totalPages - 1);
    }

    final boolean hasLastPage(final int page, final int totalPages) {
        return (totalPages > 1) && hasNextPage(page, totalPages);
    }

    public static String createLinkHeader(final String uri, final String rel) {
        return "<" + uri + ">; rel=\"" + rel + "\"";
    }
}
