package com.ndev.privchat.privchat.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

@Entity
@Table(name = "chat_requests")
public class ChatRequest {
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID uuid;
    @Column
    private String sender;
    @Column
    private String receiver;
    @CreationTimestamp
    @Column(name="created_at")
    private Date createdAt;
}