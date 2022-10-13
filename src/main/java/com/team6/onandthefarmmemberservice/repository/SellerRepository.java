package com.team6.onandthefarmmemberservice.repository;

import com.team6.onandthefarmmemberservice.entity.seller.Seller;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends CrudRepository<Seller,Long> {

    Seller findBySellerEmail(String email);
    Seller findBySellerEmailAndSellerPassword(String sellerEmail, String sellerPassword);
}
