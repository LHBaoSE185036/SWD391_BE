package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.dto.response.CustomerMembershipResponse;

import java.util.List;

public interface CustomerMembershipService {
    List<CustomerMembershipResponse> findActiveMemberships(int customerId);

    String regisCustomerMembership(int customerId, int membershipId);

    String deleteCustomerMembership(int id);

    List<CustomerMembershipResponse> getAllMemberships();

    String checkOutManually(int customerId);
}
