package com.team6.onandthefarmmemberservice.repository;

import com.team6.onandthefarmmemberservice.entity.seller.Seller;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends CrudRepository<Seller,Long> {

    Optional<Seller> findBySellerEmail(String email);
    Optional<Seller> findBySellerNameAndAndSellerPhone(String name, String sellerPhone);

    Optional<Seller> findBySellerEmailAndSellerName(String sellerEmail,String name);
}
