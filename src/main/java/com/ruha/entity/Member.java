package com.ruha.entity;

import com.ruha.dto.MemberRequest;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDateTime;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime updated;

    @OneToMany(mappedBy = "follower")
    private List<Follow> followings;

    @OneToMany(mappedBy = "following")
    private List<Follow> followers;

    public Member(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static Member toEntity(MemberRequest memberRequest) {
        return Member.builder()
                .email(memberRequest.getEmail())
                .password(memberRequest.getPassword())
                .name(memberRequest.getName())
                .build();
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.created = now;
        this.updated = now;
        this.roleType = RoleType.NORMAL;
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }
}
