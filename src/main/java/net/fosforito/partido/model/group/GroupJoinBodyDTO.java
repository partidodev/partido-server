package net.fosforito.partido.model.group;

import javax.validation.constraints.NotNull;

public class GroupJoinBodyDTO {

    @NotNull
    private Long userId;

    @NotNull
    private String joinKey;


    public GroupJoinBodyDTO() {
    }

    public GroupJoinBodyDTO(Long userId, String joinKey) {
        this.userId = userId;
        this.joinKey = joinKey;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getJoinKey() {
        return joinKey;
    }

    public void setJoinKey(String joinKey) {
        this.joinKey = joinKey;
    }
}
