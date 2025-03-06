package com.swd.gym_face_id_access.repository;

import com.swd.gym_face_id_access.model.SlotTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SlotTimeRepository extends JpaRepository<SlotTime, Integer> {

    @Query("select s from SlotTime s where s.membership.id = :membershipId")
    List<SlotTime> findByMembershipId(int membershipId);
}