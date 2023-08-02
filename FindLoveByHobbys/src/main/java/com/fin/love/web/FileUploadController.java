package com.fin.love.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/profile")
public class FileUploadController {
	
	@GetMapping("/profileimage")
	public String image(Model model) {
		log.info("imageUploadPage()");
		
		return "/profile/image_upload";
	}
	
	
	
}
