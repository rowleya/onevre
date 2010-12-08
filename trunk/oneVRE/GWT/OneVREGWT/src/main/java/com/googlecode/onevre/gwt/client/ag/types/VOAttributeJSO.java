package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class VOAttributeJSO extends JavaScriptObject {

	protected VOAttributeJSO(){

	}

	public native final String getVo() /*-{
		return this.getVo();
	}-*/;

	public native final String getGroup() /*-{
		return this.getGroup();
	}-*/;

	public native final String getRole() /*-{
		return this.getRole();
	}-*/;

	public native final String getCap() /*-{
		return this.getCap();
	}-*/;

}
