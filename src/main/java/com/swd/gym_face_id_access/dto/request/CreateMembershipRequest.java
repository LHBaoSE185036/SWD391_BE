package com.swd.gym_face_id_access.dto.request;

import lombok.Data;


import java.util.List;

@Data
public class CreateMembershipRequest {
    private String name;
    private String type;
    private String description;
    private Integer trainingDay;
    private Integer duration;
    private Integer price;
    private String slotTimeType;
    private List<SlotTimeRequest> slotTimeRequest;

    @Data
    public static class SlotTimeRequest {
        private String slotTimeType;
        private String startTime;
        private String endTime;

    }
}
