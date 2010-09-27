package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class VectorJSO<T extends JavaScriptObject> extends JavaScriptObject {

	protected VectorJSO (){
	}


	public native final T get(int i) /*-{
		return this.get(i);
	}-*/;

	public native final int size() /*-{
		return this.size();
	}-*/;

}
