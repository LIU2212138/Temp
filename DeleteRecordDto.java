package com.projectgroup.project.Dto;

public record DeleteRecordDto(Historys historys, Stars stars) {
    public record Goods(int total,String[] ids) {
    }
    public record Post(int total,String[] ids) {
    }
    public record Stars(Goods goods,Post post) {
    }
    public record Historys(Goods goods,Post post) {
    }
}