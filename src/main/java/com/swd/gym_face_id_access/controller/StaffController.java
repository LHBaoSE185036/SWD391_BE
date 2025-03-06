package com.swd.gym_face_id_access.controller;

import com.swd.gym_face_id_access.dto.response.ApiResponse;
import com.swd.gym_face_id_access.dto.response.StaffResponse;
import com.swd.gym_face_id_access.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gym-face-id-access/api/v1/staff")
public class StaffController {
    private final StaffService staffService;

    @GetMapping("/staffs")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getAllStaffs() {
            return ResponseEntity.ok().body(ApiResponse.<List<StaffResponse>>builder()
                            .errorCode(null)
                            .message("Success")
                            .data(staffService.getAllStaff())
                            .success(true)
                    .build());

    }
}
