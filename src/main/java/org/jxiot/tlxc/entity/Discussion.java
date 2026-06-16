package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Discussion {
    private Integer id;
    private Integer problemId;
    private Integer userId;
    private String title;
    private String content;
    private String type;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isPinned;
    private Date createdAt;
    private Date updatedAt;

    // transient
    private String username;
    private String nickname;
    private List<Comment> comments;
}
