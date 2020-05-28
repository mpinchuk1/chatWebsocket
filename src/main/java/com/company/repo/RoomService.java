package com.company.repo;

import com.company.entities.ChatRoom;
import com.company.entities.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ChatRoom> getAllRooms() {
        return roomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ChatRoom findByID(Long id) {
        return roomRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean addRoom(String name, String ownerName, boolean isSingleUserRoom) {
        if (roomRepository.existsByName(name))
            return false;
        ChatRoom newRoom = new ChatRoom(name);
        CustomUser owner = userRepository.findByLogin(ownerName);
        newRoom.setOwner(owner);

        if(!isSingleUserRoom){
            userRepository.findAll().forEach(newRoom::addUserToRoom);
        }else {
            newRoom.addUserToRoom(owner);
        }

        newRoom.setIsSingleUserRoom(isSingleUserRoom);
        roomRepository.save(newRoom);
        return true;
    }

    @Transactional(readOnly = true)
    public ChatRoom findByName(String name){
        return roomRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name){
        return roomRepository.existsByName(name);
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> findAll(){
        return roomRepository.findAll();
    }

}
