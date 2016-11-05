package com._3po_labs.alexa_model_tester.controller;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import com._3po_labs.alexa_model_tester.exception.NewtoolException;
import com._3po_labs.alexa_model_tester.exception.NewtoolExceptionCode;
import com._3po_labs.alexa_model_tester.messagequeue.AlexaRequestQueue;
import com._3po_labs.alexa_model_tester.model.SendAudioRequest;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.threepio_labs.avsclient.client.AVSClient;
import com.threepio_labs.avsclient.model.RecognizeSpeechRequest;

@Controller
public class AudioFileRequestController {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ApplicationContext appContext;
	
	@Value("${audioRequest.maxWait}")
	private int maxWait;
	
	private AlexaRequestQueue alexaRequestQueue = AlexaRequestQueue.getInstance();
	
    @RequestMapping(value = "/rest/audioFileRequest", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody SpeechletRequestEnvelope sendAudioFile(@RequestBody SendAudioRequest body, @RequestHeader(value="Authorization") String authorizationHeader) throws NewtoolException {
    	AVSClient client = appContext.getBean("avsClient", AVSClient.class);
    	Future<Response> response = client.recognizeSpeechAsync(authorizationHeader, new RecognizeSpeechRequest(),body.getAudioFileUrl());
    	return waitForAlexaRequest(body.getUserId(),body.getApplicationId(), response);
    }

	private SpeechletRequestEnvelope waitForAlexaRequest(String userId, String applicationId, Future<Response> response) throws NewtoolException {
		if(userId == null || applicationId == null){
	        throw new NewtoolException("Valid userId and applicationId required.", NewtoolExceptionCode.INVALID);
		}
		
		SpeechletRequestEnvelope alexaRequest = alexaRequestQueue.getAlexaResponse(userId, applicationId);
		if(alexaRequest != null){
			return alexaRequest;
		}
		
		Object semaphore = alexaRequestQueue.getSemaphore();
		long endTime = System.currentTimeMillis() + maxWait;
		while(true){
			// This code should be broken out into its own thread outside this method 
			// that can similarly interrupt this thread once the future completes
			try {
				if(response.isDone() && response.get() != null && (response.get().getStatus() == 401 || response.get().getStatus() == 403)){
					throw new NewtoolException("Request did not include a valid Login With Amazon access token.", NewtoolExceptionCode.UNAUTHORIZED);
				}
			} catch (InterruptedException | ExecutionException e) {
				throw new NewtoolException("Unknown exception: " + e.getMessage(), NewtoolExceptionCode.GENERAL);
			}
			long currentTime = System.currentTimeMillis();
			if(currentTime >= endTime){
				LOG.error("Past endtime, exiting.");
		        throw new NewtoolException("No response found in the alotted time.", NewtoolExceptionCode.ROUNDTRIP_TIMEOUT);
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
		        throw new NewtoolException("Thread waiting for Alexa request was interrupted unexpectedly.", NewtoolExceptionCode.GENERAL);
			}
		}
	}
}
