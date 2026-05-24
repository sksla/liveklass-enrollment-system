package com.example.liveklass.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.liveklass.dto.EnrollmentDto;
import com.example.liveklass.dto.LectureDto;
import com.example.liveklass.dto.PageInfoDto;
import com.example.liveklass.service.EnrollmentService;
import com.example.liveklass.util.PagingUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/enrollment")
@RequiredArgsConstructor
@Controller
public class EnrollmentController {
	
	private final EnrollmentService enrollmentService;
	private final PagingUtil pagingUtil;
	
	@GetMapping("/list.page")
	public String enrollmentListPage() {
		return "enrollmentList";
	}
	
	// 신청 가능한 강의 목록 조회 
	@ResponseBody
	@PostMapping("lectureList.do")
	public Map<String, Object> selectLectureListForEnroll(){
		
		List<LectureDto> list = enrollmentService.selectLectureListForEnroll();
		Map<String, Object> mp = new HashMap<>();
		
		mp.put("list", list);
		
		return mp;
	}
	
	// 내 수강 신청 목록 조회(페이징)
	@ResponseBody
	@PostMapping(value="/enrollmentList.do", produces="application/json; charset=utf-8")
	public Map<String, Object> selectMyEnrollmentList(@RequestParam(value="page", defaultValue="1") int currentPage){
		
		List<EnrollmentDto> list = new ArrayList<>();
		int listCount = enrollmentService.selectMyEnrollmentListCount();
		PageInfoDto pi = pagingUtil.getPageIntoDto(listCount, currentPage, 5, 5);
		list = enrollmentService.selectMyEnrollmentList(pi);
		
		Map<String, Object> mp = new HashMap<>();
		mp.put("list", list);
		mp.put("pi",pi);
		
		return mp;
		
	}
	
	// 수강 신청
	@ResponseBody
	@PostMapping("/enroll.do")
	public Map<String, Object> insertEnrollment(EnrollmentDto enroll) {

	    int result = enrollmentService.insertEnrollment(enroll);
	    Map<String, Object> mp = new HashMap<>();

	    if (result == 1) {
	        mp.put("alertMsg", "수강 신청에 성공하였습니다. 결제 확정을 진행해주세요.");
	    } else if (result == 3) {
	        mp.put("alertMsg", "정원이 초과되었습니다.");
	    } else if (result == 4) {
	        mp.put("alertMsg", "이미 신청 중인 강의입니다.");
	    } else {
	        mp.put("alertMsg", "수강 신청에 실패했습니다.");
	    }

	    return mp;
	}
	
	// 결제 확정
	@ResponseBody
	@PostMapping("/confirm.do")
	public Map<String, Object> confirmEnrollment(EnrollmentDto enroll) {

	    int result = enrollmentService.confirmEnrollment(enroll);
	    Map<String, Object> mp = new HashMap<>();

	    if (result == 1) {
	        mp.put("alertMsg", "결제가 완료되어 수강 확정되었습니다.");
	    } else if (result == 3) {
	        mp.put("alertMsg", "정원이 초과되었습니다.");
	    } else {
	        mp.put("alertMsg", "결제 확정에 실패했습니다.");
	    }

	    return mp;
	}
	
	// 수강 취소
	@ResponseBody
	@PostMapping("/cancel.do")
	public Map<String, Object> cancelEnrollment(EnrollmentDto enroll) {

	    int result = enrollmentService.cancelEnrollment(enroll);
	    Map<String, Object> mp = new HashMap<>();

	    if (result == 1) {
	        mp.put("alertMsg", "수강이 정상적으로 취소되었습니다.");
	    } else if (result == 3) {
	        mp.put("alertMsg", "결제 취소 기간이 지났습니다.");
	    } else {
	        mp.put("alertMsg", "수강 취소에 실패했습니다.");
	    }

	    return mp;
	}

}
