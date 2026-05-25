package com.example.liveklass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import com.example.liveklass.dao.EnrollmentDao;
import com.example.liveklass.dao.LectureDao;
import com.example.liveklass.dto.EnrollmentDto;
import com.example.liveklass.dto.LectureDto;
import com.example.liveklass.dto.PageInfoDto;
import com.example.liveklass.service.EnrollmentServiceImpl;

@ExtendWith(MockitoExtension.class) // "이 테스트는 Mockito를 사용할거야"
public class EnrollmentServiceMockTest {

    @Mock
    private EnrollmentDao enrollmentDao;
    // 가짜 EnrollmentDao 생성
    // 진짜 DB 연결 없음, 껍데기만 있는 상태

    @Mock
    private LectureDao lectureDao;
    // 가짜 LectureDao 생성


    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;
    // 진짜 Service — 단, 위의 가짜 Dao들을 주입받음
    // 즉, Service는 진짜인데 내부에서 쓰는 Dao는 가짜
    
    private EnrollmentDto enroll;
    private LectureDto lecture;

    // 매 테스트 전에 공통 데이터 세팅 (enroll, lecture 객체)
    @BeforeEach
    void setUp() {
        enroll = new EnrollmentDto();
        enroll.setEnrollId(1);
        enroll.setLecId(100);
        enroll.setMemId(200);

        lecture = new LectureDto();
        lecture.setLecId(100);
        lecture.setCapacity(30);
        lecture.setCurrentEnrollmentCount(10);
    }
    
    // when(메소드).thenReturn(값) => 메소드가 호출되면 값을 반환해
    // 실제 DB조회 없이, 그냥 값을 반환하도록 가짜로 정해두는 것 (가짜 Dao 동작 정의)
    
    // ============= 목록 조회 ===========================
    @Test
    @DisplayName("신청 가능한 강의 목록 조회 성공")
    void selectLectureListForEnroll_성공() {

        // given
        when(enrollmentDao.selectLectureListForEnroll())
                .thenReturn(new ArrayList<>());

        // when
        int resultSize = enrollmentService
                .selectLectureListForEnroll()
                .size();

        // then
        assertEquals(0, resultSize);

        verify(enrollmentDao)
                .selectLectureListForEnroll();
    }
    
    @Test
    @DisplayName("내 수강 신청 목록 개수 조회 성공")
    void selectMyEnrollmentListCount_성공() {

        // given
        when(enrollmentDao.selectMyEnrollmentListCount())
                .thenReturn(5);

        // when
        int result = enrollmentService
                .selectMyEnrollmentListCount();

        // then
        assertEquals(5, result);

        verify(enrollmentDao)
                .selectMyEnrollmentListCount();
    }
    
    @Test
    @DisplayName("내 수강 신청 목록 조회 성공")
    void selectMyEnrollmentList_성공() {

        // given
        PageInfoDto pi = new PageInfoDto();

        when(enrollmentDao.selectMyEnrollmentList(pi))
                .thenReturn(new ArrayList<>());

        // when
        int resultSize = enrollmentService
                .selectMyEnrollmentList(pi)
                .size();

        // then
        assertEquals(0, resultSize);

        verify(enrollmentDao)
                .selectMyEnrollmentList(pi);
    }

    // ===================== 수강 신청 =====================

    @Test
    @DisplayName("수강 신청 성공 - 신규")
    void insertEnrollment_신규_성공() {
    	
    	// given : 상황 설정
        when(enrollmentDao.checkCancelledEnrollment(enroll)).thenReturn(0);
        // "취소 내역 조회하면 0개 나옴" (신규니까)
        
        when(enrollmentDao.insertEnrollment(enroll)).thenReturn(1);
        // "INSERT 하면 성공(1) 반환함"
        
        // when: 실제 실행
        int result = enrollmentService.insertEnrollment(enroll);

        // then: 결과 검증
        assertEquals(1, result);
        // result가 1인지 확인 (성공이면 1)
        
        verify(enrollmentDao).insertEnrollment(enroll);
        // insertEnrollment가 실제로 호출됐는지 확인
    }

    @Test
    @DisplayName("수강 신청 성공 - 취소 후 재신청")
    void insertEnrollment_재신청_성공() {
        when(enrollmentDao.checkCancelledEnrollment(enroll)).thenReturn(1);
        when(enrollmentDao.updateEnrollmentStatus(enroll)).thenReturn(1);

        int result = enrollmentService.insertEnrollment(enroll);

        assertEquals(1, result);
        verify(enrollmentDao).updateEnrollmentStatus(enroll);
        verify(enrollmentDao, never()).insertEnrollment(enroll); // INSERT는 호출 안 됨
    }

    @Test
    @DisplayName("수강 신청 실패 - 중복 신청 (동시 요청)")
    void insertEnrollment_중복신청_실패() {
    	// given
        when(enrollmentDao.checkCancelledEnrollment(enroll)).thenReturn(0);
        // 취소 내역 없음 → 신규 신청 시도하는 상황
        
        when(enrollmentDao.insertEnrollment(enroll)).thenThrow(DuplicateKeyException.class);
        // INSERT 하면 UNIQUE 위반 예외 발생시킴
        // (동시에 두 번 신청한 상황을 흉내냄)
        
        // when
        int result = enrollmentService.insertEnrollment(enroll);

        // then
        assertEquals(4, result);
     // Service에서 DuplicateKeyException 잡아서 4 반환하는지 확인
    }

    // ===================== 결제 확정 =====================

