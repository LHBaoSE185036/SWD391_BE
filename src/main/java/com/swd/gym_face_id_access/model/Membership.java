package com.swd.gym_face_id_access.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "membership")
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Lob
    @Column(name = "TYPE", nullable = false)
    private String type;

    @Column(name = "TRAINING_DAY", nullable = false)
    private Integer trainingDay;

    @Column(name = "PRICE", nullable = false)
    private Integer price;

    @Column(name = "DURATION", nullable = false)
    private Integer duration;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Lob
    @Column(name = "STATUS", nullable = false)
    private String status;

}