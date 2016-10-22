package com._3po_labs.alexa_model_tester.model;

public class SendAudioRequest {

	private String audioFileUrl;
	private String userId;
	private String applicationId;

	public String getAudioFileUrl() {
		return audioFileUrl;
	}

	public void setAudioFileUrl(String audioFileUrl) {
		this.audioFileUrl = audioFileUrl;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	@Override
	public String toString() {
		return "SendAudioRequest [audioFileUrl=" + audioFileUrl + ", userId=" + userId + ", applicationId="
				+ applicationId + "]";
	}
}
