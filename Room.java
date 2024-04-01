package com.projectgroup.project.Model;

import com.projectgroup.project.Model.ChatContent;
import com.projectgroup.project.Model.RoomConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Room {
    private final RoomConfig config;
    private final Map<String,String> users = new ConcurrentHashMap<>();



    public Room(RoomConfig config){
        this.config = config;
    }

    public Set<String> getUsers() {
        return users.keySet();
    }

    public List<String> getUUIDs(){
        return new ArrayList<>(users.values());
    }



    public boolean judgePass( String password) {
        return  (!this.config.isNeedPassword() || password.equals(this.config.getPassword()));
    }

    public void doRegister(String user,String uuid){
        users.put(user, uuid);
        config.setCurrentSize(config.getCurrentSize()+1);
    }


    public void removeUser(String user){
        users.remove(user);
    }



    public RoomConfig getConfig() {
        return config;
    }
}
