package com._3po_labs.alexa_model_tester.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.Future;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import com._3po_labs.alexa_model_tester.exception.AlexaModelTesterException;
import com._3po_labs.alexa_model_tester.messagequeue.AlexaRequestQueue;
import com._3po_labs.alexa_model_tester.model.SendAudioRequest;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.threepio_labs.avsclient.client.AVSClient;
import com.threepio_labs.avsclient.model.RecognizeSpeechRequest;

@Controller
@Scope("request")
public class AudioFileRequestController {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ApplicationContext appContext;
	
/*	@Autowired
	private Environment env;*/
	
	@Value("${audioRequest.maxWait}")
	private int maxWait;
	
	private AlexaRequestQueue alexaRequestQueue = AlexaRequestQueue.getInstance();
	
    @RequestMapping(value = "/rest/audioFileRequest", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody SpeechletRequestEnvelope sendAudioFile(@RequestBody SendAudioRequest body, @RequestHeader(value="Authorization") String authorizationHeader) throws AlexaModelTesterException {
    	AVSClient client = appContext.getBean("avsClient", AVSClient.class);
    	Future<Response> response = client.recognizeSpeechAsync(authorizationHeader, new RecognizeSpeechRequest(),body.getAudioFileUrl());
    	return waitForAlexaRequest(body.getUserId(),body.getApplicationId());
    }

	private SpeechletRequestEnvelope waitForAlexaRequest(String userId, String applicationId) throws AlexaModelTesterException {
		if(userId == null || applicationId == null){
	        throw new AlexaModelTesterException("Valid userId and applicationId required.");
		}
		
		SpeechletRequestEnvelope alexaRequest = alexaRequestQueue.getAlexaResponse(userId, applicationId);
		if(alexaRequest != null){
			return alexaRequest;
		}
		
		Object semaphore = alexaRequestQueue.getSemaphore();
		long endTime = System.currentTimeMillis() + maxWait;
		while(true){
			long currentTime = System.currentTimeMillis();
			if(currentTime >= endTime){
				LOG.error("Past endtime, exiting.");
		        throw new AlexaModelTesterException("No response found in the alotted time.");
			}
			
			try {
				LOG.debug("Waiting until " + Instant.ofEpochMilli(endTime).toString());
				synchronized(semaphore){
					semaphore.wait(endTime - currentTime);
				}

				LOG.debug("Stopped waiting, checking for results for userId '" + userId.substring(0,25)
				+ "...' and applicationId '" + applicationId + "'.");
				alexaRequest = alexaRequestQueue.getAlexaResponse(userId, applicationId);
				if(alexaRequest != null){
					return alexaRequest;
				}
			} catch (InterruptedException e) {
				LOG.error("Thread interrupted.");
		        throw new AlexaModelTesterException("Thread waiting for Alexa request was interrupted unexpectedly.");
			}
		}
	}
}
