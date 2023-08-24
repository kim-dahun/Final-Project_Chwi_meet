package com.fin.love.service.chatting;

import com.fin.love.dto.chatting.ChattingListDto;
import com.fin.love.repository.chat.ChattingRoom;
import com.fin.love.repository.chat.ChattingRoomRepository;
import com.fin.love.respository.member.Member;
import com.fin.love.respository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChattingRoomService {

    private final ChattingRoomRepository chattingRoomRepository;
    private final MemberRepository memberRepository;

    public List<ChattingListDto> getChattingRoomListById(String userId, int sex) {

        List<ChattingRoom> list = new ArrayList<>();

        // userId에 해당하는 채팅방 정보 가져오기
        if (sex == 1) {
            list = chattingRoomRepository.findByMaleid(userId);
        } else {
            list = chattingRoomRepository.findByFemaleid(userId);
        }

        //
        List<ChattingListDto> dtoList = new ArrayList<>();

        // 채팅방 정보에서 상대방 ID, 상대방과 연결된 채팅방 번호 가져오기
        for (ChattingRoom entity : list) {
            if (sex == 1) {
                ChattingListDto dto = new ChattingListDto(entity.getContentid(), entity.getFemaleid());
                dtoList.add(dto);
            } else {
                ChattingListDto dto = new ChattingListDto(entity.getContentid(), entity.getMaleid());
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        chattingRoomRepository.deleteById(roomId);
    }

    public void makeChattingRoomm(String senderId, String user) {
        Member sender = memberRepository.findById(senderId).orElseThrow();
        if (sender.getSex() == 1) {
            chattingRoomRepository.save(ChattingRoom.builder()
                    .maleid(senderId)
                    .femaleid(user)
                    .build());
        } else {
            chattingRoomRepository.save(ChattingRoom.builder()
                    .maleid(user)
                    .femaleid(senderId)
                    .build());
        }
    }
}
