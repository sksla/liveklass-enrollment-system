package com.example.liveklass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PageInfoDto {

	private int listCount;
	private int currentPage;
	private int pageLimit; 	// 하단에 보여줄 페이지 번호 개수
	private int boardLimit;	// 한 페이지에 보여줄 게시글 수
	private int maxPage;
	private int startPage;
	private int endPage;
}
