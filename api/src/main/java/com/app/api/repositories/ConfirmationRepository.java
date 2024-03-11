package com.app.api.repositories;

import com.app.api.entities.user.Confirmation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationRepository extends JpaRepository<Confirmation,Long> {
    Confirmation findByConfirmationKey(String confirmationKey);
    
}
