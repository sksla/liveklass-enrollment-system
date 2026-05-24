package com.example.liveklass.dto;

import java.sql.Date;

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
public class EnrollmentDto {
	
	private int enrollId;
	private int lecId;
	private String lecTitle;
	private int memId;
	private String enrollStatus;
	private String confirmedAt;
	private String cancelledAt;
	private String createdAt;
	private int capacity;
	private int currentEnrollmentCount;
	
	
}
