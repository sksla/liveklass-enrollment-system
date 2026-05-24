package com.example.liveklass.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.liveklass.dao.LectureDao;
import com.example.liveklass.dto.LectureDto;
import com.example.liveklass.dto.MemberDto;
import com.example.liveklass.dto.PageInfoDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LectureServiceImpl implements LectureService {

	private final LectureDao lectureDao;

	// 강의 목록 조회(필터링, 페이징)
	@Override
	public List<LectureDto> filterLectureList(String condition, PageInfoDto pi) {
		return lectureDao.filterLectureList(condition, pi);
	}

	@Override
	public int filterLectureListCount(String condition) {
		return lectureDao.filterLectureListCount(condition);
	}

	// 강의 등록
	@Override
	public int insertLecture(LectureDto lec) {
		return lectureDao.insertLecture(lec);
	}

	// 강의 상세조회
	@Override
	public LectureDto selectLecture(int lecId) {
		return lectureDao.selectLecture(lecId);
	}

	@Override
	public List<MemberDto> selectEnrollmentMemberList(int lecId) {
		return lectureDao.selectEnrollmentMemberList(lecId);
	}

	// 강의 상태 변경
	@Override
	public int updateLectureStatus(LectureDto lecture) {
		LectureDto originLecture = lectureDao.selectLecture(lecture.getLecId());
		
		if(originLecture == null) {
		    return 0;
		}
		
	    // 초안 -> 모집중만 허용
		if("DRAFT".equals(originLecture.getStatus())
	            && "OPEN".equals(lecture.getStatus())) {

	        return lectureDao.updateLectureStatus(lecture);
	    }
		
		// 모집중 -> 모집마감만 허용
	    if("OPEN".equals(originLecture.getStatus())
	            && "CLOSED".equals(lecture.getStatus())) {

	        return lectureDao.updateLectureStatus(lecture);
	    }
	    
	    return 2;
	}
	
	



	
	
	
}
