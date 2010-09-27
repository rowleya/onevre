package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class StringJSO extends JavaScriptObject {

	protected StringJSO (){
	}

	public native final String value() /*-{
		return this;
	}-*/;

}
