package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;

@Data
public class UserProfile {
    private Integer id;
    private Integer userId;
    private String bio;
    private String avatarUrl;
    private String githubUrl;
    private String websiteUrl;
    private String organization;
    private String location;
    private Integer acceptedProblems;
    private Integer totalSubmissions;
    private java.math.BigDecimal acceptanceRate;
    private Date updatedAt;
}
