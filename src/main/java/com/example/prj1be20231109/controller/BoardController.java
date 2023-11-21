package com.example.prj1be20231109.controller;

import com.example.prj1be20231109.domain.Board;
import com.example.prj1be20231109.domain.Member;
import com.example.prj1be20231109.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.font.MultipleMaster;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor //주의 받을게 있으면
@RequestMapping("/api/board")
public class BoardController {

    //    스프링이 주입해 돌라고 final로 만듬.
    private final BoardService service;

    //post 방식으로 /api/board는 클래스 레벨에 적어놨으니 add만 적어 주면 된다.
    @PostMapping("add")
//    응답 코드를 줄 수 있는 ResponseEntity 을 썼다.
    public ResponseEntity add (Board board,
                              @RequestParam(value = "uploadFiles[]", required = false) MultipartFile[] files,
                              @SessionAttribute(value = "login", required = false) Member login) throws IOException {

        if (login ==  null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401번 로그인 안하면
        }

        if (!service.validate(board)) { // 우리가 원하는 값이 잘들어와 있는지 서비스에 검증해보자이
            return ResponseEntity.badRequest().build(); // 400번 클라이언트가 요청이 잘못되었을때
        }

        if (service.save(board, files, login)) {
            return ResponseEntity.ok().build(); // 글이 저장이 잘됬으면
        } else {
            return ResponseEntity.internalServerError().build(); // 500번 서버문제
        }
    }

    // /api/board/list?p=6
    // /api/board/list?k=java
    @GetMapping("list")
    public Map<String, Object> list(@RequestParam(value = "p", defaultValue = "1") Integer page,
                                    @RequestParam(value = "k", defaultValue = "") String keyword) {
        // 요청 넘어 왔으면 서비스에 일 시키면 된다.
        return service.list(page, keyword);
    }

    @GetMapping("id/{id}")
    public Board get(@PathVariable Integer id) {
        return service.get(id);
    }

    @DeleteMapping("remove/{id}")
    public ResponseEntity<String> remove(@PathVariable Integer id,
                                 @SessionAttribute(value = "login", required = false) Member login) {
//        로그아웃때 삭제 할려구 할때 아에 권한이 없다.
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 로그인안했을때
        }
//        니가 누군지 알지만 권한이 없다.
        if (!service.hasAccess(id, login)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 권한없을때
        }

        if (service.remove(id)) {
            //잘됬으면 이거
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build(); // 서버문제
        }
    }

    @PutMapping("edit")
    public ResponseEntity edit(Board board,
                               @RequestParam(value = "removeFileIds[]", required = false)List<Integer> removeFileIds,
                               @RequestParam(value = "uploadFiles[]", required = false) MultipartFile[] uploadFiles,
                               @SessionAttribute(value = "login", required = false) Member login) {

        System.out.println("board = " + board);
        System.out.println("removeFileIds = " + removeFileIds);
        System.out.println("uploadFiles = " + uploadFiles);

        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!service.hasAccess(board.getId(),login)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

//        System.out.println("board = " + board);
        if (service.validate(board)) {
            if (service.update(board)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}