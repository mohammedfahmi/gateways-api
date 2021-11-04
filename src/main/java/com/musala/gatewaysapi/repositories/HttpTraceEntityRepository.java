package com.musala.gatewaysapi.repositories;

import com.musala.gatewaysapi.entities.Trace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HttpTraceEntityRepository extends JpaRepository<Trace, Long> {
}
