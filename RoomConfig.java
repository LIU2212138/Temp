package com.projectgroup.project.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomConfig {
    private String name;
    private String password;
    private String owner;
    private String ownerImg;
    private boolean needPassword;
    private int limit;
    private int currentSize;
}
