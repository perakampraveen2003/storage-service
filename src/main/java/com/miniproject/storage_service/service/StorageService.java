package com.miniproject.storage_service.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniproject.storage_service.model.Employee;

import jakarta.annotation.PostConstruct;

@Service
public class StorageService {

	private AmazonS3 s3Client;

	@Value("${aws.accessKey}")
	private String awsAccessKey;

	@Value("${aws.secretKey}")
	private String awsSecretKey;

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.bucket}")
	private String bucketName;

	@Autowired
	private ObjectMapper mapper;

	@PostConstruct
	public void initializeAWSClient() {
		AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(
				new BasicAWSCredentials(awsAccessKey, awsSecretKey));
		this.s3Client = AmazonS3ClientBuilder.standard().withCredentials(credentials).withRegion(awsRegion).build();

	}

	@SuppressWarnings("deprecation")
	public void saveToStorage(List<Employee> empList) {
		empList.stream().forEach(emp -> {
			try {
				s3Client.putObject(bucketName, emp.getEmpName() + "/" + new Date().toLocaleString() + ".json",
						mapper.writeValueAsString(emp));
				System.out.println("Successfully saved to storage");
			} catch (SdkClientException | JsonProcessingException e) {
				System.out.println("failed emp : " + emp.getEmpid());
				e.printStackTrace();
			}
		});
	}

}
