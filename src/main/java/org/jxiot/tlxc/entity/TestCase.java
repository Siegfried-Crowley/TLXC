package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;

@Data
public class TestCase {
    private Integer id;
    private Integer problemId;
    private String inputData;
    private String expectedOutput;
    private Boolean isHidden;
    private Integer scoreWeight;
    private String remark;
    private Date updatedAt;
}
