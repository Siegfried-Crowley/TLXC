package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;

@Data
public class UserFavorite {
    private Integer id;
    private Integer userId;
    private Integer problemId;
    private Date createdAt;

    // transient
    private String problemTitle;
    private String problemDifficulty;
}
