package com.googlecode.onevre.gwt.client.axmlrpc;

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;

public interface RequestReceiver {

	public void execute(String source, Vector<JavaScriptObject> parameters);

	public void init(String source);

}
