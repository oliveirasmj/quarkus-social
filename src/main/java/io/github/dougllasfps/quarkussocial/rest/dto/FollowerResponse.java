package io.github.dougllasfps.quarkussocial.rest.dto;

import io.github.dougllasfps.quarkussocial.domain.model.Follower;
import lombok.Data;

@Data
public class FollowerResponse {

    private long id;
    private String name;

    public FollowerResponse() {
    }

    public FollowerResponse(Follower follower) {
        this(follower.getId(), follower.getFollower().getName());
    }

    public FollowerResponse(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
