package com.example.prj1be20231109.domain;

import lombok.Data;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Data
public class Member {
    private String id;
    private String password;
    private String email;
    private LocalDateTime inserted;


}
