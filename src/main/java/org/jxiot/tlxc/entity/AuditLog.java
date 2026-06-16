package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;

@Data
public class AuditLog {
    private Integer id;
    private Integer adminId;
    private String action;
    private String targetType;
    private Integer targetId;
    private String detail;
    private Date createdAt;
}
