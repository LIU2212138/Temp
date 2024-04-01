package com.projectgroup.project.ReturnData;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshGoodForumReturnData {
    int code;
    String message = "";

    RefreshGoodForumReturnData.Data data;

    @lombok.Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data{
        int total;
        RefreshGoodForumReturnData.GoodCard[] array;
    }
    @lombok.Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GoodCard {
        int id;
        float price;
        String title = "";
        String cover = "";
        int comment_cnt;
        int[] tags;

    }
}
