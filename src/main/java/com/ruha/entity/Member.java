package com.ruha.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime updated;

    public Member(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;

    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.created = now;
        this.updated = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }
}
