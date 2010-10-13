package com.googlecode.onevre.gwt.client.axmlrpc;

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.googlecode.onevre.gwt.client.ag.types.AgEventJSO;

public interface RequestReceiver {

	public void execute(String source, Vector<JavaScriptObject> parameters);

	public void execute(AgEventJSO eventJSO);

	public void init(String source);

}
