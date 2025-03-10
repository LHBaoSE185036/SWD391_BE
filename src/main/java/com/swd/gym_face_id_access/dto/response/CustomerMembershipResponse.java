package com.swd.gym_face_id_access.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerMembershipResponse {
    private int id;
    private int customerId;
    private int membershipId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int sessionCounter;
}