    @Test
    @DisplayName("결제 확정 성공")
    void confirmEnrollment_성공() {

        when(enrollmentDao.increaseCurEnrollCnt(enroll.getLecId())).thenReturn(1);
        when(enrollmentDao.updateEnrollmentStatus(enroll)).thenReturn(1);

        // 추가 - 정원 안 찬 상황 (10/30)
        when(lectureDao.selectLecture(enroll.getLecId())).thenReturn(lecture);
        // lecture는 BeforeEach에서 capacity=30, currentEnrollmentCount=10으로 세팅됨
        // 정원 안 찼으니까 강의 상태 변경 없이 return confirmResult * increaseResult = 1

        int result = enrollmentService.confirmEnrollment(enroll);

        assertEquals(1, result);
    }

    @Test
    @DisplayName("결제 확정 성공 - 정원 마감으로 강의 상태 CLOSED 변경")
    void confirmEnrollment_정원마감_성공() {

        when(enrollmentDao.increaseCurEnrollCnt(enroll.getLecId())).thenReturn(1);
        when(enrollmentDao.updateEnrollmentStatus(enroll)).thenReturn(1);

        // 정원이 꽉 찬 상황 (30/30)
        lecture.setCurrentEnrollmentCount(30);
        when(lectureDao.selectLecture(enroll.getLecId())).thenReturn(lecture);
        when(lectureDao.updateLectureStatus(lecture)).thenReturn(1);

        int result = enrollmentService.confirmEnrollment(enroll);

        assertEquals(1, result); // 1 * 1 * 1 = 1
        verify(lectureDao).updateLectureStatus(lecture); // CLOSED 변경 호출됐는지 확인
    }
    
    @Test
    @DisplayName("결제 확정 실패 - 정원 초과")
    void confirmEnrollment_정원초과_실패() {
    	
    	// given
        when(enrollmentDao.increaseCurEnrollCnt(enroll.getLecId())).thenReturn(0); // 정원 초과로 UPDATE 0건
        // cur_enrollment_cnt 증가 시도했는데 0 반환
        // = DB에서 "capacity 초과라 UPDATE 안했어" 라는 의미
        
        // when
        int result = enrollmentService.confirmEnrollment(enroll);

        // then
        assertEquals(3, result); // 정원 초과 코드 3 반환하는지 확인
        
        verify(enrollmentDao, never()).updateEnrollmentStatus(enroll); // 상태 변경 호출 안 됨
        // 정원 초과니까 상태 변경은 호출되면 안 됨
        // never() = "이 메서드가 절대 호출되지 않았어야 해"
    }

    @Test
    @DisplayName("결제 확정 실패 - 상태 변경 실패 시 RuntimeException")
    void confirmEnrollment_상태변경실패_예외() {
        when(enrollmentDao.increaseCurEnrollCnt(enroll.getLecId())).thenReturn(1);
        when(enrollmentDao.updateEnrollmentStatus(enroll)).thenReturn(0);

        assertThrows(RuntimeException.class, () -> enrollmentService.confirmEnrollment(enroll));

        // 추가 - 예외 터졌으니까 lectureDao는 호출 안 됐어야 함
        verify(lectureDao, never()).selectLecture(anyInt());
        verify(lectureDao, never()).updateLectureStatus(any());
    }

    // ===================== 수강 취소 =====================

    @Test
    @DisplayName("수강 취소 성공 - PENDING 상태")
    void cancelEnrollment_PENDING_성공() {
        enroll.setEnrollStatus("PENDING");
        when(enrollmentDao.updateEnrollmentStatus(enroll)).thenReturn(1);

        int result = enrollmentService.cancelEnrollment(enroll);

        assertEquals(1, result);
        verify(enrollmentDao, never()).decreaseCurEnrollCnt(anyInt()); // 인원 감소 호출 안 됨
    }

    @Test
    @DisplayName("수강 취소 성공 - CONFIRMED 상태, 7일 이내")
    void cancelEnrollment_CONFIRMED_7일이내_성공() {
        enroll.setEnrollStatus("CONFIRMED");
        enroll.setConfirmedAt(LocalDateTime.now().minusDays(3)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))); // 3일 전 결제

        when(enrollmentDao.updateEnrollmentStatus(enroll)).thenReturn(1);
        when(enrollmentDao.decreaseCurEnrollCnt(enroll.getLecId())).thenReturn(1);

        int result = enrollmentService.cancelEnrollment(enroll);

        assertEquals(1, result);
        verify(enrollmentDao).decreaseCurEnrollCnt(enroll.getLecId());
    }

    @Test
    @DisplayName("수강 취소 실패 - CONFIRMED 상태, 7일 초과")
    void cancelEnrollment_CONFIRMED_7일초과_실패() {
    	
    	// 여기선 when().thenReturn() 없음
    	// DB를 안건드리고 날짜 체크만으로 return 3이 되어야 하기 때문에, 가짜 Dao 약속 자체가 필요 없음
        
    	// given
    	enroll.setEnrollStatus("CONFIRMED");
        enroll.setConfirmedAt(LocalDateTime.now().minusDays(8)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))); // 8일 전 결제
        // 결제일을 8일 전으로 세팅 (7일 초과 상황)

        // when
        int result = enrollmentService.cancelEnrollment(enroll);

        // then
        assertEquals(3, result);
        
        verify(enrollmentDao, never()).updateEnrollmentStatus(enroll); // DB 건드리지 않음
        // DB 건드리지 않고 return 3 했는지 확인
        verify(enrollmentDao, never()).decreaseCurEnrollCnt(anyInt());
    }
}
