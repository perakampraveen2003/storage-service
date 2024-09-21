package com.miniproject.storage_service.model;

import java.util.Date;

import lombok.Data;

@Data
public class Employee {

	private long empid;
	private String empName;
	private double empSalary;
	private Date empJoiningDate;
	private String empEmailId;
}
