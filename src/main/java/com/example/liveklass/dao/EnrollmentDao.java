package com.example.liveklass.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.example.liveklass.dto.EnrollmentDto;
import com.example.liveklass.dto.LectureDto;
import com.example.liveklass.dto.PageInfoDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class EnrollmentDao {
	private final SqlSessionTemplate sqlSessionTemplate;
	
	// 신청 가능한 강의 목록 조회
	public List<LectureDto> selectLectureListForEnroll(){
		return sqlSessionTemplate.selectList("enrollmentMapper.selectLectureListForEnroll");
	}
	
	// 내 수강 신청 목록 조회
	public int selectMyEnrollmentListCount() {
		return sqlSessionTemplate.selectOne("enrollmentMapper.selectMyEnrollmentListCount");
	}
	
	public List<EnrollmentDto> selectMyEnrollmentList(PageInfoDto pi){
		
		int size = pi.getBoardLimit(); 
		int offset = (pi.getCurrentPage() - 1) * size;
		
		Map<String, Object> param = new HashMap<>();
		param.put("offset", offset);
		param.put("size", size);

		return sqlSessionTemplate.selectList("enrollmentMapper.selectMyEnrollmentList", param);
		
		
	}
	
	// 기존에 취소한 수강 내역이 있는지 조회
	public int checkCancelledEnrollment(EnrollmentDto enroll) {
        return sqlSessionTemplate.selectOne("enrollmentMapper.checkCancelledEnrollment", enroll);
    }

	
	// 신규 수강 신청
	public int insertEnrollment(EnrollmentDto enroll) {
		return  sqlSessionTemplate.insert("enrollmentMapper.insertEnrollment", enroll);
	}
	
	
	// 수강 상태 변경
	public int updateEnrollmentStatus(EnrollmentDto enroll) {
		return sqlSessionTemplate.update("enrollmentMapper.updateEnrollmentStatus", enroll);
	}
	
	// 수강 인원 증가
	public int increaseCurEnrollCnt(int lecId) {
		return sqlSessionTemplate.update("enrollmentMapper.increaseCurEnrollCnt", lecId);
	}
	
	// 수강 인원 감소
	public int decreaseCurEnrollCnt(int lecId) {
		return sqlSessionTemplate.update("enrollmentMapper.decreaseCurEnrollCnt", lecId);
	}
	
	
}
