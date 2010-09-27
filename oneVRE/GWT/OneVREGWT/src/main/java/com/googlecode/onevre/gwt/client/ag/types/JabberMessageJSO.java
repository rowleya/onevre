package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class JabberMessageJSO extends JavaScriptObject {

	protected JabberMessageJSO (){
	}

	public native final String getFrom() /*-{
		return this.getFrom();
	}-*/;

	public native final String getMessage() /*-{
		return this.getMessageWithLinks();
	}-*/;

	public native final String getDate() /*-{
		return this.getDate();
	}-*/;


}
