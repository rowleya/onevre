package com.googlecode.onevre.gwt.client.ag.types;

public class MessageBox {

    // Who the message is from
    private String type = "";

    // The message
    private String message = "";


	public MessageBox (MessageBoxJSO jso) {
		message = jso.getMessage();
		type = jso.getType();
	};


	public String getMessage() {
		return message;
	}

	public String getType() {
		return type;
	};

	public String toString(){
		String out = "( "+ type+" ) " + message;
		return out;
	}
}
