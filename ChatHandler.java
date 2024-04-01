package com.projectgroup.project.util;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.projectgroup.project.Model.ChatContent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectgroup.project.Model.Room;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ChatHandler extends TextWebSocketHandler {
    public static Map<String, WebSocketSession> sshMap = new ConcurrentHashMap<>();

    public static Map<String, Room> rooms = new ConcurrentHashMap<>();
    public static Map<String, Room> roomMap = new ConcurrentHashMap<>();
    public static Map<String, String> uidToUser = new ConcurrentHashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 用户连接上WebSocket的回调
     *
     * @param webSocketSession
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        System.out.println("用户 连接WebSocket");
        String uuid = UUID.randomUUID().toString();
        sshMap.put(uuid, webSocketSession);
        try {
            // 第一次握手
            var content = new ChatContent(0,"","",new ChatContent.Content(0,uuid,""),"","","");
            webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(content)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 收到消息的回调
     *
     * @param webSocketSession
     * @param webSocketMessage
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws IOException {
        if (webSocketMessage instanceof TextMessage){

            System.out.println(webSocketMessage.getPayload());
            ChatContent content = JSON.parseObject(webSocketMessage.getPayload().toString(),ChatContent.class);
            if (content.getType() == 0){
                var uuid = content.getContent().getText();
                String roomName=content.getRoom();
                var room = rooms.get(roomName);
                uidToUser.put(uuid,content.getFrom());
                if (room.judgePass(content.getPassword())&&room.getConfig().getCurrentSize()<room.getConfig().getLimit()){
                    room.doRegister(content.getFrom(), uuid);
                    roomMap.put(uuid,room);
                    var roommates = getRoommateSessions(roomName);
                    for (var a : roommates) {
                        var publicContent = ChatContent.publicContent(roomName, new ChatContent.Content(0, content.getFrom() + " 加入讨论间", ""));
                        a.sendMessage(new TextMessage(objectMapper.writeValueAsString(publicContent)));

                    }
                    return;
                }
                else {
                    sshMap.remove(uuid);
                    webSocketSession.sendMessage(new TextMessage("加入失败"));
                    webSocketSession.close();
                    return;
                }
            }
            var roommates = getRoommateSessions(content.getRoom());
            for (var a : roommates) {
                TextMessage message;
                if (content.getType() == 1){
                    message = (TextMessage)webSocketMessage;
                }else {
                    message = new TextMessage("未实现的类型");
                }
                a.sendMessage(message);
            }
        }
    }

    /**
     * 出现错误的回调
     * @param webSocketSession
     * @param throwable
     */
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {

    }

    /**
     * 连接关闭的回调
     *
     * @param webSocketSession
     * @param closeStatus
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws IOException {

        for (String i:sshMap.keySet()){
            if (sshMap.get(i).equals(webSocketSession)){

                sshMap.remove(i);
                Room room;
                if ((room= roomMap.get(i))!=null)
                {
                    String roomName=room.getConfig().getName();
                    String username=uidToUser.get(i);
                    room.removeUser(username);
                    room.getConfig().setCurrentSize(room.getConfig().getCurrentSize() - 1);
                    roomMap.remove(i);
                    uidToUser.remove(i);

                    for (var a : getRoommateSessions(roomName)) {
                        var publicContent = ChatContent.publicContent(roomName, new ChatContent.Content(0, username + " 退出讨论间", ""));
                        a.sendMessage(new TextMessage(objectMapper.writeValueAsString(publicContent)));

                    }
                }
                System.out.println("用户断开连接");
                return;
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


    public static List<WebSocketSession> getRoommateSessions(String name){
        return rooms.get(name)
                .getUUIDs()
                .stream()
                .map(i -> sshMap.get(i))
                .collect(Collectors.toList());
    }

}
