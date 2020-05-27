package com.company.controller;

import com.company.entities.ChatRoom;
import com.company.entities.CustomUser;
import com.company.repo.RoomService;
import com.company.repo.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);
    @Autowired
    private RoomService roomService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/addNewRoom", method = RequestMethod.POST)
    public ResponseEntity<Void> addNewRoom(@RequestParam String roomName, @RequestParam String ownerName){

        if(roomService.addRoom(roomName, ownerName, false)){
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

//    @RequestMapping(value = "/joinToRoom", method = RequestMethod.POST)
//    public ResponseEntity<Void> joinToRoom(@RequestParam String to,
//                                           @RequestParam String userName){
//        ChatRoom room = roomService.findByName(to);
//        if(room != null){
//            CustomUser user = userService.findByLogin(userName);
//            room.addUserToRoom(user);
//            return ResponseEntity.ok().build();
//        }else {
//            return ResponseEntity.badRequest().build();
//        }
//    }

//    @RequestMapping(value = "/leaveFromRoom", method = RequestMethod.POST)
//    public ResponseEntity<Void> leaveFromRoom(@RequestParam String from,
//                                           @RequestParam String userName){
//        ChatRoom room = roomService.findByName(from);
//        if(room != null){
//            CustomUser user = userService.findByLogin(userName);
//            room.deleteUserFromRoom(user);
//            return ResponseEntity.ok().build();
//        }else {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    @GetMapping("/fetchAllRooms")
    public List<ChatRoom> fetchAllRooms() {

        return roomService.getAllRooms();
    }

    @RequestMapping(value = "/getRoomInfo", method = RequestMethod.POST)
    public ChatRoom setOnlineState(@RequestParam String roomName){
        return roomService.findByName(roomName);
    }
}
