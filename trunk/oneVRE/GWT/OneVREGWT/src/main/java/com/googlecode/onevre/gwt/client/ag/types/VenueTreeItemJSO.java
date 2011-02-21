package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class VenueTreeItemJSO extends JavaScriptObject {

    protected VenueTreeItemJSO() {
    }

    public final native int getTreeInstances() /*-{
        return this.getTreeInstances();
    }-*/;

    public final native String getId() /*-{
        return this.getId();
    }-*/;

    // The name of the venue
    public final native String getName() /*-{
        return this.getName();
    }-*/;

    // The venue being addressed
    public final native String getUri() /*-{
        return this.getUri();
    }-*/;

    // True if the venue item is expanded
    public final native boolean isExpanded() /*-{
        return this.isExpanded();
    }-*/;

    // True if the venue item is selected
    public final native boolean isSelected() /*-{
        return this.isSelected();
    }-*/;
    // True if the venue item is selected
    public final native boolean hasConnections() /*-{
        return this.hasConnections();
    }-*/;

    // The connections from this venue to other venues
    public final native VectorJSO<VenueTreeItemJSO> getConnections() /*-{
        return this.getConnections();
    }-*/;


}
