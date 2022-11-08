package com.team6.onandthefarmmemberservice.repository;

import com.team6.onandthefarmmemberservice.entity.user.ReservedPoint;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ReservedPointRepository extends CrudRepository<ReservedPoint,Long> {
    boolean existsByOrderSerialAndIdempoStatus(String orderSerial,boolean status);

    Optional<ReservedPoint> findByOrderSerial(String orderSerial);
}
