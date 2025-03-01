package com.ruha.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created;

    @OneToOne(mappedBy = "category", fetch = FetchType.LAZY)
    private TodoList todoList;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.created = now;
    }


}
