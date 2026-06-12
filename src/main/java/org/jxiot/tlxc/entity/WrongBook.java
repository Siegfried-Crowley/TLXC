package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;

@Data
public class WrongBook {
    private Integer id;
    private Integer userId;
    private Integer problemId;
    private Integer lastSubmissionId;
    private String status;
    private String note;
    private Integer wrongCount;
    private Date createdAt;
    private Date updatedAt;
}
