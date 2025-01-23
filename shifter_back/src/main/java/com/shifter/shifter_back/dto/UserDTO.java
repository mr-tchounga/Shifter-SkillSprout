package com.shifter.shifter_back.dto;

import lombok.Data;

@Data
public class UserDTO {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String picture;
    private String score;
}
