package com.swd.gym_face_id_access.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CustomerResponseWithCount {
    private int count;
    private List<CustomerResponse> customers;
}
