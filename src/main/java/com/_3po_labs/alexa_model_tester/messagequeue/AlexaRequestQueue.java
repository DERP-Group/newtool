package com._3po_labs.alexa_model_tester.messagequeue;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.json.SpeechletRequestEnvelope;

public class AlexaRequestQueue {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private Map<String, SpeechletRequestEnvelope> alexaRequests;
	private Object semaphore;
	
	private AlexaRequestQueue() {
		alexaRequests = new HashMap<String, SpeechletRequestEnvelope>();
	}

	private static class SingletonHolder {
		private static final AlexaRequestQueue INSTANCE = new AlexaRequestQueue();
	}

	public static AlexaRequestQueue getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public SpeechletRequestEnvelope getAlexaResponse(String userId, String applicationId){
		String hash = hash(userId, applicationId);
		
		synchronized(alexaRequests){
			return alexaRequests.remove(hash);
		}
	}
	
	public void addAlexaResponse(String userId, String applicationId, SpeechletRequestEnvelope sre){
		String hash = hash(userId, applicationId);
		
		synchronized(alexaRequests){
			alexaRequests.put(hash, sre);
		}
	}
	
	private String hash(String userId, String applicationId){
		return userId + applicationId;
	}
	
	public Object getSemaphore(){
		synchronized(this){
			if(semaphore == null){
				semaphore = new Object();
				LOG.debug("Initialized semaphore as: " + semaphore);
			}
		}
		return semaphore;
	}
	
	public void notifyAllThreads(){
			synchronized(semaphore){
				semaphore.notifyAll();
			}
	}
}
