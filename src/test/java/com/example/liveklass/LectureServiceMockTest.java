package com.example.liveklass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.liveklass.dao.LectureDao;
import com.example.liveklass.dto.LectureDto;
import com.example.liveklass.dto.MemberDto;
import com.example.liveklass.dto.PageInfoDto;
import com.example.liveklass.service.LectureServiceImpl;

@ExtendWith(MockitoExtension.class)
//Mockito를 사용할 수 있게 해주는 설정
//"가짜 객체(Mock)를 사용해서 테스트할거야!" 라고 선언하는 느낌
public class LectureServiceMockTest {

 @Mock
 private LectureDao lectureDao;
 // 가짜 LectureDao 생성
 // 실제 DB 연결 없이 동작함

 @InjectMocks
 private LectureServiceImpl lectureService;
 // 진짜 Service 객체 생성
 // 단, 내부의 lectureDao에는 위에서 만든 "가짜 Dao"가 자동 주입됨

 private LectureDto lecture;
 private PageInfoDto pi;

 @BeforeEach
 void setUp() {

     // 매 테스트 전에 공통 데이터 생성

     lecture = new LectureDto();
     lecture.setLecId(1);
     lecture.setStatus("DRAFT");

     pi = new PageInfoDto();
 }

 // =========================================================
 // 강의 목록 조회 테스트
 // =========================================================

 @Test
 @DisplayName("강의 목록 조회 성공")
 // 테스트 이름
 void filterLectureList_성공() {

     // given ==========================
     // "이런 상황이야!" 를 세팅하는 단계

     when(lectureDao.filterLectureList("OPEN", pi))
             .thenReturn(new ArrayList<>());
     // lectureDao.filterLectureList()가 호출되면
     // 빈 리스트를 반환하도록 가짜 동작 정의

     // when ===========================
     // 실제 테스트할 메소드 실행

     int resultSize = lectureService
             .filterLectureList("OPEN", pi)
             .size();

     // then ===========================
     // 결과 검증

     assertEquals(0, resultSize);
     // 결과 리스트 크기가 0인지 확인

     verify(lectureDao).filterLectureList("OPEN", pi);
     // lectureDao.filterLectureList()가 실제로 호출됐는지 검증
 }

 // =========================================================
 // 강의 목록 개수 조회 테스트
 // =========================================================

 @Test
 @DisplayName("강의 목록 개수 조회 성공")
 void filterLectureListCount_성공() {

     // given
     when(lectureDao.filterLectureListCount("OPEN"))
             .thenReturn(5);
     // count 조회 시 5 반환하도록 설정

     // when
     int result = lectureService
             .filterLectureListCount("OPEN");

     // then
     assertEquals(5, result);
     // 실제 결과가 5인지 확인

     verify(lectureDao).filterLectureListCount("OPEN");
     // Dao 메소드 호출 여부 확인
 }

 // =========================================================
 // 강의 등록 테스트
 // =========================================================

 @Test
 @DisplayName("강의 등록 성공")
 void insertLecture_성공() {

     // given
     when(lectureDao.insertLecture(lecture))
             .thenReturn(1);
     // insert 성공 시 1 반환하도록 설정

     // when
     int result = lectureService.insertLecture(lecture);

     // then
     assertEquals(1, result);
     // 성공 결과인지 확인

     verify(lectureDao).insertLecture(lecture);
     // insert 호출 여부 확인
 }

 // =========================================================
 // 강의 상세 조회 테스트
 // =========================================================

 @Test
 @DisplayName("강의 상세 조회 성공")
 void selectLecture_성공() {

     // given
     when(lectureDao.selectLecture(1))
             .thenReturn(lecture);
     // 1번 강의 조회 시 lecture 객체 반환

     // when
     LectureDto result = lectureService.selectLecture(1);

     // then
     assertEquals(1, result.getLecId());
     // 조회된 강의 번호가 1인지 확인

     verify(lectureDao).selectLecture(1);
     // Dao 호출 여부 확인
 }

 // =========================================================
 // 수강 신청 회원 목록 조회 테스트
 // =========================================================

