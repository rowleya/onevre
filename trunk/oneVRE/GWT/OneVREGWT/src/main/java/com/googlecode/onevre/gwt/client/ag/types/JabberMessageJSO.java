package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class JabberMessageJSO extends JavaScriptObject {

    protected JabberMessageJSO() {
    }

    public final native String getFrom() /*-{
        return this.from;
    }-*/;

    public final native String getMessage() /*-{
        return this.messageWithLinks;
    }-*/;

    public final native String getDate() /*-{
        return this.date;
    }-*/;


}
