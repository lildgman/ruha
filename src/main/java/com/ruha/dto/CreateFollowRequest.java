package com.ruha.dto;

import com.ruha.entity.Member;
import lombok.Data;

@Data
public class CreateFollowRequest {

    private Member follower;
    private Member following;

    public CreateFollowRequest(Member follower, Member following) {
        this.follower = follower;
        this.following = following;
    }
}
