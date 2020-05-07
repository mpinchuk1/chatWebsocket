package com.company.entities;

import javax.persistence.*;

@Entity
public class CustomUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String login;
    @Column
    private String password;
    @Column
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Column
    @Enumerated(EnumType.STRING)
    private UserState state;
    @Column
    private String email;
    @Column
    private String phone;
    @Lob
    //@Column(name = "photo", columnDefinition = "MEDIUMBLOB")
    private byte[] img;

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
}
