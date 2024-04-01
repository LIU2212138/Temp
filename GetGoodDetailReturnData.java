package com.projectgroup.project.ReturnData;

import lombok.*;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetGoodDetailReturnData {
    int code;
    String message = "";
    GetGoodDetailReturnData.GoodDetail data;
    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GoodDetail{

        int id;
        boolean liked;
        boolean stared;
        String title;
        String owner;
        String head;
        String detail;
        String description;
        String time;
        int comment_count;
        int like_count;
        int star_count;
        float price;
        String cover;
        String[] img;
        String info;
    }
}
