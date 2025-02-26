package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.dto.request.CreateCustomerRequest;
import com.swd.gym_face_id_access.dto.response.CustomerDetailResponse;
import com.swd.gym_face_id_access.dto.response.CustomerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CustomerService {
    List<CustomerResponse> getAllCustomer();

    String addCustomer(CreateCustomerRequest createCustomerRequest);

    void updateCustomerFaceImg(MultipartFile multipartFile, int customerId) throws IOException;

    CustomerDetailResponse getCustomerDetail(int customerId);
}
