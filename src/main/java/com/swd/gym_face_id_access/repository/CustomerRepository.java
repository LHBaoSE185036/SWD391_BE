package com.swd.gym_face_id_access.repository;

import com.swd.gym_face_id_access.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    List<Customer> findAllByFaceFeatureIsNotNull();

    Customer getById(int id);

    Customer getByFaceFeature(String faceFeature);
}