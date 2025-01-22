package com.ndev.privchat.privchat.dtos;


import com.ndev.privchat.privchat.entities.MessageRecord;
import com.ndev.privchat.privchat.entities.RegistrationRecord;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Builder
@Setter
@Getter
public class AppDataDto {
    private int currentUsersAmount;
    private List<MessageRecord> messages;
    private List<RegistrationRecord> registrations;
}
