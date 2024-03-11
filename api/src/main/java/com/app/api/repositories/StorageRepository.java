package com.app.api.repositories;

import com.app.api.entities.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorageRepository extends JpaRepository<ImageModel, Long> {


    Optional<ImageModel> findByName(String fileName);
}
