package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Problem {
    private Integer id;
    private String title;
    private String difficulty;
    private String tags;
    private String description;
    private String inputDescription;
    private String outputDescription;
    private String examples;
    private String hint;
    private String constraints;
    private String source;
    private String status;
    private Integer timeLimitMs;
    private Integer memoryLimitMb;
    private Integer createdBy;
    private String starterCode;
    private Boolean isActive;
    private Date createdAt;
    private Date updatedAt;

    // Transient fields for tag system
    private List<Tag> tagList;
    private List<Integer> tagIds;

    /**
     * @return supported languages for this problem, comma-separated, default "python"
     */
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getAllowedLanguages() {
        return "python,java,cpp,javascript";
    }
}
