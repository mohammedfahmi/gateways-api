package com.musala.gatewaysapi.hateoas.listeners;

import com.musala.gatewaysapi.hateoas.events.CreationResultEvent;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class CreationDiscoverabilityListener
        implements ApplicationListener<CreationResultEvent> {

    @Override
    public void onApplicationEvent(CreationResultEvent creationResultEvent) {
        String locationHeader = creationResultEvent.getUriBuilder().build().encode().toUriString();
        creationResultEvent.getResponse().addHeader(HttpHeaders.LOCATION, locationHeader);
    }
}
