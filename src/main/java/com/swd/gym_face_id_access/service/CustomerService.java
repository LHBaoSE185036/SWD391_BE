package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.dto.request.CustomerRequest;
import com.swd.gym_face_id_access.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {
    List<CustomerResponse> getAllCustomer();

    boolean addCustomer(CustomerRequest customerRequest);
}
