package com.swd.gym_face_id_access.dto.response;


import lombok.Data;
import java.time.LocalTime;
@Data
public class SlotTimeResponse {
    private String slotTimeType;
    private LocalTime startTime;
    private LocalTime endTime;
}
