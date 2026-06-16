package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;

@Data
public class UserFollow {
    private Integer id;
    private Integer followerId;
    private Integer followingId;
    private Date createdAt;

    // transient
    private String followingUsername;
    private String followingNickname;
    private Integer followingTotalPoints;
}
