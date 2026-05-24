package com.example.liveklass.service;

import java.util.List;

import com.example.liveklass.dto.LectureDto;
import com.example.liveklass.dto.MemberDto;
import com.example.liveklass.dto.PageInfoDto;

public interface LectureService {
	
	// 강의 목록 조회(전체, 필터링 포함)
	List<LectureDto> filterLectureList(String condition, PageInfoDto pi);
	int filterLectureListCount(String condition);
	
	// 강의 등록하기 서비스
	int insertLecture(LectureDto lec);
	
	// 강의 상세조회 서비스
	LectureDto selectLecture(int lecId);
	List<MemberDto> selectEnrollmentMemberList(int lecId); // 수강생목록 조회
	
	// 강의 상태 변경 서비스
	int updateLectureStatus(LectureDto lecture);
	
	
}
