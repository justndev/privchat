package com.ndev.privchat.privchat.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserDto {
    private String nickname;
    private String password;
}
