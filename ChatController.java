package com.projectgroup.project.Controller;

import com.projectgroup.project.ReturnData.BasicReturnData;
import com.projectgroup.project.ReturnData.GetCommentsReturnData;
import com.projectgroup.project.ReturnData.Result;
import com.projectgroup.project.Model.Room;
import com.projectgroup.project.Model.RoomConfig;
import com.projectgroup.project.Service.UserService;
import com.projectgroup.project.util.ChatHandler;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contact")
public class ChatController {
    @Resource
    UserService userService;
    final ChatHandler chatHandler;

    public ChatController(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @GetMapping(value = "/",produces = "application/json;charset=utf-8")
    public Result<List<RoomConfig>> welcome(@RequestParam("username") String username,
                                            @CookieValue("login-cookie") String cookie) {
        if (!userService.checkCookie(username, cookie)) {
            return Result.error(402,"Cookie Invalid");
        }
        return Result.ok(ChatHandler.rooms.values().stream()
                .map(Room::getConfig)
                .collect(Collectors.toList()));
    }


    @PostMapping(value = "/create",produces = "application/json;charset=utf-8")
    public Result<?> createRoom(@RequestParam("username") String username,
                                @CookieValue("login-cookie") String cookie,
                                @RequestBody RoomConfig config) {
        if (!userService.checkCookie(username, cookie)) {
            return Result.error(402,"Cookie Invalid");
        }
        System.out.println(config);
        Room room = new Room(config);
        ChatHandler.rooms.put(config.getName(), room);

        return Result.ok(null);
    }
    @RequestMapping(value = "/check-name", method = RequestMethod.GET)
    public BasicReturnData checkName(@RequestParam(value = "room-name") String roomName,
                                     @RequestParam(value = "username",defaultValue = "") String username,
                                     @CookieValue(value = "login-cookie" , defaultValue = "") String cookie) {
        if (!userService.checkCookie(username, cookie)) {
            return new BasicReturnData(402, "Cookie Invalid");
        }
        for (String room : ChatHandler.rooms.keySet()){
            if (room.equals(roomName)){
                return new BasicReturnData(-1,"Duplicate roomName",null);
            }
        }
        return new BasicReturnData(0,"OK",null);
    }
}
