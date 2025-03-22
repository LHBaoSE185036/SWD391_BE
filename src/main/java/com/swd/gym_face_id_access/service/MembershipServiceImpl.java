package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.dto.request.CreateMembershipRequest;
import com.swd.gym_face_id_access.dto.response.MembershipResponse;
import com.swd.gym_face_id_access.dto.response.SlotTimeResponse;
import com.swd.gym_face_id_access.exception.MembershipNotFoundException;
import com.swd.gym_face_id_access.model.Membership;
import com.swd.gym_face_id_access.model.SlotTime;
import com.swd.gym_face_id_access.repository.MembershipRepository;
import com.swd.gym_face_id_access.repository.SlotTimeRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;

    private final SlotTimeRepository slotTimeRepository;

    @Override
    public List<MembershipResponse> getAllMemberships() {

        List<Membership> memberships = membershipRepository.findAll();
        List<MembershipResponse> membershipResponses = new ArrayList<>();
        for (Membership membership : memberships) {
            MembershipResponse membershipResponse = new MembershipResponse();
            membershipResponse.setId(membership.getId());
            membershipResponse.setName(membership.getName());
            membershipResponse.setType(membership.getType());
            membershipResponse.setDescription(membership.getDescription());
            membershipResponse.setTrainingDay(membership.getTrainingDay());
            membershipResponse.setDuration(membership.getDuration());
            membershipResponse.setPrice(membership.getPrice());
            membershipResponse.setStatus(membership.getStatus());
            membershipResponse.setSlotTimeType(membership.getSlotTimeType());
            List<SlotTime> slotTimes = slotTimeRepository.findByMembershipId(membership.getId());
            List<SlotTimeResponse> slotTimeResponses = new ArrayList<>();
            for (SlotTime slotTime : slotTimes) {
                SlotTimeResponse slotTimeResponse = new SlotTimeResponse();
                slotTimeResponse.setSlotTimeType(slotTime.getSlotTimeType());
                slotTimeResponse.setStartTime(slotTime.getStartTime());
                slotTimeResponse.setEndTime(slotTime.getEndTime());
                slotTimeResponses.add(slotTimeResponse);
            }
            membershipResponse.setSlotTimeResponses(slotTimeResponses);
            membershipResponses.add(membershipResponse);
        }
        return membershipResponses;
    }

    @Override
    public String addMembership(CreateMembershipRequest createMembershipRequest) {

        Membership membership = getMembership(createMembershipRequest);
        membershipRepository.save(membership);

        List<CreateMembershipRequest.SlotTimeRequest> slotTimeRequests = createMembershipRequest.getSlotTimeRequest();
        for (CreateMembershipRequest.SlotTimeRequest slotTimeRequest : slotTimeRequests) {
            addSlotTime(slotTimeRequest, membership);
        }



        return "Added Membership Successfully";
    }

    @Override
    public String deleteMembership(int membershipId) {
        if(!membershipRepository.existsById(membershipId)){
            throw new MembershipNotFoundException("Membership not found");
        }
        Membership membership = membershipRepository.findById(membershipId).get();
        membership.setStatus("inactive");
        membershipRepository.save(membership);
        return "Deleted Membership Successfully";
    }

    private static Membership getMembership(CreateMembershipRequest createMembershipRequest) {
        Membership membership = new Membership();
        membership.setName(createMembershipRequest.getName());
        membership.setType(createMembershipRequest.getType());
        membership.setDescription(createMembershipRequest.getDescription());
        membership.setTrainingDay(createMembershipRequest.getTrainingDay());
        membership.setDuration(createMembershipRequest.getDuration());
        membership.setSlotTimeType(createMembershipRequest.getSlotTimeType());
        membership.setPrice(createMembershipRequest.getPrice());
        membership.setStatus("active");
        return membership;
    }

    private void addSlotTime(CreateMembershipRequest.SlotTimeRequest slotTimeRequest, Membership membership) {
        SlotTime slotTime = new SlotTime();
        LocalTime startTime = LocalTime.parse(slotTimeRequest.getStartTime());
        LocalTime endTime = LocalTime.parse(slotTimeRequest.getEndTime());
        slotTime.setSlotTimeType(slotTimeRequest.getSlotTimeType());
        slotTime.setStartTime(startTime);
        slotTime.setEndTime(endTime);
        slotTime.setMembership(membership);
        slotTimeRepository.save(slotTime);
    }
}
