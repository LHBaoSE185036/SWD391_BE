package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.model.Customer;
import com.swd.gym_face_id_access.model.CustomerMembership;
import com.swd.gym_face_id_access.repository.CustomerMembershipRepository;
import com.swd.gym_face_id_access.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ScheduleTask {

    private final CustomerRepository customerRepository;

    private final CustomerMembershipRepository customerMembershipRepository;

    @Scheduled(cron = "0 30 8 * * ?")
    @Transactional
    public void checkOutCustomersSlotTimeA0830() {
        String slotTime = "";
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            List<CustomerMembership> customerMemberships = customerMembershipRepository.findByCustomerId(customer.getId());
            for (CustomerMembership customerMembership : customerMemberships) {
                slotTime  = customerMembership.getMembership().getSlotTimeType();
            }
            if(customer.getPresentStatus() && Objects.equals(slotTime, "Khung giờ A")){
                customer.setPresentStatus(false);
                customerRepository.save(customer);
            }
        }
    }

    @Scheduled(cron = "0 0 21 * * ?")
    @Transactional
    public void checkOutCustomersSlotTimeA2100() {
        String slotTime = "";
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            List<CustomerMembership> customerMemberships = customerMembershipRepository.findByCustomerId(customer.getId());
            for (CustomerMembership customerMembership : customerMemberships) {
                slotTime  = customerMembership.getMembership().getSlotTimeType();
            }
            if(customer.getPresentStatus() && Objects.equals(slotTime, "Khung giờ A")){
                customer.setPresentStatus(false);
                customerRepository.save(customer);
            }
        }
    }

    @Scheduled(cron = "0 0 14 * * ?")
    @Transactional
    public void checkOutCustomersSlotTimeB1400() {
        String slotTime = "";
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            List<CustomerMembership> customerMemberships = customerMembershipRepository.findByCustomerId(customer.getId());
            for (CustomerMembership customerMembership : customerMemberships) {
                slotTime  = customerMembership.getMembership().getSlotTimeType();
            }
            if(customer.getPresentStatus() && Objects.equals(slotTime, "Khung giờ B")){
                customer.setPresentStatus(false);
                customerRepository.save(customer);
            }
        }
    }

    @Scheduled(cron = "0 0 18 * * ?")
    @Transactional
    public void checkOutCustomersSlotTimeB1800() {
        String slotTime = "";
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            List<CustomerMembership> customerMemberships = customerMembershipRepository.findByCustomerId(customer.getId());
            for (CustomerMembership customerMembership : customerMemberships) {
                slotTime  = customerMembership.getMembership().getSlotTimeType();
            }
            if(customer.getPresentStatus() && Objects.equals(slotTime, "Khung giờ B")){
                customer.setPresentStatus(false);
                customerRepository.save(customer);
            }
        }
    }
}
