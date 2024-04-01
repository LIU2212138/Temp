package com.projectgroup.project.Dto;

public record GoodPostDto(String title, String cover,
                          String description, String info, float price, String[] img,
                          String[] tags) {
}
