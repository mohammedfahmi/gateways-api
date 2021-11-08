package com.musala.gatewaysapi.hateoas.listeners;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ListenerUtil {
    public static String createLinkHeader(final String uri, final String rel) {
        return "<" + uri + ">; rel=\"" + rel + "\"";
    }
}
