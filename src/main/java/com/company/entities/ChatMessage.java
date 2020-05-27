package com.company.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.dialect.CUBRIDDialect;
import org.hibernate.loader.custom.CustomQuery;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    @JsonManagedReference
    private ChatRoom to;
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Europe/Kiev")
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", sender=" + sender +
                ", to=" + to +
                ", content='" + content + '\'' +
                ", date=" + date +
                '}';
    }
}
