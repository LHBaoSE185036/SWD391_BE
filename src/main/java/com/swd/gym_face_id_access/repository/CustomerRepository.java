package com.swd.gym_face_id_access.repository;

import com.swd.gym_face_id_access.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    List<Customer> findAllByFaceFeatureIsNotNull();

    Customer getById(int id);

    @Query("select c from Customer c where c.fullName like %:name%")
    List<Customer> findByName(String name);

    Customer getByFaceFeature(String faceFeature);

    @Query("select c from Customer c where c.presentStatus = true")
    List<Customer> findByPresentStatus();
}