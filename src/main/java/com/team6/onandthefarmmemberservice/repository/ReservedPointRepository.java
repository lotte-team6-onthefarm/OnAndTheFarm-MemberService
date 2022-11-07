package com.team6.onandthefarmmemberservice.repository;

import com.team6.onandthefarmmemberservice.entity.user.ReservedPoint;
import org.springframework.data.repository.CrudRepository;

public interface ReservedPointRepository extends CrudRepository<ReservedPoint,Long> {
    boolean existsByOrderSerial(String orderSerial);
}
