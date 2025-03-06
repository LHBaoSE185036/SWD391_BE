package com.swd.gym_face_id_access.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull
    @Lob
    @Column(name = "type", nullable = false)
    private String type;

    @NotNull
    @Column(name = "TRAINING_DAY", nullable = false)
    private Integer trainingDay;

    @NotNull
    @Column(name = "PRICE", nullable = false)
    private Integer price;

    @NotNull
    @Column(name = "DURATION", nullable = false)
    private Integer duration;

    @Size(max = 255)
    @NotNull
    @Column(name = "NAME", nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "DESCRIPTION")
    private String description;

    @NotNull
    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    @Size(max = 45)
    @NotNull
    @Column(name = "slot_time_type", nullable = false, length = 45)
    private String slotTimeType;


}