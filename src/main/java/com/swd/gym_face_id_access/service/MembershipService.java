package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.dto.request.CreateMembershipRequest;
import com.swd.gym_face_id_access.dto.response.MembershipResponse;

import java.util.List;

public interface MembershipService {
    List<MembershipResponse> getAllMemberships();

    String addMembership(CreateMembershipRequest createMembershipRequest);

    String deleteMembership(int membershipId);
}
