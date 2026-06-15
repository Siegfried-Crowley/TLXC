package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Tag {
    private Integer id;
    private String name;
    private String color;
    private Date createdAt;
}
