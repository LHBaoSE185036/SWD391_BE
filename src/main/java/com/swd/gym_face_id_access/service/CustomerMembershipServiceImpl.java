package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.dto.response.CustomerMembershipResponse;
import com.swd.gym_face_id_access.model.CustomerMembership;
import com.swd.gym_face_id_access.repository.CustomerMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerMembershipServiceImpl implements CustomerMembershipService{
    private final CustomerMembershipRepository customerMembershipRepository;

    @Override
    public List<CustomerMembershipResponse> findActiveMemberships(int customerId) {
        List<CustomerMembership> customerMemberships = customerMembershipRepository.findActiveMemberships(customerId);
        List<CustomerMembershipResponse> customerMembershipResponses = new ArrayList<>();
        for (CustomerMembership cm : customerMemberships) {
            CustomerMembershipResponse customerMembershipResponse = new CustomerMembershipResponse();
            customerMembershipResponse.setId(cm.getId());
            customerMembershipResponse.setCustomerId(cm.getCustomer().getId());
            customerMembershipResponse.setStartDate(cm.getStartDate());
            customerMembershipResponse.setEndDate(cm.getEndDate());
            customerMembershipResponse.setSessionCounter(cm.getSessionCounter());
            customerMembershipResponses.add(customerMembershipResponse);
        }
        return customerMembershipResponses;
    }


}
