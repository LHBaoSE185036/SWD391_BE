package com.swd.gym_face_id_access.repository;

import com.swd.gym_face_id_access.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Integer> {
}