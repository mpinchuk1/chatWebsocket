package com.company.entities;

import org.hibernate.dialect.CUBRIDDialect;
import org.hibernate.loader.custom.CustomQuery;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    private CustomUser sender;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "toRoom_id", nullable = false)
    private ChatRoom to;
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public ChatMessage() {
    }

    public ChatMessage(CustomUser sender, ChatRoom to, String content) {
        this.sender = sender;
        this.to = to;
        this.content = content;
        this.date = new Date();
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CustomUser getSender() {
        return sender;
    }

    public void setSender(CustomUser sender) {
        this.sender = sender;
    }

    public ChatRoom getTo() {
        return to;
    }

    public void setTo(ChatRoom to) {
        this.to = to;
    }


}
