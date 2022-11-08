package com.team6.onandthefarmmemberservice.repository;

import com.team6.onandthefarmmemberservice.entity.admin.Admin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends CrudRepository<Admin, Long> {

    Optional<Admin> findAdminByAdminEmailAndAdminPassword(String adminEmail, String adminPassword);
}