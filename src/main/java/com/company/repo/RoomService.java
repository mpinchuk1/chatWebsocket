package com.company.repo;

import com.company.entities.ChatRoom;
import com.company.entities.CustomUser;
import com.company.entities.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public List<ChatRoom> getAllRooms() {
        return roomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ChatRoom findByID(Long id) {
        return roomRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean addRoom(String name) {
        if (roomRepository.existsByName(name))
            return false;
        ChatRoom newRoom = new ChatRoom(name);
        roomRepository.save(newRoom);
        return true;
    }

}
