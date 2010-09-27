package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class VenueTreeItemJSO extends JavaScriptObject {

	protected VenueTreeItemJSO (){
	}

	public native final int getTreeInstances() /*-{
    	return this.getTreeInstances();
	}-*/;

    public native final String getId() /*-{
    	return this.getId();
	}-*/;

    // The name of the venue
    public native final String getName() /*-{
	    return this.getName();
	}-*/;

    // The venue being addressed
    public native final String getUri() /*-{
	    return this.getUri();
	}-*/;

    // True if the venue item is expanded
    public native final boolean isExpanded() /*-{
	    return this.isExpanded();
	}-*/;

    // True if the venue item is selected
    public native final boolean isSelected() /*-{
	    return this.isSelected();
	}-*/;
    // True if the venue item is selected
    public native final boolean hasConnections() /*-{
	    return this.hasConnections();
	}-*/;

    // The connections from this venue to other venues
    public native final VectorJSO<VenueTreeItemJSO> getConnections() /*-{
	    return this.getConnections();
	}-*/;


}
