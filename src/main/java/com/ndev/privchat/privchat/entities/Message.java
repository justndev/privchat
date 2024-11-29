package com.ndev.privchat.privchat.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID uuid;
    @Column
    private String content;
    @CreationTimestamp
    @Column(nullable = false, name="created_at")
    private Date createdAt;
    @Column
    private String sender;
    @Column
    private String receiver;
}
