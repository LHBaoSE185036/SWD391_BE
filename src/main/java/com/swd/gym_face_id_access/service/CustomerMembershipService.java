package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.dto.response.CustomerMembershipResponse;

import java.util.List;

public interface CustomerMembershipService {
    List<CustomerMembershipResponse> findActiveMemberships(int customerId);
}
