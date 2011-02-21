package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class MessageBoxJSO extends JavaScriptObject {

    protected MessageBoxJSO() {
    }

    public final native String getMessage() /*-{
        return this.message;
    }-*/;

    public final native String getType() /*-{
        return this.type;
    }-*/;

}
