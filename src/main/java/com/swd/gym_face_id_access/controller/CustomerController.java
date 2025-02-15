package com.swd.gym_face_id_access.controller;

import com.swd.gym_face_id_access.dto.request.CustomerRequest;
import com.swd.gym_face_id_access.dto.response.ApiResponse;
import com.swd.gym_face_id_access.dto.response.CustomerResponse;

import com.swd.gym_face_id_access.service.CustomerService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gym-face-id-access/api/customer")
public class CustomerController {

    private final CustomerService customerService;

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

    @PostMapping("/update-face/{customerId}")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> updateFace(@RequestParam("image") MultipartFile file,@PathVariable int customerId) throws IOException {
        customerService.updateCustomerFaceImg(file,customerId);
        return ResponseEntity.ok().body(ApiResponse.<List<CustomerResponse>>builder()
                .errorCode(null)
                .message("success")
                .success(true)
                .build());
    }

}
