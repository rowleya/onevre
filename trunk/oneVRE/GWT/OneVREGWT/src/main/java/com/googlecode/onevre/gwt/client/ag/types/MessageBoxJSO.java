package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class MessageBoxJSO extends JavaScriptObject {

	protected MessageBoxJSO (){
	}

	public native final String getMessage() /*-{
		return this.message;
	}-*/;

	public native final String getType() /*-{
		return this.type;
	}-*/;

}
