package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.dto.request.CreateStaffRequest;
import com.swd.gym_face_id_access.dto.response.StaffResponse;

import java.util.List;

public interface StaffService {

    List<StaffResponse> getAllStaff();

    String addStaff(CreateStaffRequest createStaffRequest);

    String disableStaff(int staffId);
}
