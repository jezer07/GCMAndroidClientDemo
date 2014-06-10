package com.example.gcmdemo.model;

import java.util.Date;

public class PushMessage {
	
	private String message;
	private Date received;
	private String from;
	
	public PushMessage(String from, String message, Date received) {
		super();
		this.from = from;
		this.message = message;
		this.received = received;
	}
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getReceived() {
		return received;
	}
	public void setReceived(Date received) {
		this.received = received;
	}

}
