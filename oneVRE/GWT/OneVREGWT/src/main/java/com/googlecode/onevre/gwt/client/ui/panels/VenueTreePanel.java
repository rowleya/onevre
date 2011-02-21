package com.googlecode.onevre.gwt.client.ui.panels;

import java.util.Vector;

import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;
import com.googlecode.onevre.gwt.client.ag.types.VenueTreeItem;

public interface VenueTreePanel {

    VenueServerType getVenueServerType();

    void updateTree(Vector<VenueTreeItem> venueTree);
}
