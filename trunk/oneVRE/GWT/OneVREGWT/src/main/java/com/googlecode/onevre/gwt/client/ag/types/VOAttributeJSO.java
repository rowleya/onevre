package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class VOAttributeJSO extends JavaScriptObject {

    protected VOAttributeJSO() {

    }

    public final native String getVo() /*-{
        return this.getVo();
    }-*/;

    public final native String getGroup() /*-{
        return this.getGroup();
    }-*/;

    public final native String getRole() /*-{
        return this.getRole();
    }-*/;

    public final native String getCap() /*-{
        return this.getCap();
    }-*/;

}
