package com.musala.gatewaysapi.IT;

import com.musala.gatewaysapi.models.AbstractGateway;
import com.musala.gatewaysapi.utils.HealthCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.musala.gatewaysapi.utils.TestUtils.isEven;
@Slf4j
public class TestRestTemplate {

    private static final String NAME= "KM57OfbQz0scHVBn";
    private static final String PASSWORD = "4wiAox57mJRoVyUx";
    private static final String CONTEXT = "gateways-api";
    private static final String HOST = "localhost";
    private static final String PORT = "8080";
    private static final String API_ROOT = "api/rest";
    private static final String HEALTH_CHECK_URL = "http://localhost:8080/gateways-api/actuator/health";
    private static final String DATA_CHECK_URL = "http://localhost:8080/gateways-api/api/rest/gateways?size=4&page=10";

    private static final RestOperations restTemplate = new RestTemplate();

    public static Boolean healthCheckCall() {
        HealthCheck healthCheck;
        try {
            healthCheck = restTemplate.getForEntity(HEALTH_CHECK_URL, HealthCheck.class).getBody();
            assert healthCheck != null;
        } catch (Exception e) {
            return false;
        }
        return healthCheck.getStatus().equals("UP");
    }
    public static Boolean testCall() {
        AbstractGateway[] response;
        try {
            response =  restTemplate.exchange(DATA_CHECK_URL,HttpMethod.GET,new HttpEntity<>(createHttpHeaders()), AbstractGateway[].class).getBody();
            assert response != null;
        } catch (Exception e) {
            return false;
        }
        return response.length > 0;
    }
    public static <T> ResponseEntity<?> restCall(final String uri, final HttpMethod method, final Map<String,String> queryParameters, final Class<?> responseType, final T requestBody) {
        switch (method) {
            case GET:
                return doGet(uri, queryParameters, responseType);
            case POST:
                return doPost(uri, queryParameters, requestBody, responseType);
            case PUT:
                return doPut(uri, queryParameters, requestBody, responseType);
            default:
                return doDelete(uri, queryParameters, requestBody, responseType);
        }
    }

    private static <T> ResponseEntity<?> doGet(final String uri, final Map<String,String> queryParameters, final Class<?> responseType) {
        final HttpEntity<T> headers = new HttpEntity<>(createHttpHeaders());
        return restTemplate.exchange(uriBuilder(uri,queryParameters),HttpMethod.GET,headers,responseType);
    }

    private static <T> ResponseEntity<?> doPost(final String uri, final Map<String,String> queryParameters, final T requestBody, final Class<?> responseType) {
        final HttpEntity<T> requestEntity = new HttpEntity<>(requestBody, createHttpHeaders());
        return restTemplate.exchange(uriBuilder(uri,queryParameters),HttpMethod.POST,requestEntity,responseType);
    }

    private static <T> ResponseEntity<?> doPut(final String uri, final Map<String,String> queryParameters, final T requestBody, final Class<?> responseType) {
        final HttpEntity<T> requestEntity = new HttpEntity<>(requestBody, createHttpHeaders());
        return restTemplate.exchange(uriBuilder(uri,queryParameters),HttpMethod.PUT,requestEntity,responseType);
    }

    private static <T> ResponseEntity<?> doDelete(final String uri, final Map<String,String> queryParameters, final T requestBody, final Class<?> responseType) {
        final HttpEntity<T> requestEntity = new HttpEntity<>(requestBody, createHttpHeaders());
        return restTemplate.exchange(uriBuilder(uri,queryParameters),HttpMethod.DELETE,requestEntity,responseType);
    }

    private static String uriBuilder(final String URI) {
        final String baseOfURI = MessageFormat.format("{0}:{1}/{2}/{3}", HOST, PORT, CONTEXT, API_ROOT);
        return MessageFormat.format("http://{0}{1}",baseOfURI,URI);
    }

    private static String uriBuilder(final String URI, final Map<String,String> queryParameters) {
        final StringBuilder baseOfURI = new StringBuilder(10).append(uriBuilder(URI));
        String params = "";
        if (!queryParameters.isEmpty()) {
            baseOfURI.append("?");
            final Set<String> keys = queryParameters.keySet();
            params = keys.stream().map(key -> MessageFormat.format("{0}={1}", key, queryParameters.get(key)))
                    .collect(Collectors.joining("&"));
        }
        return baseOfURI.append(params).toString();
    }

    private static HttpHeaders createHttpHeaders() {
        final String notEncoded = NAME + ":" + PASSWORD;
        final String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(notEncoded.getBytes());
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", encodedAuth);
        return headers;
    }
    public static Map<String, String> prepareQueryParams(String... keyValuePairs) {
        ArrayList<String> keyValuePairList = new ArrayList<>(Arrays.asList(keyValuePairs));
        Assert.isTrue(isEven(keyValuePairList.size()), "[Assertion failed] - keyValuePairs length must be even");
        Map<String, String> queryParams = new HashMap<>();
        String key,value;
        while(!keyValuePairList.isEmpty()) {
            if(isEven(keyValuePairList.size())) {
                value = keyValuePairList.remove(keyValuePairList.size() - 1);
                key = keyValuePairList.remove(keyValuePairList.size() - 1);
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }
}
