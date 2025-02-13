package com.swd.gym_face_id_access.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "staff")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "FULL_NAME", nullable = false)
    private String fullName;

    @Lob
    @Column(name = "ROLE", nullable = false)
    private String role;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "STATUS", nullable = false)
    private Boolean status = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    private Account account;

}