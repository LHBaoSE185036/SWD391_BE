package com.swd.gym_face_id_access.controller;

import com.swd.gym_face_id_access.dto.response.ApiResponse;
import com.swd.gym_face_id_access.dto.response.CustomerResponse;

import com.swd.gym_face_id_access.service.CustomerService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gym-face-id-access/api/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @GetMapping("/all-customer")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomer() {
        List<CustomerResponse> customerResponseList = customerService.getAllCustomer();
        return ResponseEntity.ok().body(ApiResponse.<List<CustomerResponse>>builder()
                        .errorCode(null)
                        .message("success")
                        .data(customerResponseList)
                        .success(true)
                .build());
    }
}
