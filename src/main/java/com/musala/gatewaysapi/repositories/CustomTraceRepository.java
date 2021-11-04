package com.musala.gatewaysapi.repositories;

import com.google.gson.Gson;
import com.musala.gatewaysapi.entities.Trace;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Repository
public class CustomTraceRepository implements HttpTraceRepository {
    private HttpTraceEntityRepository httpTraceEntityRepository;
    private final Gson gson = new Gson();
    @Override
    public List<HttpTrace> findAll() {
        List<HttpTrace> result = httpTraceEntityRepository.findAll(
                PageRequest.of(0,100, Sort.by(Sort.Direction.DESC, "traceDate"))
        ).getContent().stream().map(Trace::getHttpTraceFromTraceEntity).collect(Collectors.toList());
        log.debug("All Traces: {}", gson.toJson(result));
        return result;
    }

    @Override
    public void add(HttpTrace httpTrace) {
        String trace = gson.toJson(httpTrace);
        log.debug("Incoming Request Trace: {}",trace);
        httpTraceEntityRepository.save(
                Trace.builder()
                        .traceDate(LocalDateTime.now(ZoneOffset.UTC))
                        .tracedRequestResponse(trace).build()
        );
    }
}
