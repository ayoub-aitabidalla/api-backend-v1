package com.app.api.repositories;


import com.app.api.entities.business.BusinessCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BusinessCategoryReposiroty extends JpaRepository<BusinessCategory,Long> {
    @Query(value = "SELECT * FROM BusinessCategory b WHERE b.category_name = ?1", nativeQuery = true)
    BusinessCategory findByTypeName(String categoryName);
}
