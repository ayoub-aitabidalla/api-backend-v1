package com.app.api.repositories;


import com.app.api.entities.business.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BusinessRepository extends JpaRepository<Business, Integer> {
    @Query(value = "SELECT * FROM Business b WHERE b.business_name = ?1 ", nativeQuery = true)
    Business findByBusinessName(String businessName);
    @Query(value = "SELECT * FROM Business b WHERE b.user_id = ?1", nativeQuery = true)
    List<Business> findByUserId(int id);

    @Query(value = "SELECT * FROM Business b WHERE b.business_name = ?1 AND b.user_id = ?2", nativeQuery = true)
    Business findByBusinessNameAndUser(String businessName, int id);

}
