package com.swd.gym_face_id_access.controller;

import com.swd.gym_face_id_access.dto.request.CreateCustomerRequest;
import com.swd.gym_face_id_access.dto.request.UpdateCustomerRequest;
import com.swd.gym_face_id_access.dto.response.ApiResponse;
import com.swd.gym_face_id_access.dto.response.CustomerDetailResponse;
import com.swd.gym_face_id_access.dto.response.CustomerResponse;

import com.swd.gym_face_id_access.exception.CustomerNotFoundException;
import com.swd.gym_face_id_access.exception.InvalidRequestException;
import com.swd.gym_face_id_access.service.CustomerService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gym-face-id-access/api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomer() {
        List<CustomerResponse> customerResponseList = customerService.getAllCustomer();
        return ResponseEntity.ok().body(ApiResponse.<List<CustomerResponse>>builder()
                        .errorCode(null)
                        .message("success")
                        .data(customerResponseList)
                        .success(true)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDetailResponse>> getCustomerById(@PathVariable int id) {
        try {
            return ResponseEntity.ok().body(ApiResponse.<CustomerDetailResponse>builder()
                    .errorCode(null)
                    .message("success")
                    .data(customerService.getCustomerDetail(id))
                    .success(true)
                    .build());
        } catch (CustomerNotFoundException e) {
            throw new CustomerNotFoundException(e.getMessage());
        }
    }

    @PutMapping("/update-face/{customerId}")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> updateFace(@RequestParam("image") MultipartFile file,@PathVariable int customerId) throws IOException {
        customerService.updateCustomerFaceImg(file,customerId);
        return ResponseEntity.ok().body(ApiResponse.<List<CustomerResponse>>builder()
                .errorCode(null)
                .message("success")
                .success(true)
                .build());
    }

    @PostMapping("/customer")
    public ResponseEntity<?> addCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
        try{
            return ResponseEntity.ok().body(ApiResponse.<String>builder()
                    .errorCode(null)
                    .message("success")
                    .data(customerService.addCustomer(createCustomerRequest))
                    .success(true)
                    .build());
        } catch (InvalidRequestException invalidRequestException) {
            throw new InvalidRequestException(invalidRequestException.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable int id, @RequestBody UpdateCustomerRequest updateCustomerRequest) {
        try{
            return ResponseEntity.ok().body(ApiResponse.<String>builder()
                    .errorCode(null)
                    .message("success")
                    .data(customerService.updateCustomer(updateCustomerRequest, id))
                    .success(true)
                    .build());
        } catch (CustomerNotFoundException e) {
            throw new CustomerNotFoundException(e.getMessage());
        }
    }
}
