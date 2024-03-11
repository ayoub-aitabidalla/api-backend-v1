package com.app.api.entities.business;

import com.app.api.entities.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Business")
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String businessName;
    private String email;
    private String phone;
    private String address;
    private String facebookLink;
    private String instagramLink;
    private String googleLink;
    private String coverImageUrl;
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private BusinessType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
