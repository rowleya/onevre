package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class AgEventJSO extends JavaScriptObject {

    protected AgEventJSO() {
    }

    public final native  String getEventSource() /*-{
        return this.eventSource;
    }-*/;

    public final native String getEventName() /*-{
        return this.eventName;
    }-*/;

    public final native JavaScriptObject getEventObject() /*-{
        return this.eventObject;
    }-*/;

    public static final native AgEventJSO parse(String json) /*-{
        return eval('('+json+')');
    }-*/;

}
