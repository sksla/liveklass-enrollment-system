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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.liveklass.dto.LectureDto;
import com.example.liveklass.dto.PageInfoDto;
import com.example.liveklass.service.LectureService;
import com.example.liveklass.util.PagingUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/lecture")
@RequiredArgsConstructor
@Controller
public class LectureController {
	
	private final LectureService lectureService;
	private final PagingUtil pagingUtil;
	
	// 강의 목록 관련 ===================================================
	
	// 강의 목록 페이지 
	@GetMapping("/list.page")
	public String lectureListPage() {
		return "lectureList";
	}
	
	// 강의 목록 필터링 및 페이징 조회
	@ResponseBody
	@PostMapping(value="/filterLecture.do", produces="application/json; charset=utf-8")
	public Map<String, Object> filterLectureList(@RequestParam(value="page", defaultValue="1") int currentPage
											   , @RequestParam String condition){
		
		List<LectureDto> list = new ArrayList<>();
		int listCount = lectureService.filterLectureListCount(condition);
		PageInfoDto pi = pagingUtil.getPageIntoDto(listCount, currentPage, 5, 5);
		list = lectureService.filterLectureList(condition, pi);
		
		Map<String, Object> filterMap = new HashMap<>();
		log.debug("강의목록 요청 페이지 : {}", currentPage);
		log.debug("강의목록 : {}", list);
		filterMap.put("list", list);
		filterMap.put("pi", pi);
		
		return filterMap;
	}	
	
	// 강의 등록 ==============================
	@GetMapping("/lecturRegistForm.page")
	public String lectureRegistForm() {
		return "lectureRegistForm";
	}
	
	@PostMapping("/lectureRegist.do")
	public String insertLecture(LectureDto lecture, RedirectAttributes redirectAttributes) {
		
		int result = lectureService.insertLecture(lecture);
		
		// 성공시 => alert메세지와 함께 목록페이지로 이동
		// 실패시 => alert메세지와 함께 작성페이지에 그대로
		if (result == 1) {
			redirectAttributes.addFlashAttribute("alertMsg", "강의 등록에 성공하였습니다.");
		} else {
			redirectAttributes.addFlashAttribute("alertMsg", "강의 등록에 실패하였습니다.");
			redirectAttributes.addFlashAttribute("historyBackYN", "Y");
		}
		
		return "redirect:/lecture/list.page";
	}
	
	// 강의 상세 조회 ====================================
	@GetMapping("/detail.do")
	public ModelAndView lectureDetail(int lecId, ModelAndView mv) {
		
		mv.addObject("lecture", lectureService.selectLecture(lecId))
		  .addObject("list", lectureService.selectEnrollmentMemberList(lecId))
		  .setViewName("lectureDetail");
		
		return mv;
		
	}
	
	// 강의 상태 변경
	@PostMapping("/modify.do")
	public String updateLectureStatus(LectureDto lecture, RedirectAttributes redirectAttributes) {
		
		int result = lectureService.updateLectureStatus(lecture);
		
		if(result == 1) {
			redirectAttributes.addFlashAttribute("alertMsg", "강의 상태가 변경되었습니다.");
		} else if(result == 0) {
			redirectAttributes.addFlashAttribute("alertMsg", "강의 상태 변경에 실패하였습니다.");
		} else {
			redirectAttributes.addFlashAttribute("alertMsg", "잘못된 상태 변경 입니다..");
		}
		
		return "redirect:/lecture/list.page";
	}

}
