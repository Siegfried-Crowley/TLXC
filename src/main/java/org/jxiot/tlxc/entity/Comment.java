package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Comment {
    private Integer id;
    private Integer discussionId;
    private Integer userId;
    private Integer parentId;
    private String content;
    private Integer likeCount;
    private Date createdAt;

    // transient
    private String username;
    private String nickname;
    private List<Comment> replies;
}
