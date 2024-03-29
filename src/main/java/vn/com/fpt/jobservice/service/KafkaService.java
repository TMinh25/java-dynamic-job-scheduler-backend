package vn.com.fpt.jobservice.service;

import vn.com.fpt.jobservice.model.request.KafkaMessageRequest;

public interface KafkaService {

	void sendKafkaMessage(KafkaMessageRequest messageRequest) throws Exception;

}
