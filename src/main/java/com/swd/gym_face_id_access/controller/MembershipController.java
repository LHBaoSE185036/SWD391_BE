package com.swd.gym_face_id_access.controller;

import com.swd.gym_face_id_access.dto.request.CreateMembershipRequest;
import com.swd.gym_face_id_access.dto.response.ApiResponse;
import com.swd.gym_face_id_access.dto.response.MembershipResponse;
import com.swd.gym_face_id_access.exception.MembershipNotFoundException;
import com.swd.gym_face_id_access.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gym-face-id-access/api/v1/membership")
public class MembershipController {
    private final MembershipService membershipService;

    @GetMapping("/memberships")
    public ResponseEntity<ApiResponse<List<MembershipResponse>>> getMemberships() {
        try{
            return ResponseEntity.ok().body(ApiResponse.<List<MembershipResponse>>builder()
                            .errorCode(null)
                            .message("Success")
                            .data(membershipService.getAllMemberships())
                            .success(true)
                    .build());
        } catch (Exception e){
            throw new RuntimeException("Failed to get memberships", e);
        }
    }

    @PostMapping("/membership")
    public ResponseEntity<ApiResponse<String>> addMembership(@RequestBody CreateMembershipRequest createMembershipRequest){
        try{
            return ResponseEntity.ok().body(ApiResponse.<String>builder()
                    .errorCode(null)
                    .message("Success")
                    .data(membershipService.addMembership(createMembershipRequest))
                    .success(true)
                    .build());
        } catch (Exception e){
            throw new RuntimeException("Failed to add memberships", e);
        }
    }

    @DeleteMapping("/membership/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMembership(@PathVariable int id){try{
        return ResponseEntity.ok().body(ApiResponse.<String>builder()
                .errorCode(null)
                .message("Success")
                .data(membershipService.deleteMembership(id))
                .success(true)
                .build());
    } catch (MembershipNotFoundException e){
        throw new MembershipNotFoundException(e.getMessage());
    }
    }
}
