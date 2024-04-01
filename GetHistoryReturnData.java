package com.projectgroup.project.ReturnData;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetHistoryReturnData {
    int code;
    String message;
    Data data;

    @lombok.Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Goods {
        int total;
        Card[] array;
    }

    @lombok.Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Card {
        int id;
        String title;
        String publisher;
        String content;
    }

    @lombok.Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Posts {
        int total;
        Card[] array;


    }


    @lombok.Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        Goods goods;
        Posts posts;

    }

}