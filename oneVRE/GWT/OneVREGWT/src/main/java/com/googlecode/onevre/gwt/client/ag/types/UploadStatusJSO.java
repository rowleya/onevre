package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class UploadStatusJSO extends JavaScriptObject {

	protected UploadStatusJSO (){
	}

	public native final String getFileName() /*-{
		return this.fileName;
	}-*/;

	public native final String getSize() /*-{
		return this.size;
	}-*/;

	public native final String getDone() /*-{
		return this.done;
	}-*/;

	public native final String getPercentage() /*-{
		return this.percentage;
	}-*/;


}
