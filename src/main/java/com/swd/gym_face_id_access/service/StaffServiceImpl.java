package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.configuration.JwtUtil;
import com.swd.gym_face_id_access.dto.response.StaffResponse;
import com.swd.gym_face_id_access.exception.UnauthorizedException;
import com.swd.gym_face_id_access.model.Enum.Roles;
import com.swd.gym_face_id_access.model.Staff;
import com.swd.gym_face_id_access.repository.StaffRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;

    private final JwtUtil jwtUtil;

    private final HttpServletRequest request;

    @Override
    public List<StaffResponse> getAllStaff() {
        String token = jwtUtil.getCurrentToken(request);

        if(!jwtUtil.extractRole(token).equals(Roles.ADMIN)) {
            throw new UnauthorizedException("Unauthorized access");
        }

        List<Staff> staffList = staffRepository.findAllByOrderByStatusDesc();
        List<StaffResponse> staffResponseList = new ArrayList<>();
        for(Staff staff : staffList) {
            StaffResponse staffResponse = new StaffResponse();
            staffResponse.setStaffId(staff.getId());
            staffResponse.setStaffName(staff.getFullName());
            staffResponse.setRole(staff.getRole());
            staffResponse.setEmail(staff.getEmail());
            staffResponse.setAccount(staff.getAccount().getUserName());
            staffResponse.setStatus(staff.getStatus());
            staffResponseList.add(staffResponse);
        }
        return staffResponseList;
    }
}
