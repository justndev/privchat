package com.ndev.privchat.privchat.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDataDto {
    String userAgent;
    String platform;
    String screenWidth;
    String screenHeight;
    String language;
    String timezone;
}
