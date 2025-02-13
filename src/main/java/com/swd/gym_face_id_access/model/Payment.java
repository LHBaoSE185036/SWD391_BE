package com.swd.gym_face_id_access.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private Customer user;

    @Column(name = "CONTENT")
    private String content;

    @Lob
    @Column(name = "TYPE", nullable = false)
    private String type;

    @Lob
    @Column(name = "STATUS", nullable = false)
    private String status;

}