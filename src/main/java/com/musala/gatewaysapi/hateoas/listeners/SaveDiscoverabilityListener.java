package com.musala.gatewaysapi.hateoas.listeners;

import com.musala.gatewaysapi.hateoas.events.SaveResultEvent;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class SaveDiscoverabilityListener
        implements ApplicationListener<SaveResultEvent> {

    @Override
    public void onApplicationEvent(SaveResultEvent saveResultEvent) {
        String locationHeader = saveResultEvent.getUriBuilder().build().encode().toUriString();
        saveResultEvent.getResponse().addHeader(HttpHeaders.LOCATION, locationHeader);
    }
}
