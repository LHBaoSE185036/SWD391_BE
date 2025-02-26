package com.swd.gym_face_id_access.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "FULL_NAME", nullable = false)
    private String fullName;

    @Column(name = "PHONE_NUMBER", nullable = false, length = 10)
    private String phoneNumber;

    @Column(name = "FACE_IMAGE")
    private String faceImage;

    @Column(name = "FACE_FEATURE")
    private String faceFeature;

    @ColumnDefault("0")
    @Column(name = "WARNING_COUNTER")
    private Integer warningCounter;

    @Lob
    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "EMAIL", length = 50)
    private String email;

}