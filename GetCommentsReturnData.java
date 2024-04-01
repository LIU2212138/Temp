package com.projectgroup.project.ReturnData;

import com.projectgroup.project.Model.Comment;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCommentsReturnData {
    int code;
    String message = "";

    commentsData data;

    public GetCommentsReturnData(int code, String message, List<CommentCardData> array){
        this.code = code;
        this.message = message;
        data = new commentsData(array.size(), array);
    }
    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class commentsData{
        int total;
        List<CommentCardData> array;
    }
    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentCardData {
        int id;
        String username;
        String detail;
        String content;
        String img;
        int like_cnt;
        boolean liked;
        String time;
    }
}
