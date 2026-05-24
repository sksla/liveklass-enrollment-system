package com.example.liveklass.dto;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class LectureDto {
	
	private int lecId;
	private int creatorId; // 강사 id
	private String memName; // 강사 이름
	private String lecTitle;
	private String description;
	private int capacity;
	private int currentEnrollmentCount;
	private int price;
	private String status;
	private String startDate;
	private String endDate;
	private Date createdAt;
	private Date updatedAt;
	
	private List<MemberDto> memList;
	
	
}
