package com.swd.gym_face_id_access.repository;

import com.swd.gym_face_id_access.model.CustomerMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerMembershipRepository extends JpaRepository<CustomerMembership, Integer> {
    @Query("SELECT cm FROM CustomerMembership cm WHERE cm.customer.id = :customerId AND cm.endDate >= CURRENT_DATE AND cm.sessionCounter > 0")
    List<CustomerMembership> findActiveMemberships(@Param("customerId") int customerId);

    CustomerMembership findById(int id);
}
