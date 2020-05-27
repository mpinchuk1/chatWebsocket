package com.company.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonManagedReference
    private CustomUser owner;
    @ManyToMany(mappedBy = "rooms")
    @JsonIgnore
    private List<CustomUser> roomMembers = new ArrayList<>();
    @OneToMany(mappedBy = "to", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonBackReference
    private List<ChatMessage> roomMessages = new ArrayList<>();
    private Boolean isSingleUserRoom;

    public ChatRoom() {
    }

    public ChatRoom(String name) {
        this.name = name;
        this.isSingleUserRoom = false;
    }

    public void addUserToRoom(CustomUser user){
        roomMembers.add(user);
        user.getRooms().add(this);
    }

    public void deleteUserFromRoom(CustomUser delUser){
        roomMembers.remove(delUser);
        delUser.getRooms().remove(this);
    }

    public void addMessage(ChatMessage message){
        roomMessages.add(message);
        message.setTo(this);
    }

    public CustomUser getOwner(){
        return owner;
    }
    @JsonIgnore
    public int getNumberOfMembers(){
        return roomMembers.size();
    }

    public void setOwner(CustomUser owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CustomUser> getRoomMembers() {
        return roomMembers;
    }

    public void setRoomMembers(List<CustomUser> roomMembers) {
        this.roomMembers = roomMembers;
    }

    public List<ChatMessage> getRoomMessages() {
        return roomMessages;
    }

    public void setRoomMessages(List<ChatMessage> roomMessages) {
        this.roomMessages = roomMessages;
    }

    public Boolean isSingleUserRoom() {
        return isSingleUserRoom;
    }

    public void setIsSingleUserRoom(Boolean flag) {
        isSingleUserRoom = flag;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owner=" + owner +
                '}';
    }
}
