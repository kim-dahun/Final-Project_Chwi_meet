package com.fin.love.web;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fin.love.dto.facechat.MakeFaceChatRoomDto;

import com.fin.love.dto.facechat.ReportFaceChatDto;
import com.fin.love.respository.member.Member;
import com.fin.love.service.FaceChatService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/facechat")
@Controller
@Slf4j
public class FaceChatController {

	@Autowired
	private FaceChatService faceservice;

	// 화상채팅 방으로 입장하는 메서드
	@PostMapping("/room")
	public String facechatroom(Model model, MakeFaceChatRoomDto dto, HttpSession session) {
		log.info("dto = {}", dto);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userid = authentication.getName();

		long roomId = dto.getRoomId();
		int result = faceservice.makeRoom(dto);
		if (result == 0) {
			// 이미 신고된 방이라 연결이 안되는 경우 다시 채팅방으로. 주소는 나중에 수정 필요 TODO
			return "redirect:/facechat/chatroom";

		}
		List<Member> list = faceservice.loadMemberName(dto);

		for (Member x : list) {
			// TODO 로그인 하면 여기에 아이디 담겨야 함.
			if (x.getId().equals(userid)) {

				model.addAttribute("speakmember1", x.getName());
				model.addAttribute("speak1", x.getId());
			} else {

				model.addAttribute("speakmember2", x.getName());
				model.addAttribute("speak2", x.getId());
			}

		}

		model.addAttribute("roomId", roomId);

		log.info("facechatroom(roomId = {})", roomId);
		return "/facechat/room";
	}

	@GetMapping("/room")
	public String facechatroom(MakeFaceChatRoomDto dto, Model model) {

		log.info("dto = {}", dto);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userid = authentication.getName();
		
		List<Member> list = faceservice.loadMemberName(dto);

		for (Member x : list) {
			// TODO 로그인 하면 여기에 아이디 담겨야 함.
			if (x.getId().equals(userid)) {

				model.addAttribute("speakmember1", x.getName());

			} else {

				model.addAttribute("speakmember2", x.getName());

			}

		}
		
		model.addAttribute("roomId",dto.getRoomId());

		return "/facechat/room";
		
	}

	// 신고 처리 메서드
	@PostMapping("/report")
	public String facechatreport(@RequestParam String audios, ReportFaceChatDto dto, HttpSession session) {
		log.info("doReport({})", dto);
		log.info("{}", audios);
		dto.setAudios(audios);

		faceservice.doReport(dto);

		return "redirect:/chat/chat";

	}

	// 리폿 당했을 경우 강제로 사이트 이동
	@GetMapping("/report")
	public String facechatreport() {

		log.info("report 당함");

		return "redirect:/chat/chat";

	}

}
