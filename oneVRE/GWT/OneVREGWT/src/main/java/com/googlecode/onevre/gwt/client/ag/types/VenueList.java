package com.googlecode.onevre.gwt.client.ag.types;

import java.util.HashMap;
import java.util.Vector;

public class VenueList {

    // Urls of venues at the root of the tree
    private Vector<VenueTreeItem> roots = new Vector<VenueTreeItem>();

    // The url of the current venue
    private String currentVenueUrl = null;

    // The name of the current venue
    private String currentVenueName = null;

     // A map of venue ids to venues
    private HashMap<String, VenueTreeItem> venues =
        new HashMap<String, VenueTreeItem>();

    // The currently selected venue id
    private String selectedVenueId = null;


}
