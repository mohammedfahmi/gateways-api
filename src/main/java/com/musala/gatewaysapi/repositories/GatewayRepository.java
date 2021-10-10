package com.musala.gatewaysapi.repositories;

import com.musala.gatewaysapi.entities.Gateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GatewayRepository extends JpaRepository<Gateway, Long> {

    @Query(value = "SELECT count(id) FROM Gateway" )
    Integer getAllGatewaysCount();
}