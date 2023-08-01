package com.fin.love.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ProfileController {
	
	@GetMapping("/profile")
	public String home(Model model) {
		log.info("home()");
		
		return "/profile/profile";
	}
}