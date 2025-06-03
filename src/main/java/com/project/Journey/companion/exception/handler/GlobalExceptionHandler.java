package com.project.Journey.companion.exception.handler;

import com.project.Journey.companion.exception.PostException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //PostException 처리
    @ExceptionHandler(PostException.class)
    public ResponseEntity<String> handlePostException(PostException e){
        return ResponseEntity.status(e.getStatus())
                .body(e.getMessage());
    }

    //NullPointerException 및 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 오류가 발생했습니다: "+e.getMessage());
    }
}
