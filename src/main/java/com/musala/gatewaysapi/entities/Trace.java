package com.musala.gatewaysapi.entities;

import com.google.gson.Gson;
import lombok.*;
import org.springframework.boot.actuate.trace.http.HttpTrace;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "http_trace")
@Entity
public class Trace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trace_id", nullable = false)
    private Long id;

    @Lob
    @Column(name = "traced_request_response", nullable = false)
    private String tracedRequestResponse;

    @Column(name = "trace_date", nullable = false)
    private LocalDateTime traceDate;

    public HttpTrace getHttpTraceFromTraceEntity() {
        Gson gson = new Gson();
        return  gson.fromJson( this.tracedRequestResponse , HttpTrace.class);
    }
}