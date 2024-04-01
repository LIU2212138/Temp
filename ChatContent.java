package com.projectgroup.project.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatContent {
    private int type;

    /** type | 取值协议 | content内容
     *  0: 握手， <- UUID
     *  1: 正文， <- 聊天文本 ->
     *  2: 广播， 广播消息内容 ->
     */


    private String from;
    private String head;
    private Content content;
    private String room;
    String data;
    String password;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Content {
        int type;
        String text;
        String img;
    }


    public static ChatContent publicContent(String roomName,Content message){
        return new ChatContent(2,"null","null",message,roomName,"null","null");
    }


}
