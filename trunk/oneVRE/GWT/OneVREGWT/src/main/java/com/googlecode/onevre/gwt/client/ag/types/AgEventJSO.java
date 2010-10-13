package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class AgEventJSO extends JavaScriptObject {

	protected AgEventJSO() {
	}

	public  native final String getEventSource() /*-{
		return this.eventSource;
	}-*/;

	public  native final String getEventName() /*-{
		return this.eventName;
	}-*/;

	public  native final JavaScriptObject getEventObject() /*-{
		return this.eventObject;
	}-*/;

	public static native final AgEventJSO parse(String JSON)/*-{
		return eval('('+JSON+')');
	}-*/;

}
