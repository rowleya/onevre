package com.googlecode.onevre.gwt.client.axmlrpc;

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.googlecode.onevre.gwt.client.ag.types.AgEventJSO;

public interface RequestReceiver {

    void execute(String source, Vector<JavaScriptObject> parameters);

    void execute(AgEventJSO eventJSO);

    void init(String source);

}
