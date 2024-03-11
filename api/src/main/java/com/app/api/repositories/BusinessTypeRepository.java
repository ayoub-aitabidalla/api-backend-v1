package com.app.api.repositories;


import com.app.api.entities.business.BusinessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BusinessTypeRepository extends JpaRepository<BusinessType,Long> {
    @Query(value = "SELECT * FROM BusinessType b WHERE b.type_name = ?1", nativeQuery = true)
    BusinessType findByTypeName(String typeName);

    @Query(value = "SELECT * FROM business_type b WHERE b.category_id = ?1", nativeQuery = true)
    List<BusinessType> findAllByCatgId(long id);

}
