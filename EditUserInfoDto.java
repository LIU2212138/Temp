package com.projectgroup.project.Dto;

public record EditUserInfoDto(String major,
                              String gender, String college,
                              String grade, String department,
                              String email, String introduction,
                              int id) {
}
