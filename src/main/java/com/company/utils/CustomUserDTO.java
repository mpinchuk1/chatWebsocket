package com.company.utils;

public class CustomUserDTO {

    private Long id;
    private String login;
    private UserRole role;
    private UserState state;
    private byte[] img;

    public CustomUserDTO() {

    }

    public CustomUserDTO(Long id, String login, UserRole role) {
        this.id = id;
        this.login = login;
        this.role = role;
        this.state = UserState.OFFLINE;
        //this.img = img;
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

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }
}
