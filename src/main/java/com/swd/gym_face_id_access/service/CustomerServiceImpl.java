package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.dto.request.CustomerRequest;
import com.swd.gym_face_id_access.dto.response.CustomerResponse;
import com.swd.gym_face_id_access.model.Customer;
import com.swd.gym_face_id_access.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final CloudinaryService cloudinaryService;

    @Override
    public List<CustomerResponse> getAllCustomer() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerResponse> customerResponses = new ArrayList<>();
        for (Customer customer : customers) {
            CustomerResponse customerResponse = new CustomerResponse();
            customerResponse.setFullName(customer.getFullName());
            customerResponse.setPhoneNumber(String.valueOf(customer.getPhoneNumber()));
            customerResponse.setEmail(customer.getEmail());
            customerResponse.setStatus(customer.getStatus());
            customerResponses.add(customerResponse);
        }
        return customerResponses;
    }

    @Override
    public boolean addCustomer(CustomerRequest customerRequest) {
        return false;
    }

    @Override
    public void updateCustomerFaceImg(MultipartFile multipartFile, int customerId) throws IOException {
        if (!customerRepository.existsById(customerId)) {
            return;
        }
        String ImgUrl = cloudinaryService.uploadFile(multipartFile);
        Customer customer = customerRepository.findById(customerId).get();
        customer.setFaceImage(ImgUrl);
        customerRepository.save(customer);
    }


}
