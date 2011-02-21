package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class VectorJSO<T extends JavaScriptObject> extends JavaScriptObject {

    protected VectorJSO() {
    }


    public final native T get(int i) /*-{
        return this.get(i);
    }-*/;

    public final native int size() /*-{
        return this.size();
    }-*/;


}
