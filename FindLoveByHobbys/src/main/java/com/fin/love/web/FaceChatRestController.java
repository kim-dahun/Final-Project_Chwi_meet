package com.fin.love.web;

import java.io.File;
import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/faceapi")
public class FaceChatRestController {

	@PostMapping("/report/{roomId}")
	public ResponseEntity<String> reportFaceChat(@PathVariable long roomId,@RequestBody MultipartFile audioFile) {

		try {
			// 파일 저장 경로 설정
			String savePath = "/audio/report/";
			String fileName = "record-" + roomId + ".wav";
			File file = new File(savePath, fileName);

			// 파일 저장
			audioFile.transferTo(file);
			log.info("{}", file);
			return ResponseEntity.ok(savePath+fileName);

			// 서비스 호출해서 리폿 기록

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.ok("실패");
			

		}

		

	}

}
