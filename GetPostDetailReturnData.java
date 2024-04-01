package com.projectgroup.project.ReturnData;

import com.projectgroup.project.Model.Post;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetPostDetailReturnData {
    int code;
    String message = "";
    PostDetail data;
    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostDetail{

        int id;
        boolean liked;
        boolean stared;
        String title;
        String username;
        String head;
        String detail;
        String content;
        String time;
        int comment_cnt;
        int like_cnt;
        int star_cnt;

    }
}
