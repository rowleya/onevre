package com.googlecode.onevre.gwt.client.ag.types;

public class JabberMessage {

    // Who the message is from
    private String from = "";

    // The message
    private String message = null;

    // The date of the message
    private String date = null;

	public JabberMessage (JabberMessageJSO jso) {
		from = jso.getFrom();
		message = jso.getMessage();
		date = jso.getDate();
	};

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return from;
	}

	public String getMessage() {
		return message;
	};

	public void setMessage(String message) {
		this.message = message;
	};

	public String getDate() {
		return date;
	};

	public void setDate(String date) {
		this.date = date;
	}

}
