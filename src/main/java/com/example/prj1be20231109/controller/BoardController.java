package com.example.prj1be20231109.controller;

import com.example.prj1be20231109.domain.Board;
import com.example.prj1be20231109.domain.Member;
import com.example.prj1be20231109.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor //주의 받을게 있으면
@RequestMapping("/api/board")
public class BoardController {

    //    스프링이 주입해 돌라고 final로 만듬.
    private final BoardService service;

    //post 방식으로 /api/board는 클래스 레벨에 적어놨으니 add만 적어 주면 된다.
    @PostMapping("add")
//    응답 코드를 줄 수 있는 ResponseEntity 을 썼다.
    public ResponseEntity add(@RequestBody Board board,
                              @SessionAttribute(value = "login", required = false) Member login) {
        System.out.println("login = " + login);

        if (login ==  null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401번 로그인 안하면
        }

        if (!service.validate(board)) { // 우리가 원하는 값이 잘들어와 있는지 서비스에 검증해보자이
            return ResponseEntity.badRequest().build(); // 400번 클라이언트가 요청이 잘못되었을때
        }

        if (service.save(board, login)) {
            return ResponseEntity.ok().build(); // 글이 저장이 잘됬으면
        } else {
            return ResponseEntity.internalServerError().build(); // 500번 서버문제
        }
    }

    // /api/board/list?p=6
    @GetMapping("list")
    public List<Board> list(@RequestParam(value = "p", defaultValue = "1") Integer page) {
        return service.list(page);
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
    public ResponseEntity edit(@RequestBody Board board,
                               @SessionAttribute(value = "login", required = false) Member login) {
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