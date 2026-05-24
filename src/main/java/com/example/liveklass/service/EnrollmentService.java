package com.example.liveklass.service;

import java.util.List;

import com.example.liveklass.dto.EnrollmentDto;
import com.example.liveklass.dto.LectureDto;
import com.example.liveklass.dto.PageInfoDto;

public interface EnrollmentService {
	
	// 신청 가능한 강의 목록 조회
	List<LectureDto> selectLectureListForEnroll();
	
	// 내 수강신청 목록 조회(페이징)
	int selectMyEnrollmentListCount();
	List<EnrollmentDto> selectMyEnrollmentList(PageInfoDto pi);
	
	// 수강 신청
	int insertEnrollment(EnrollmentDto enroll);

	// 결제 확정
    int confirmEnrollment(EnrollmentDto enroll);

    // 수강 취소
    int cancelEnrollment(EnrollmentDto enroll);

}
