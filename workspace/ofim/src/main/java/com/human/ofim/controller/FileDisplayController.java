package com.human.ofim.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FileDisplayController {

    // 업로드된 기본 경로 (AuthService.java에 설정된 경로와 동일해야 함)
    private final String uploadPath = "D:/ofim_uploads/";

    @GetMapping("/display")
    public ResponseEntity<byte[]> getFile(@RequestParam("fileName") String fileName) {
        ResponseEntity<byte[]> result = null;
        try {
            // D:/ofim_uploads/ + partner_docs/business/어쩌구.jpg
            File file = new File(uploadPath + fileName);
            
            HttpHeaders header = new HttpHeaders();
            // 파일의 확장자에 따라 브라우저에 보낼 MIME 타입 설정 (이미지 띄우기)
            header.add("Content-Type", Files.probeContentType(file.toPath()));
            
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
}