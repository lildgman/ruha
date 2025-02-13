package com.ruha.dto;

import com.ruha.entity.Member;
import lombok.Data;

@Data
public class CreateFollowRequest {

    private Long followerId;
    private Long followingId;

}
