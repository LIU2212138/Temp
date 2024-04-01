package com.projectgroup.project.ReturnData;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetStarReturnData {
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
        GoodCard[] array;
    }

    @lombok.Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GoodCard {
        int id;
        String title;
        String publisher;
        String cover;
        String head;
    }

    @lombok.Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Posts {
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
        String title;
        String publisher;
        String[] imgs;
        String head;
    }

    @lombok.Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        Goods goods;
        Posts post;

    }

}