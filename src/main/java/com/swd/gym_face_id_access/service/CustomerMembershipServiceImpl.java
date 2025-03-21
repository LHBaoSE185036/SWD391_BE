package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.configuration.JwtUtil;
import com.swd.gym_face_id_access.dto.response.CustomerMembershipResponse;
import com.swd.gym_face_id_access.exception.CustomerNotFoundException;
import com.swd.gym_face_id_access.exception.MembershipNotFoundException;
import com.swd.gym_face_id_access.exception.NoTokenException;
import com.swd.gym_face_id_access.exception.UnauthorizedException;
import com.swd.gym_face_id_access.model.Customer;
import com.swd.gym_face_id_access.model.CustomerMembership;
import com.swd.gym_face_id_access.model.Enum.Roles;
import com.swd.gym_face_id_access.model.Membership;
import com.swd.gym_face_id_access.repository.CustomerMembershipRepository;
import com.swd.gym_face_id_access.repository.CustomerRepository;
import com.swd.gym_face_id_access.repository.MembershipRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerMembershipServiceImpl implements CustomerMembershipService{
    private final CustomerMembershipRepository customerMembershipRepository;

    private final JwtUtil jwtUtil;

    private final CustomerRepository customerRepository;

    private final MembershipRepository membershipRepository;

    private final HttpServletRequest request;

    private final CustomerServiceImpl customerServiceImpl;

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

    @Override
    public String regisCustomerMembership(int customerId, int membershipId) {
        String token = jwtUtil.getCurrentToken(request);
        Customer customer;
        Membership membership ;
        int tempTime;
        int counter;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate;

        if(!jwtUtil.extractRole(token).equals(Roles.ADMIN)) {
            throw new UnauthorizedException("Unauthorized access");
        }
        if(!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer is not found");
        } else{
            customer = customerRepository.findById(customerId).get();
        }
        if(!membershipRepository.existsById(membershipId)) {
            throw new MembershipNotFoundException("Membership is not found");
        } else{
            membership = membershipRepository.findById(membershipId).get();
            tempTime = membership.getDuration();
            counter = membership.getTrainingDay();
        }

        List<CustomerMembership> customerMemberships = customerMembershipRepository.findActiveMemberships(customerId);
        if(!customerMemberships.isEmpty()) {
            return "Customer already has membership that still available";
        }

        endDate = startDate.plusMonths(tempTime);

        CustomerMembership customerMembership = new CustomerMembership();
        customerMembership.setCustomer(customer);
        customerMembership.setMembership(membership);
        customerMembership.setStartDate(startDate);
        customerMembership.setEndDate(endDate);
        customerMembership.setSessionCounter(counter);
        customerMembershipRepository.save(customerMembership);

        return "Registered Successfully";
    }

    @Override
    public String deleteCustomerMembership(int id) {
        String token = jwtUtil.getCurrentToken(request);

        if(token == null) {
            throw new NoTokenException("Missing JWT token");
        }

        if(!customerMembershipRepository.existsById(id)) {
            throw new MembershipNotFoundException("Membership is not found");
        }

        customerMembershipRepository.deleteById(id);

        return "Deleted Successfully";
    }

    @Override
    public List<CustomerMembershipResponse> getAllMemberships() {
        String token = jwtUtil.getCurrentToken(request);

        if(token == null) {
            throw new NoTokenException("Missing JWT token");
        }

        List<CustomerMembership>  customerMemberships = customerMembershipRepository.findAll();
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

    @Override
    public String checkOutManually(int customerId) {
        String token = jwtUtil.getCurrentToken(request);

        if(token == null) {
            throw new NoTokenException("Missing JWT token");
        }
        if(!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer is not found");
        }

        Customer customer = customerRepository.findById(customerId).get();
        if(!customer.getPresentStatus()){
            return "Customer is not in the gym! Please check again";
        }
        customer.setPresentStatus(false);
        customerRepository.save(customer);
        customerServiceImpl.notifyClients();

        return "Checked out";
    }


}
