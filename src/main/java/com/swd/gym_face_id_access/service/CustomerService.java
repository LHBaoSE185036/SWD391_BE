package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.dto.request.CreateCustomerRequest;
import com.swd.gym_face_id_access.dto.request.UpdateCustomerRequest;
import com.swd.gym_face_id_access.dto.response.CustomerDetailResponse;
import com.swd.gym_face_id_access.dto.response.CustomerResponse;
import com.swd.gym_face_id_access.dto.response.CustomerResponseWithCount;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CustomerService {
    List<CustomerResponse> getAllCustomer();

    String addCustomer(CreateCustomerRequest createCustomerRequest);

    void updateCustomerFaceImg(MultipartFile multipartFile, int customerId) throws IOException;

    CustomerDetailResponse getCustomerDetail(int customerId);

    String updateCustomer(UpdateCustomerRequest updateCustomerRequest, int customerId);

    String banCustomer(int customerId);

    String warningCustomer(int customerId);

    List<CustomerResponse> searchCustomer(String name);

    CustomerResponse findByID(int customerId);

    CustomerResponse findByFaceFeature(String faceFeature);

    CustomerResponseWithCount getCustomerInGym();

    String deleteCustomer(int customerId);
}
