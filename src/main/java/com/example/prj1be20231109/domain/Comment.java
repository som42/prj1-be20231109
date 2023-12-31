package com.example.prj1be20231109.domain;

import com.example.prj1be20231109.util.AppUtil;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer id;
    private Integer boardId;
    private String memberId;
    private String memberNickName;
    private String comment;
    private LocalDateTime inserted;

    public String getAgo(){
        return AppUtil.getAgo(inserted);
    }
}
