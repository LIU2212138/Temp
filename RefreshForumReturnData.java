package com.projectgroup.project.ReturnData;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshForumReturnData {
    int code;
    String message = "";

    Data data;

    @lombok.Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data{
        int total;
        PostCard[] array;
    }
    @lombok.Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostCard {
        int id;
        String detail = "";
        String text = "";
//        String url = "";
        String title = "";
        String username = "";
        int comment_cnt;
        int like_cnt;
        int star_cnt;
        String[] pngs;
        String img = ""; // 用户头像
    }
}
