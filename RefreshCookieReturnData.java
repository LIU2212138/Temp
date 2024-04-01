package com.projectgroup.project.ReturnData;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshCookieReturnData {
    int code;
    String message = "";
    Data data;
    public RefreshCookieReturnData(int code, String message, String username,
                                   int priority, int info_id, String img, String cookie) {
        this.code = code;
        this.message = message;
        this.data = new Data(username, priority, info_id, img, cookie);

    }
    public RefreshCookieReturnData(int code, String message) {
        this.code = code;
        this.message = message;
    }
    @lombok.Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Data {
        String username = "";
        int priority;
        int info_id;
        String img = "";
        String cookie = "";
    }
}
