package com.example.liveklass.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.liveklass.dao.EnrollmentDao;
import com.example.liveklass.dao.LectureDao;
import com.example.liveklass.dto.EnrollmentDto;
import com.example.liveklass.dto.LectureDto;
import com.example.liveklass.dto.PageInfoDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EnrollmentServiceImpl implements EnrollmentService {

	private final EnrollmentDao enrollmentDao;
	private final LectureDao lectureDao;
	
	// 신청 가능한 강의 목록 조회
	@Override
	public List<LectureDto> selectLectureListForEnroll() {
		return enrollmentDao.selectLectureListForEnroll();
	}

	// 내 수강신청 목록 조회(페이징)
	@Override
	public int selectMyEnrollmentListCount() {
		return enrollmentDao.selectMyEnrollmentListCount();
	}

	@Override
	public List<EnrollmentDto> selectMyEnrollmentList(PageInfoDto pi) {
		return enrollmentDao.selectMyEnrollmentList(pi);
	}

	// 수강 신청
	@Transactional
	@Override
	public int insertEnrollment(EnrollmentDto enroll) {
		
		// 정원 체크 제거 (PENDING 단계에서는 cur_enrollment_cnt 변경 없음)
	    // 결제 확정 시점에 DB 레벨로 정원 초과를 막음
		
		// 1. 기존 취소 내역 있는지 조회
        int count = enrollmentDao.checkCancelledEnrollment(enroll);
        
        if (count > 0) { // 2-1.중복(이미 취소 내역이 존재)
        	
        	enroll.setEnrollStatus("PENDING"); // 수강 상태를 "PENDING"으로 update
        	return enrollmentDao.updateEnrollmentStatus(enroll);
        	
    	}else { // 2-2.신규 수강 신청
    		try {
                return enrollmentDao.insertEnrollment(enroll);
            } catch (DuplicateKeyException e) {
                return 4; // 이미 신청 중인 강의 (중복 신청) => 동일인이 동시에 두 번 신청 요청을 보내는 경우
            }
    	}
		
	}

	// 결제 확정
	@Transactional
	@Override
	public int confirmEnrollment(EnrollmentDto enroll) {
		
		// 수강 인원 증가 시도 
	    int increaseResult = enrollmentDao.increaseCurEnrollCnt(enroll.getLecId());
		
	    if (increaseResult == 0) {
	    	return 3; // 정원 초과 (다른 트랜잭션이 먼저 자리를 차지함)
	    }
	    
	    // 2. 결제 확정으로 상태 변경
	    enroll.setEnrollStatus("CONFIRMED");
	    int confirmResult = enrollmentDao.updateEnrollmentStatus(enroll);
		
		if(confirmResult == 0) {
			// 상태 변경 실패 시 인원 수 롤백
			// @Transactional이 있으므로 예외를 던지면 자동 롤백됨
			throw new RuntimeException("수강 상태 변경 실패");
		}
		
		// 3. 정원이 꽉 찼으면 강의 상태를 Closed로 변경
		// 3-1. 강의 상세조회
		LectureDto lecture = lectureDao.selectLecture(enroll.getLecId());
		
		// 3-2. 현재 인원수랑 정원 인원수 비교
		if(lecture.getCurrentEnrollmentCount() >= lecture.getCapacity()) {
			// 정원이 찼다면 강의 상태도 변경
			lecture.setStatus("CLOSED");
			int lecStatusResult = lectureDao.updateLectureStatus(lecture);
			
			return confirmResult * increaseResult * lecStatusResult;
			
		}
	    		
		return confirmResult * increaseResult;
	}

	// 수강 취소
	@Transactional
	@Override
	public int cancelEnrollment(EnrollmentDto enroll) {
		
		String originStatus = enroll.getEnrollStatus();
		
		// 1. 결제 확정 상태일 때 결제일로부터 7일이 지나지 않았는지 확인
		if("CONFIRMED".equals(originStatus)) {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	        LocalDateTime confirmedAt = LocalDateTime.parse(enroll.getConfirmedAt(), formatter);
	        LocalDateTime now = LocalDateTime.now();

	        if (now.isAfter(confirmedAt.plusDays(7))) {
	            return 3; // 결제 취소 기간 지남
	        }
	    }
		
		// 2. 수강 취소(상태 변경)
		enroll.setEnrollStatus("CANCELLED");
		int result = enrollmentDao.updateEnrollmentStatus(enroll);
		
		// 3. 결제 확정 상태에서 취소한 경우 => 수강 인원 수 감소
		if("CONFIRMED".equals(originStatus)) {
		
			int decreaseResult = enrollmentDao.decreaseCurEnrollCnt(enroll.getLecId());
			
			result = result * decreaseResult;
		}
				
		return result;
	}
	
	
	
	
	

}
