package com.musala.gatewaysapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@UtilityClass
public class TestUtils {
    public static MultiValueMap<String, String> prepareQueryParams(String... keyValuePairs) {
        ArrayList<String> keyValuePairList = new ArrayList<>(Arrays.asList(keyValuePairs));
        Assert.isTrue(isEven(keyValuePairList.size()), "[Assertion failed] - keyValuePairs length must be even");
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        String key,value;
        while(!keyValuePairList.isEmpty()) {
            if(isEven(keyValuePairList.size())) {
                value = keyValuePairList.remove(keyValuePairList.size() - 1);
                key = keyValuePairList.remove(keyValuePairList.size() - 1);
                queryParams.add(key, value);
            }
        }
        return queryParams;
    }
    public static UriComponentsBuilder getUriBuilder() {
        return UriComponentsBuilder.fromUriString("http://localhost:8080/gateways-api");
    }
    @SuppressWarnings("RedundantModifiersUtilityClassLombok")
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean isEven(Integer Number) {
        return Number%2 == 0;
    }

    public static String generateRandomIPv4() {
        Random random = new Random();
        return random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256);
    }
}
