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
            response =  restTemplate.exchange(DATA_CHECK_URL,HttpMethod.GET,new HttpEntity<>(createHttpHeaders(true)), AbstractGateway[].class).getBody();
            assert response != null;
        } catch (Exception e) {
            return false;
        }
        return response.length > 0;
    }
    public static <T> ResponseEntity<?> restCall(final String uri, final HttpMethod method, final Map<String,String> queryParameters,
                                                 final Class<?> responseType, final T requestBody, final Boolean withAuth, String... uriVariables) {
        switch (method) {
            case GET:
                return doGet(uri, queryParameters, responseType, withAuth, uriVariables);
            case POST:
                return doPost(uri, queryParameters, requestBody, responseType, withAuth, uriVariables);
            case PUT:
                return doPut(uri, queryParameters, requestBody, responseType, withAuth, uriVariables);
            default:
                return doDelete(uri, queryParameters, requestBody, responseType, withAuth, uriVariables);
        }
    }

    private static <T> ResponseEntity<?> doGet(final String uri, final Map<String,String> queryParameters,
                                               final Class<?> responseType, final Boolean withAuth, String... uriVariables) {
        final HttpEntity<T> headers = new HttpEntity<>(createHttpHeaders(withAuth));
        return restTemplate.exchange(uriBuilder(uri,queryParameters),HttpMethod.GET,headers,responseType, (Object[]) uriVariables);
    }

    private static <T> ResponseEntity<?> doPost(final String uri, final Map<String,String> queryParameters,
                                                final T requestBody, final Class<?> responseType, final Boolean withAuth, String... uriVariables) {
        final HttpEntity<T> requestEntity = new HttpEntity<>(requestBody, createHttpHeaders(withAuth));
        return restTemplate.exchange(uriBuilder(uri,queryParameters),HttpMethod.POST,requestEntity,responseType, (Object[]) uriVariables);
    }

    private static <T> ResponseEntity<?> doPut(final String uri, final Map<String,String> queryParameters,
                                               final T requestBody, final Class<?> responseType, final Boolean withAuth, String... uriVariables) {
        final HttpEntity<T> requestEntity = new HttpEntity<>(requestBody, createHttpHeaders(withAuth));
        return restTemplate.exchange(uriBuilder(uri,queryParameters),HttpMethod.PUT,requestEntity,responseType, (Object[]) uriVariables);
    }

    private static <T> ResponseEntity<?> doDelete(final String uri, final Map<String,String> queryParameters,
                                                  final T requestBody, final Class<?> responseType, final Boolean withAuth, String... uriVariables) {
        final HttpEntity<T> requestEntity = new HttpEntity<>(requestBody, createHttpHeaders(withAuth));
        return restTemplate.exchange(uriBuilder(uri,queryParameters),HttpMethod.DELETE,requestEntity,responseType, (Object[]) uriVariables);
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

    private static HttpHeaders createHttpHeaders(Boolean withAuth) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(withAuth) {
            final String notEncoded = NAME + ":" + PASSWORD;
            final String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(notEncoded.getBytes());
            headers.add("Authorization", encodedAuth);
        }
        return headers;
    }
    public static Map<String, String> prepareParams(String... keyValuePairs) {
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
