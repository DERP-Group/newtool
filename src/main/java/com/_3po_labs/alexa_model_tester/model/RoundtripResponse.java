package com._3po_labs.alexa_model_tester.model;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.json.SpeechletResponseEnvelope;

public class RoundtripResponse {

	private SpeechletRequestEnvelope alexaSkillsRequest;
	private SpeechletResponseEnvelope alexaSkillsResponse;
	private Object alexaVoiceServiceResponse;

	public SpeechletRequestEnvelope getAlexaSkillsRequest() {
		return alexaSkillsRequest;
	}

	public void setAlexaSkillsRequest(SpeechletRequestEnvelope alexaSkillsRequest) {
		this.alexaSkillsRequest = alexaSkillsRequest;
	}

	public SpeechletResponseEnvelope getAlexaSkillsResponse() {
		return alexaSkillsResponse;
	}

	public void setAlexaSkillsResponse(SpeechletResponseEnvelope alexaSkillsResponse) {
		this.alexaSkillsResponse = alexaSkillsResponse;
	}

	public Object getAlexaVoiceServiceResponse() {
		return alexaVoiceServiceResponse;
	}

	public void setAlexaVoiceServiceResponse(Object alexaVoiceServiceResponse) {
		this.alexaVoiceServiceResponse = alexaVoiceServiceResponse;
	}
}
