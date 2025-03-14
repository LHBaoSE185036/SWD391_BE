package com.swd.gym_face_id_access.controller;

import com.swd.gym_face_id_access.dto.response.ApiResponse;
import com.swd.gym_face_id_access.dto.response.CustomerMembershipResponse;
import com.swd.gym_face_id_access.exception.CustomerNotFoundException;
import com.swd.gym_face_id_access.exception.MembershipNotFoundException;
import com.swd.gym_face_id_access.service.CustomerMembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gym-face-id-access/api/v1/customer-membership")
public class CustomerMembershipController {

    private final CustomerMembershipService customerMembershipService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestParam("customerId") int customerId, @RequestParam("membershipId") int membershipId) {
        try{
            return ResponseEntity.ok().body(ApiResponse.<String>builder()
                    .errorCode(null)
                    .message("Success")
                    .data(customerMembershipService.regisCustomerMembership(customerId, membershipId))
                    .success(true)
                    .build());
        }catch (CustomerNotFoundException e){
            throw new CustomerNotFoundException(e.getMessage());
        }catch (MembershipNotFoundException e){
            throw new MembershipNotFoundException(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCustomerMembership(@PathVariable int id) {
        return ResponseEntity.ok().body(ApiResponse.<String>builder()
                        .errorCode(null)
                        .message("Success")
                        .data(customerMembershipService.deleteCustomerMembership(id))
                        .success(true)
                .build());
    }

    @Cacheable("customerMemberships")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CustomerMembershipResponse>>> getAll() {
        System.out.println("Fetching all customers memberships from database");
        return ResponseEntity.ok().body(ApiResponse.<List<CustomerMembershipResponse>>builder()
                        .errorCode(null)
                        .message("Success")
                        .data(customerMembershipService.getAllMemberships())
                        .success(true)
                .build());
    }

    @PostMapping("/check-out-manual/{customerId}")
    public ResponseEntity<ApiResponse<String>> checkOuManual(@PathVariable int customerId) {
        return ResponseEntity.ok().body(ApiResponse.<String>builder()
                        .errorCode(null)
                        .message("Success")
                        .data(customerMembershipService.checkOutManually(customerId))
                        .success(true)
                .build());
    }
}