 @Test
 @DisplayName("수강 신청 회원 목록 조회 성공")
 void selectEnrollmentMemberList_성공() {

     // given
     when(lectureDao.selectEnrollmentMemberList(1))
             .thenReturn(new ArrayList<MemberDto>());
     // 빈 회원 리스트 반환하도록 설정

     // when
     int resultSize = lectureService
             .selectEnrollmentMemberList(1)
             .size();

     // then
     assertEquals(0, resultSize);

     verify(lectureDao).selectEnrollmentMemberList(1);
 }

 // =========================================================
 // 강의 상태 변경 테스트
 // =========================================================

 @Test
 @DisplayName("강의 상태 변경 성공 - DRAFT -> OPEN")
 void updateLectureStatus_DRAFT_OPEN_성공() {

     // 기존 DB 상태를 흉내낸 객체
     LectureDto originLecture = new LectureDto();
     originLecture.setLecId(1);
     originLecture.setStatus("DRAFT");

     // 사용자가 변경 요청한 상태
     LectureDto requestLecture = new LectureDto();
     requestLecture.setLecId(1);
     requestLecture.setStatus("OPEN");

     // given
     when(lectureDao.selectLecture(1))
             .thenReturn(originLecture);
     // DB 조회 시 DRAFT 상태 반환

     when(lectureDao.updateLectureStatus(requestLecture))
             .thenReturn(1);
     // 상태 변경 성공 시 1 반환

     // when
     int result = lectureService
             .updateLectureStatus(requestLecture);

     // then
     assertEquals(1, result);
     // 성공 결과 확인

     verify(lectureDao).updateLectureStatus(requestLecture);
     // update 호출 여부 확인
 }

 // =========================================================
 // OPEN -> CLOSED 성공 테스트
 // =========================================================

 @Test
 @DisplayName("강의 상태 변경 성공 - OPEN -> CLOSED")
 void updateLectureStatus_OPEN_CLOSED_성공() {

     LectureDto originLecture = new LectureDto();
     originLecture.setLecId(1);
     originLecture.setStatus("OPEN");

     LectureDto requestLecture = new LectureDto();
     requestLecture.setLecId(1);
     requestLecture.setStatus("CLOSED");

     // given
     when(lectureDao.selectLecture(1))
             .thenReturn(originLecture);

     when(lectureDao.updateLectureStatus(requestLecture))
             .thenReturn(1);

     // when
     int result = lectureService
             .updateLectureStatus(requestLecture);

     // then
     assertEquals(1, result);

     verify(lectureDao).updateLectureStatus(requestLecture);
 }

 // =========================================================
 // 잘못된 상태 변경 테스트
 // =========================================================

 @Test
 @DisplayName("강의 상태 변경 실패 - 잘못된 상태 변경")
 void updateLectureStatus_잘못된상태변경_실패() {

     // DRAFT -> CLOSED 는 허용되지 않는 상황

     LectureDto originLecture = new LectureDto();
     originLecture.setLecId(1);
     originLecture.setStatus("DRAFT");

     LectureDto requestLecture = new LectureDto();
     requestLecture.setLecId(1);
     requestLecture.setStatus("CLOSED");

     // given
     when(lectureDao.selectLecture(1))
             .thenReturn(originLecture);

     // when
     int result = lectureService
             .updateLectureStatus(requestLecture);

     // then
     assertEquals(2, result);
     // Service에서 잘못된 상태 변경이면 2 반환

     verify(lectureDao, never())
             .updateLectureStatus(requestLecture);
     // update가 호출되면 안 됨
     // never() = "절대 호출되지 않았어야 함"
 }

 // =========================================================
 // 존재하지 않는 강의 테스트
 // =========================================================

 @Test
 @DisplayName("강의 상태 변경 실패 - 존재하지 않는 강의")
 void updateLectureStatus_존재하지않는강의_실패() {

     LectureDto requestLecture = new LectureDto();
     requestLecture.setLecId(999);
     requestLecture.setStatus("OPEN");

     // given
     when(lectureDao.selectLecture(999))
             .thenReturn(null);
     // DB 조회 결과 없음

     // when
     int result = lectureService
             .updateLectureStatus(requestLecture);

     // then
     assertEquals(0, result);
     // 존재하지 않는 강의면 0 반환

     verify(lectureDao, never())
             .updateLectureStatus(requestLecture);
     // update 호출되면 안 됨
 }
}
