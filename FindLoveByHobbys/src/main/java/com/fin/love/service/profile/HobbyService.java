package com.fin.love.service.profile;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fin.love.dto.profile.ProfileUpdateDto;
import com.fin.love.dto.profile.UserHobbyDto;
import com.fin.love.dto.profile.UserHobbyUpdateDto;
import com.fin.love.repository.hobby.Hobby;
import com.fin.love.repository.hobby.HobbyRepository;
import com.fin.love.repository.profile.Profile;
import com.fin.love.repository.profile.UserHobby;
import com.fin.love.repository.profile.UserHobbyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HobbyService {
	
	public final HobbyRepository hobbyRepository;
	public final UserHobbyRepository userHobbyRepository;
	

	@Transactional(readOnly = true)
	public List<Hobby> readHobbyList() {
		
		log.info("readHobbyList()");
		
		return hobbyRepository.findAll();
	}
	
	
	public void hobbySave(UserHobbyDto hobbyDto) {
		log.info("hobbySave(hobbyDto= {})", hobbyDto);
		
		UserHobby uhEntity = hobbyDto.toEntity();
		log.info("uhEntity= {}", uhEntity);
		
		uhEntity = userHobbyRepository.save(uhEntity);
		log.info("userHobby= {}", uhEntity);
	}
	
	
	@Transactional
	public void userHobbyDelete(String userId) {
		log.info("userHobbyDelete(userId={})", userId);
		
		userHobbyRepository.deleteById(userId);
	}


	public List<UserHobby> findById(String userId) {
		log.info("findById(userId = {})", userId);
		
		return userHobbyRepository.findByUserid(userId);
	}

	
	@Transactional
	public void hobbyByIdAllDelete(String userId) {
		log.info("hobbyByIdAllDelete(userId = {})", userId);
		
		userHobbyRepository.deleteByUserid(userId);
	}
	
	@Transactional
	public String findHobbyName(Long hobbyId) {
		log.info("findHobbyName(hobbyId = {})", hobbyId);
		
		return hobbyRepository.findByHobbyId(hobbyId).getHobbyName();
	}
	
//	@Transactional
//	public void userHobbyUpdate(UserHobbyUpdateDto dto) {
//		log.info("userHobbyUpdate(dto={})", dto);
//		
//		UserHobby entity = userHobbyRepository.findById(dto.getUserid()).orElseThrow();
//		entity.userHobbyUpdate(dto);
//	}
	

}
