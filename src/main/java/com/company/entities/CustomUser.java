package com.company.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class CustomUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String login;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Enumerated(EnumType.STRING)
    private UserState state;
    private String email;
    private String phone;
    @Lob
    //@Column(name = "photo", columnDefinition = "MEDIUMBLOB")
    private byte[] img;
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<ChatMessage> messages = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_room",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id"))
    @JsonBackReference
    private List<ChatRoom> rooms = new ArrayList<>();

    public CustomUser() {

    }

    public CustomUser(String login, String password, UserRole role,
                      String email, String phone) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.state = UserState.OFFLINE;
        this.email = email;
        this.phone = phone;
        //this.img = img;
    }

    public void addMessage(ChatMessage message){
        messages.add(message);
        message.setSender(this);
    }

    public void addToRoom(ChatRoom room){
        rooms.add(room);
        room.getRoomMembers().add(this);
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    @JsonIgnore
    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public List<ChatRoom> getRooms() {
        return rooms;
    }

    @JsonIgnore
    public ChatRoom getUserPrivateRoom(){
        return rooms.get(0);
    }

    public void setRooms(List<ChatRoom> rooms) {
        this.rooms = rooms;
    }

}
