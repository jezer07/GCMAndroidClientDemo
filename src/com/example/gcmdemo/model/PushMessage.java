package com.example.gcmdemo.model;

import java.util.Date;

public class PushMessage {
	
	private String message;
	//private Date received;
	private String from;
	private String type;

	private String to;
	
	
	public PushMessage(String message, String from, String type) {
		super();
		this.message = message;
		//this.received = received;
		this.from = from;
		this.type = type;
	
	}


	
	public PushMessage(String message, String from, String type,
			String to) {
		super();
		this.message = message;
		this.from = from;
		this.type = type;

		this.to = to;
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
/*	public Date getReceived() {
		return received;
	}
	public void setReceived(Date received) {
		this.received = received;
	}*/

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}






	public String getTo() {
		return to;
	}



	public void setTo(String to) {
		this.to = to;
	}

}
