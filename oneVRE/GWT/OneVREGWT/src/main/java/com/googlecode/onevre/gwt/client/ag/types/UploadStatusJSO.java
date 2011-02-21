package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class UploadStatusJSO extends JavaScriptObject {

    protected UploadStatusJSO() {
    }

    public final native String getFileName() /*-{
        return this.fileName;
    }-*/;

    public final native String getSize() /*-{
        return this.size;
    }-*/;

    public final native String getDone() /*-{
        return this.done;
    }-*/;

    public final native String getPercentage() /*-{
        return this.percentage;
    }-*/;


}
