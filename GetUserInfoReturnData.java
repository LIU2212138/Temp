package com.projectgroup.project.ReturnData;

import com.projectgroup.project.Model.UserInfo;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserInfoReturnData {
    int code;
    String message = "";

    Data data;

    public GetUserInfoReturnData(int code, String message, String username, String img,
                                 String grade, String department, String major, String introduction,
                                 String college, String email, String gender){
        this.code = code;
        this.message = message;
        this.data = new Data(username, img, grade, department, major, introduction, college, email, gender);
    }
    @lombok.Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Data {
        String username = "";
        String img = "";
        String grade = "";
        String department = "";
        String major = "";
        String introduction = "";
        String college = "";
        String email = "";
        String gender = "";
//        String state = "";
    }
}
