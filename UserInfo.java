package com.projectgroup.project.Model;


import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo{
    private int id;
    private String major = "";
    private String introduction = "";
    private int grade;
    private String birthday = "";
    private String department = "";
    private String college = "";
    private String email = "";
    private int gender;
}
