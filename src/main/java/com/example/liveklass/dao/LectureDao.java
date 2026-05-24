package com.example.liveklass.dao;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.example.liveklass.dto.LectureDto;
import com.example.liveklass.dto.MemberDto;
import com.example.liveklass.dto.PageInfoDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class LectureDao {
	private final SqlSessionTemplate sqlSessionTemplate;
	
	// 강사의 강의 목록 조회
	public int filterLectureListCount(String condition){
		
		return sqlSessionTemplate.selectOne("lectureMapper.filterLectureListCount", condition);
	}
	
	public List<LectureDto> filterLectureList(String condition, PageInfoDto pi){
		int limit = pi.getBoardLimit();
		int offset = (pi.getCurrentPage() - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		return sqlSessionTemplate.selectList("lectureMapper.filterLectureList", condition);
	}
	
	// 강의 등록
	public int insertLecture(LectureDto lec) {
		return sqlSessionTemplate.insert("lectureMapper.insertLecture", lec);
	}
	
	// 강의 상세 조회
	public LectureDto selectLecture(int lecId) {
		return sqlSessionTemplate.selectOne("lectureMapper.selectLecture", lecId);
	}
	
	public List<MemberDto> selectEnrollmentMemberList(int lecId){
		return sqlSessionTemplate.selectList("lectureMapper.selectEnrollmentMemberList", lecId);
	}
	
	// 강의 상태 변경
	public int updateLectureStatus(LectureDto lecture) {
		return sqlSessionTemplate.update("lectureMapper.updateLectureStatus", lecture);
	}

}
