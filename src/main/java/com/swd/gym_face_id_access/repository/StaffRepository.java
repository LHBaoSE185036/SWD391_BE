package com.swd.gym_face_id_access.repository;

import com.swd.gym_face_id_access.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, Integer> {

    @Query("select s from Staff s where s.account.id = :id")
    Staff findByAccountId(int id);

    List<Staff> findAllByOrderByStatusAsc();

    List<Staff> findAllByOrderByStatusDesc();
}