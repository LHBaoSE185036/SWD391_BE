package com.swd.gym_face_id_access.repository;

import com.swd.gym_face_id_access.model.CheckInLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInLogRepository extends JpaRepository<CheckInLog, Integer> {
}
