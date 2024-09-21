package com.miniproject.storage_service.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniproject.storage_service.model.Employee;

import jakarta.annotation.PostConstruct;

@Service
public class SQSConsumer {
	@Value("${sqs.url}")
	private String sqsUrl;

	@Value("${aws.accessKey}")
	private String awsAccessKey;

	@Value("${aws.secretKey}")
	private String awsSecretKey;

	@Value("${aws.region}")
	private String awsRegion;

	private AmazonSQS sqsClient;

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private StorageService storageService;

	@PostConstruct
	public void initializeAWSClient() {
		AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(
				new BasicAWSCredentials(awsAccessKey, awsSecretKey));
		this.sqsClient = AmazonSQSClientBuilder.standard().withRegion(awsRegion).withCredentials(credentials).build();

	}

	@SuppressWarnings("unchecked")
	@Scheduled(fixedDelay = 1)
	public void receiveMessage() {
		ReceiveMessageRequest messageRequest = new ReceiveMessageRequest(sqsUrl).withMaxNumberOfMessages(1)
				.withWaitTimeSeconds(3);
		List<Message> sqsMessages = sqsClient.receiveMessage(messageRequest).getMessages();
		List<Employee> empList = sqsMessages.stream().map(m -> {
			Map<String, String> map;
			try {
				map = mapper.readValue(m.getBody(), Map.class);
				Employee empMessage = mapper.readValue(map.get("Message"), Employee.class);
				deleteMessage(m.getReceiptHandle());
				return empMessage;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new Employee();
		}).collect(Collectors.toList());
		storageService.saveToStorage(empList);
	}

	private void deleteMessage(String reciepString) {
		sqsClient.deleteMessage(new DeleteMessageRequest(sqsUrl, reciepString));
	}
}
