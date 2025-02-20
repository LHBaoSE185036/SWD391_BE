package com.swd.gym_face_id_access.repository;

import com.swd.gym_face_id_access.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findByUserName(String username);

    Account findByEmail(String email);
}