package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.configuration.JwtUtil;
import com.swd.gym_face_id_access.dto.request.CreateCustomerRequest;
import com.swd.gym_face_id_access.dto.request.UpdateCustomerRequest;
import com.swd.gym_face_id_access.dto.response.CustomerDetailResponse;
import com.swd.gym_face_id_access.dto.response.CustomerResponse;
import com.swd.gym_face_id_access.dto.response.CustomerResponseWithCount;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository customerRepository;

    private final CloudinaryService cloudinaryService;

    private final JwtUtil jwtUtil;

    private final HttpServletRequest request;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data(getCustomerInGym()));
        } catch (Exception e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void notifyClients() {
        CustomerResponseWithCount updatedData = getCustomerInGym();
        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("CUSTOMER_UPDATE")
                        .data(updatedData));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        }

        emitters.removeAll(deadEmitters);
    }

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
    public CustomerResponseWithCount getCustomerInGym() {


        List<Customer> customers = customerRepository.findByPresentStatus();
        List<CustomerResponse> customerResponses = new ArrayList<>();
        CustomerResponseWithCount customerResponsesWithCount = new CustomerResponseWithCount();
        int count = 0;


        for(Customer customer : customers) {
            CustomerResponse customerResponse = new CustomerResponse();
            customerResponse.setCustomerId(customer.getId());
            customerResponse.setFullName(customer.getFullName());
            customerResponse.setPhoneNumber(customer.getPhoneNumber());
            customerResponse.setEmail(customer.getEmail());
            customerResponse.setStatus(customer.getStatus());
            count++;
            customerResponses.add(customerResponse);
        }
        customerResponsesWithCount.setCustomers(customerResponses);
        customerResponsesWithCount.setCount(count);
        return customerResponsesWithCount;
    }

    @Override
    public String deleteCustomer(int customerId) {
        String token = jwtUtil.getCurrentToken(request);

        if(token == null) {
            throw new NoTokenException("Missing JWT token");
        }

        if(!jwtUtil.extractRole(token).equals(Roles.ADMIN)) {
            throw new UnauthorizedException("Unauthorized access");
        }

        if(!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found");
        }
        Customer customer = customerRepository.findById(customerId).get();
        customer.setStatus("inactive");
        customerRepository.save(customer);
        return "Deleted Successfully";
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
