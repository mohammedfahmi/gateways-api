package com.musala.gatewaysapi.hateoas.listeners;

import com.musala.gatewaysapi.hateoas.events.GetResultEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;

import static com.musala.gatewaysapi.constants.Constants.DEFAULT_PAGE_SIZE;
import static com.musala.gatewaysapi.hateoas.listeners.ListenerUtil.createLinkHeader;

public class GetDiscoverabilityListener
        implements ApplicationListener<GetResultEvent> {

    @Override
    public void onApplicationEvent(GetResultEvent getResultEvent) {
        String linkHeader = createLinkHeader( getResultEvent.getUriBuilder()
                .replaceQueryParam("page", getPageTotalCount(getResultEvent.getTotalEntityCount()))
                .replaceQueryParam("size", DEFAULT_PAGE_SIZE)
                .build().encode().toUriString() , "collection");
        getResultEvent.getResponse().addHeader(HttpHeaders.LINK, linkHeader);
    }

    private Integer getPageTotalCount(Integer count) {
        int divideBySize = count / DEFAULT_PAGE_SIZE;
        int remainderBySize = count % DEFAULT_PAGE_SIZE;
        return  remainderBySize > 0 ?  divideBySize : divideBySize-1;
    }
}
