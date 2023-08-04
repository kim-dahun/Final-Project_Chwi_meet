package com.fin.love.respository.member;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface MemberRepository extends JpaRepository<Member, String> {
	

	// Facechat에서 사용하는 메서드. 삭제하지 마세요~
	@Query(value = "select * from USERINFO where id = :speakmember1 or id = :speakmember2", nativeQuery = true)
	List<Member> findMyNameForFacechat(@Param(value = "speakmember1") String speakmember1, @Param(value = "speakmember2") String speakmember2);
	
	

	List<Member> findBySex(int sexCode);


}
