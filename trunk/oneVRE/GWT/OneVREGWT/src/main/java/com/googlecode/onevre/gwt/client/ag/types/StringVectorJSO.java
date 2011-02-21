package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class StringVectorJSO extends JavaScriptObject {

    protected StringVectorJSO() {
    }

    public final native String get(int i) /*-{
        return this.get(i);
    }-*/;

    public final native int size() /*-{
        return this.size();
    }-*/;

}
