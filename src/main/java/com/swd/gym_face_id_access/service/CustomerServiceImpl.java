package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.configuration.JwtUtil;
import com.swd.gym_face_id_access.dto.request.CreateCustomerRequest;
import com.swd.gym_face_id_access.dto.request.UpdateCustomerRequest;
import com.swd.gym_face_id_access.dto.response.CustomerDetailResponse;
import com.swd.gym_face_id_access.dto.response.CustomerResponse;
import com.swd.gym_face_id_access.exception.CustomerNotFoundException;
import com.swd.gym_face_id_access.exception.InvalidRequestException;
import com.swd.gym_face_id_access.exception.NoTokenException;
import com.swd.gym_face_id_access.exception.UnauthorizedException;
import com.swd.gym_face_id_access.model.Customer;
import com.swd.gym_face_id_access.model.Enum.Roles;
import com.swd.gym_face_id_access.repository.CustomerRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository customerRepository;

    private final CloudinaryService cloudinaryService;

    private final JwtUtil jwtUtil;

    private final HttpServletRequest request;

    @Override
    public List<CustomerResponse> getAllCustomer() {

        String token = jwtUtil.getCurrentToken(request);

        if(token == null) {
            throw new NoTokenException("Missing JWT token");
        }

        List<Customer> customers = customerRepository.findAll();
        List<CustomerResponse> customerResponses = new ArrayList<>();
        for (Customer customer : customers) {
            CustomerResponse customerResponse = new CustomerResponse();
            customerResponse.setCustomerId(customer.getId());
            customerResponse.setFullName(customer.getFullName());
            customerResponse.setPhoneNumber(String.valueOf(customer.getPhoneNumber()));
            customerResponse.setEmail(customer.getEmail());
            customerResponse.setStatus(customer.getStatus());
            customerResponses.add(customerResponse);
        }
        return customerResponses;
    }

    @Override
    public String addCustomer(CreateCustomerRequest createCustomerRequest) {

        String token = jwtUtil.getCurrentToken(request);

        if(!jwtUtil.extractRole(token).equals(Roles.ADMIN)) {
            throw new UnauthorizedException("Unauthorized access");
        }

        Customer customer = new Customer();
        if (createCustomerRequest.getFullName() == null || createCustomerRequest.getPhoneNumber() == null) {
            throw new InvalidRequestException("Some fields are missing or invalid");
        }
        customer.setFullName(createCustomerRequest.getFullName());
        customer.setPhoneNumber(createCustomerRequest.getPhoneNumber());
        customer.setEmail(createCustomerRequest.getEmail());
        customer.setStatus("active");
        customer.setWarningCounter(0);
        customerRepository.save(customer);
        return "Added Successfully";
    }

    @Override
    public void updateCustomerFaceImg(MultipartFile multipartFile, int customerId) throws IOException {

        String token = jwtUtil.getCurrentToken(request);

        if(!jwtUtil.extractRole(token).equals(Roles.ADMIN)) {
            throw new UnauthorizedException("Unauthorized access");
        }

        if (!customerRepository.existsById(customerId)) {
            return;
        }
        String ImgUrl = cloudinaryService.uploadFile(multipartFile);
        Customer customer = customerRepository.findById(customerId).get();
        customer.setFaceImage(ImgUrl);
        customerRepository.save(customer);
    }

    @Override
    public CustomerDetailResponse getCustomerDetail(int customerId) {
        if(!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found");
        }
        Customer customer = customerRepository.findById(customerId).get();
        CustomerDetailResponse customerDetailResponse = new CustomerDetailResponse();
        customerDetailResponse.setFullName(customer.getFullName());
        customerDetailResponse.setPhoneNumber(customer.getPhoneNumber());
        customerDetailResponse.setEmail(customer.getEmail());
        customerDetailResponse.setStatus(customer.getStatus());
        customerDetailResponse.setFaceURL(customer.getFaceImage());
        return customerDetailResponse;

    }

    @Override
    public String updateCustomer(UpdateCustomerRequest updateCustomerRequest, int customerId) {

        String token = jwtUtil.getCurrentToken(request);

        if(!jwtUtil.extractRole(token).equals(Roles.ADMIN)) {
            throw new UnauthorizedException("Unauthorized access");
        }

        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found");
        }

        Customer customer = customerRepository.findById(customerId).get();
        if(updateCustomerRequest.getFullName() != null && !updateCustomerRequest.getFullName().isEmpty()) {
            customer.setFullName(updateCustomerRequest.getFullName());
        }
        if(updateCustomerRequest.getPhoneNumber() != null && !updateCustomerRequest.getPhoneNumber().isEmpty()) {
            customer.setPhoneNumber(updateCustomerRequest.getPhoneNumber());
        }
        if(updateCustomerRequest.getEmail() != null && !updateCustomerRequest.getEmail().isEmpty()) {
            customer.setEmail(updateCustomerRequest.getEmail());
        }
        customerRepository.save(customer);
        return "Updated Successfully";
    }

    @Override
    public CustomerResponse findByID(int customerId) {
        Customer customer = customerRepository.getById(customerId);
        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setCustomerId(customer.getId());
        customerResponse.setFullName(customer.getFullName());
        customerResponse.setPhoneNumber(customer.getPhoneNumber());
        customerResponse.setEmail(customer.getEmail());
        customerResponse.setStatus(customer.getStatus());
        return customerResponse;
    }

    @Override
    public CustomerResponse findByFaceFeature(String faceFeature) {
        Customer customer = customerRepository.getByFaceFeature(faceFeature);
        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setCustomerId(customer.getId());
        customerResponse.setFullName(customer.getFullName());
        customerResponse.setPhoneNumber(customer.getPhoneNumber());
        customerResponse.setEmail(customer.getEmail());
        customerResponse.setStatus(customer.getStatus());
        return customerResponse;
    }

    @Override
    public List<CustomerResponse> getCustomerInGym() {
        String token = jwtUtil.getCurrentToken(request);

        if(token == null) {
            throw new NoTokenException("Missing JWT token");
        }

        List<Customer> customers = customerRepository.findByPresentStatus();
        List<CustomerResponse> customerResponses = new ArrayList<>();
        for(Customer customer : customers) {
            CustomerResponse customerResponse = new CustomerResponse();
            customerResponse.setCustomerId(customer.getId());
            customerResponse.setFullName(customer.getFullName());
            customerResponse.setPhoneNumber(customer.getPhoneNumber());
            customerResponse.setEmail(customer.getEmail());
            customerResponse.setStatus(customer.getStatus());
            customerResponses.add(customerResponse);
        }
        return customerResponses;
    }

    @Override
    public String banCustomer(int customerId) {
        String token = jwtUtil.getCurrentToken(request);

        if(token == null) {
            throw new NoTokenException("Missing JWT token");
        }

        if(!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found");
        }

        Customer customer = customerRepository.findById(customerId).get();
        customer.setStatus("banned");
        customerRepository.save(customer);
        return "Banned!";
    }

    @Override
    public String warningCustomer(int customerId) {
        String token = jwtUtil.getCurrentToken(request);

        if(!jwtUtil.extractRole(token).equals(Roles.ADMIN)) {
            throw new UnauthorizedException("Unauthorized access");
        }

        if(!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found");
        }

        Customer customer = customerRepository.findById(customerId).get();
        int warningCounter = customer.getWarningCounter() + 1;
        customer.setWarningCounter(warningCounter);
        if(warningCounter >= 5) {
            customer.setStatus("banned");
        }
        customerRepository.save(customer);
        return "Successfully";
    }

    @Override
    public List<CustomerResponse> searchCustomer(String name) {
        String token = jwtUtil.getCurrentToken(request);

        if(!jwtUtil.extractRole(token).equals(Roles.ADMIN)) {
            throw new UnauthorizedException("Unauthorized access");
        }

        List<Customer> customers = customerRepository.findByName(name);
        List<CustomerResponse> customerResponses = new ArrayList<>();
        for (Customer customer : customers) {
            CustomerResponse customerResponse = new CustomerResponse();
            customerResponse.setCustomerId(customer.getId());
            customerResponse.setFullName(customer.getFullName());
            customerResponse.setPhoneNumber(customer.getPhoneNumber());
            customerResponse.setEmail(customer.getEmail());
            customerResponse.setStatus(customer.getStatus());
            customerResponses.add(customerResponse);
        }
        return customerResponses;
    }


}
