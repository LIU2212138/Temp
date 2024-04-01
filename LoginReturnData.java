package com.projectgroup.project.ReturnData;

import com.projectgroup.project.Model.User;
import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginReturnData implements Serializable {
    Integer code;
    String message = "";
    Data data;

    public LoginReturnData(Integer code, String message,
                           String userName, int priority, int id,
                           String img, String cookie) {
        this.code = code;
        this.message = message;
        this.data = new Data(userName, priority, id, img, cookie);
    }
    public LoginReturnData(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.data = new Data();
    }

    @lombok.Data
    @Getter
    @Setter
    @NoArgsConstructor
    private static class Data{
        String username = "";
        int priority;
        int info_id;
        String img = "";
        String cookie = "";
        public Data(String userName, int priority, int id, String img, String cookie) {
            this.username = userName;
            this.priority = priority;
            this.info_id = id;
            this.img = img;
            this.cookie = cookie;
        }
    }
}
