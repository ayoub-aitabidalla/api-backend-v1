package com.app.api.entities.user;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@NoArgsConstructor
@Setter
@Getter
@Entity
public class ResetPasswordVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime expiration_Date;
    @Column(unique = true)
    private String url;
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;



}
