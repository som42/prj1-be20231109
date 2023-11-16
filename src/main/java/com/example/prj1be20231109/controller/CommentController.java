package com.example.prj1be20231109.controller;

import com.example.prj1be20231109.domain.Comment;
import com.example.prj1be20231109.domain.Member;
import com.example.prj1be20231109.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService service;

    @PostMapping("add")
    public ResponseEntity add(@RequestBody Comment comment,
                              @SessionAttribute(value = "login", required = false) Member login) {

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (service.validate(comment)) {
            if (service.add(comment, login)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }

        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("list")
    public List<Comment> list(@RequestParam("id") Integer boardId){
        return service.list(boardId);
    }

    @DeleteMapping("{id}")
//    댓글 삭제 하기.
    public ResponseEntity<Object> remove(@PathVariable Integer id,
                                         @SessionAttribute(value = "login", required = false)Member login){
        // 어드민 안하고 자기 댓글만 삭제하게 함.
        if (login == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (service.hasAccess(id, login)) {
           if( service.remove(id)){
               return ResponseEntity.ok().build();
           }else {
               return ResponseEntity.internalServerError().build();
           }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("edit")
    public ResponseEntity update(@RequestBody Comment comment,
                       @SessionAttribute(value="login", required = false)Member login){
        if (login == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (service.hasAccess(comment.getId(),login)){
            if (!service.updateValidate(comment)){
                return ResponseEntity.badRequest().build();
            }
            if (service.update(comment)){
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }
        }else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}