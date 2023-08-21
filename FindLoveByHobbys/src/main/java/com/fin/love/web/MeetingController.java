package com.fin.love.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fin.love.dto.meeting.MeetingMakeDto;
import com.fin.love.dto.meeting.MeetingModifyDto;
import com.fin.love.dto.meeting.MeetingReadDto;
import com.fin.love.repository.hobby.Hobby;
import com.fin.love.repository.location.Location;
import com.fin.love.repository.meeting.Meeting;
import com.fin.love.repository.meetingmember.MeetingMember;
import com.fin.love.repository.profile.Age;
import com.fin.love.service.MeetingService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/meeting")
public class MeetingController {

	@Autowired
	private MeetingService meetingservice;

	@GetMapping("/meetinglist")
	public void readlist(@RequestParam(defaultValue = "1") int pagenum, Model model) {

		log.info("meetinglist()");

		List<Meeting> list = meetingservice.makelist(pagenum);

		model.addAttribute("list", list);
		model.addAttribute("pagenum", pagenum);
	}

	@GetMapping("/create")
	public void meetcreate(Model model) {

		List<Hobby> list = meetingservice.loadhobby();
		List<Location> list2 = meetingservice.loadloc();

		String iconimg = meetingservice.imageToBase64("C:/IMA/icon.png");

		model.addAttribute("list", list);
		model.addAttribute("list2", list2);

		model.addAttribute("icon", iconimg);

		log.info("meetcreate()");

	}

	@PostMapping("/create")
	public String meetcreate(MeetingMakeDto dto, HttpSession session) {

		log.info("meetmake({})", dto);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userid = authentication.getName();

		dto.setLeader(userid);

		meetingservice.create(dto);

		return "redirect:/meeting/meetinglist?pagenum=1";

	}

	@GetMapping("/modify")
	public void modify(@RequestParam long id, Model model) {

		List<Hobby> list = meetingservice.loadhobby();
		List<Location> list2 = meetingservice.loadloc();
		model.addAttribute("list", list);
		model.addAttribute("list2", list2);
		String iconimg = meetingservice.imageToBase64("C:/IMA/icon.png");
		MeetingModifyDto meet = meetingservice.readMyMeeting(id);
		model.addAttribute("icon", iconimg);
		model.addAttribute("meet", meet);

	}

	@PostMapping("/modify")
	public String modify(MeetingMakeDto dto, @RequestParam long id) {
		log.info("update(dto = {})", dto);

		meetingservice.update(dto, id);

		return "redirect:/meeting/mymeeting";

	}

	@PostMapping("/delete")
	public String delete(@RequestParam long deleteId) {

		log.info("delete(id = {})", deleteId);

		int result = meetingservice.delete(deleteId);
		if (result == 1) {
			
			log.info("삭제 성공");
		} else {
			log.info("삭제 실패");
			
		}
		return "redirect:/meeting/mymeeting";

	}
	

	@GetMapping("/mymeeting")
	public void mylist(HttpSession session, Model model, @RequestParam(defaultValue = "0") int status) {

		log.info("mymeetinglist()");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userid = authentication.getName();
		List<Meeting> list = meetingservice.myLeaderList(userid,status);
		List<Meeting> list2 = meetingservice.myMeetList(userid,status);

		model.addAttribute("list", list);
		model.addAttribute("list2", list2);
		model.addAttribute("status",status);
	}

	@GetMapping("/read")
	public void read(@RequestParam long id, Model model, HttpSession session) {

		log.info("read(id = {})", id);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userid = authentication.getName();
		List<List<MeetingMember>> list = meetingservice.readMyMember(id);
		MeetingModifyDto meet = meetingservice.readMyMeeting(id);

		int status = meet.getMeeting().getStatus();
		if (userid.equals(meet.getMeeting().getLeader())) {

			status = 1;

		}

		int result = meetingservice.checkInvited(list, userid);

		if (result == 1) {

			model.addAttribute("invite", 1);

		} else {

			model.addAttribute("invite", 0);

		}

		List<MeetingMember> list1 = list.get(1);
		List<MeetingMember> list2 = list.get(0);

		List<String> img1 = new ArrayList<>();
		List<String> img2 = new ArrayList<>();
		try {
			img1 = meetingservice.imagePrint(list1, 1);
			img2 = meetingservice.imagePrint(list2, 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<MeetingReadDto> listman = new ArrayList<>();
		List<MeetingReadDto> listwoman = new ArrayList<>();
		for (int i = 0; i < list1.size(); i++) {
			
			
			MeetingReadDto meetread = MeetingReadDto.FromEntity(img1.get(i), list1.get(i));
			listman.add(meetread);
			
		}

		for (int i = 0; i < list2.size(); i++) {
			
			MeetingReadDto meetread = MeetingReadDto.FromEntity(img2.get(i), list2.get(i));
			listwoman.add(meetread);
			
		}

		log.info("list2 = {}", list2);
		log.info("list1 = {}", list1);

		model.addAttribute("status", status);

		model.addAttribute("meet", meet);
		model.addAttribute("man", listman);
		model.addAttribute("mansize", list1.size());
		model.addAttribute("womansize", list2.size());
		model.addAttribute("woman", listwoman);
		
	}

	@GetMapping("/invite/{invite}")
	public String invite(@PathVariable int invite, @RequestParam long id, HttpSession session) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userid = authentication.getName();
		if (invite == 0) {

			meetingservice.updateAddMember(id, userid);

		} else {

			meetingservice.updateRemove(id, userid);

		}

		return "redirect:/meeting/read?id=" + id;

	}
}
