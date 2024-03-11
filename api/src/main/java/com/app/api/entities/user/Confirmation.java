package com.app.api.entities.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
@Entity(name = "confirmations")
public class Confirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String confirmationKey;
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime createdDate;
    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public Confirmation(UserEntity user) {
        this.user = user;
        this.createdDate = LocalDateTime.now();
        this.confirmationKey = UUID.randomUUID().toString();
    }
}