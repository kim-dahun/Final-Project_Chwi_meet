package com.fin.love.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fin.love.dto.question.CreateQuestDto;
import com.fin.love.dto.question.UpdateQuestDto;
import com.fin.love.dto.questionRep.QuestRepCreatetDto;
import com.fin.love.repository.question.Question;
import com.fin.love.repository.questreply.QuestionReply;
import com.fin.love.respository.member.Member;
import com.fin.love.service.QuestReplyService;
import com.fin.love.service.QuestionService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/question")
public class QuestionController {
	
	@Autowired
	private QuestReplyService questReplyService;
	
	@Autowired
	private QuestionService questionservice;

	@GetMapping("/qscreate")
	public void qscreate(HttpSession session, Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String userid = authentication.getName();
		
		Member member = questionservice.readbyUserId(userid);
		
		model.addAttribute("user", member);
		
		log.info("qscreate()");

	}

	// TODO 나중에 리스트 페이지 완성 시 변경 필요함.
		@PostMapping("/qscreate")
		public String qscreate(CreateQuestDto dto) {

			log.info("qscreate({})", dto);

			questionservice.write(dto);
	    
			return "redirect:/question/qslist";

		}

		@GetMapping("/qsmodify")
		public void qsmodify(@RequestParam long numid, Model model) {

			log.info("qsmodify()");

			Question quest = readAndModify(numid);

			model.addAttribute("quest", quest);
		}

	@PostMapping("/qsmodify")
	public String qsmodify(UpdateQuestDto dto) {

		log.info("qsmodify(update={})", dto);

		questionservice.update(dto);

		return "redirect:/question/qsread?id="+dto.getNumid();
	}

	@GetMapping("/qslist")
	public void qslist(Model model, HttpSession session) {
		
		log.info("qslist()");
		
		// TODO 로그인 하면 여기에 아이디 담겨야 함.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String userid = authentication.getName();
		
		List<Question> list2 = questionservice.read(userid);
		List<Question> list = new ArrayList<>();
		int count = 0;
		for (Question x : list2) {

			if (count == 3) {

				break;

			} else {
				list.add(x);
			}
			count++;
		}
		model.addAttribute("userid", userid);
		model.addAttribute("list", list);

	}

	@GetMapping("/qsread")
	public void read(@RequestParam long id, Model model) {

		log.info("qsread(id={})", id);
		
		Question quest = readAndModify(id);
		String role = quest.getMember().getRole().toString();
		log.info("role=({})",role);
		int roles = 0;
		switch(role) {
		
		case "USER":
			roles=0;
			break;
		case "UNVARIFIED_USER":
			roles=1;
			break;
		case "RIP_USER":
			roles=2;
			break;
		case "ADMIN":
			roles=3;
			break;
			
		}
		QuestionReply reply = questReplyService.findbyQuestionId(id);
		
		if(quest.getStatus()==2) {
			
			log.info("qreply=({})",reply);
			
		} else {
			
			reply = new QuestionReply();
			
		}
		
		
		
		model.addAttribute("reply", reply);
		log.info("roles=({})",roles);
		model.addAttribute("quest", quest);
		model.addAttribute("role", roles);
	}


	
	public Question readAndModify(long id) {

		return questionservice.readbyId(id);

	}

}
