package com.swd.gym_face_id_access.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class MembershipResponse {
    private int id;
    private String name;
    private String type;
    private String description;
    private Integer trainingDay;
    private Integer duration;
    private Integer price;
    private String slotTimeType;
    private List<SlotTimeResponse> slotTimeResponses;
}
