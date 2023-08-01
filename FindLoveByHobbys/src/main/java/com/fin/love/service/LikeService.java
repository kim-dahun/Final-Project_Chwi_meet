package com.fin.love.service;

import org.springframework.stereotype.Service;

import com.fin.love.repository.like.Like;
import com.fin.love.repository.like.LikeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
	
	private final LikeRepository likeRepository;

	public int likeSend(String userId, String memberId) {
		log.info("likeSend(userId = {}, memberId = {})", userId, memberId);
		
		int result = 0; // 0이면 실패, 1이면 성공
		
		Like like = likeRepository.findBySenderAndRecipient(userId, memberId);
		
		if (like != null) {
			result = 1;
		}
		
		return result;
	}
	
}