package com.swd.gym_face_id_access.controller;

import com.swd.gym_face_id_access.dto.response.ApiResponse;
import com.swd.gym_face_id_access.exception.CustomerNotFoundException;
import com.swd.gym_face_id_access.exception.MembershipNotFoundException;
import com.swd.gym_face_id_access.service.CustomerMembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
