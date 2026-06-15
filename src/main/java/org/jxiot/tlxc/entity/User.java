package org.jxiot.tlxc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Integer id;
    private String username;

    @JsonIgnore
    private String hashedPassword;

    private String nickname;
    private String role;
    private String status;
    private Integer totalPoints;
    private Integer streakDays;
    private Date lastPassDate;
    private Date lastLoginAt;
    private Date createdAt;
}
