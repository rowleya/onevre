/*
 * @(#)VenueList.java
 * Created: 11-May-2007
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.googlecode.onevre.ag.types;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.onevre.types.soap.exceptions.SoapException;

/**
 * A list of venues to be displayed in the client
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class VenueList {

    /**
     * The mode that shows all the venues
     */
    public static final int MODE_ALL_VENUES = 0;

    /**
     * The mode that shows the exits from the current root venue
     */
    public static final int MODE_EXITS = 1;

    /**
     * The mode that shows the venues in "My Venues"
     */
    public static final int MODE_MY_VENUES = 2;

    /**
     * The names of the modes
     */
    public static final String[] MODES = new String[]{
        "All Venues", "Exits", "My Venues"};

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

    // The current vertical scroll position of the list
    private int verticalScrollPosition = 0;

    // The current horizontal scroll position of the list
    private int horizontalScrollPosition = 0;

    // The current mode
    private int mode = MODE_EXITS;


    /**
     * Creates a new venue list
     * @param currentVenueState The state of the current venue
     * @param mode The mode to display the list using
     * @param myVenues A user-selected list of venues
     * @throws IOException
     * @throws SoapException
     */
    public VenueList(VenueState currentVenueState)
            throws IOException, SoapException {
        this.currentVenueUrl = currentVenueState.getUri();
        this.currentVenueName = currentVenueState.getName();
    }

    /**
     * Creates an empty VenueList (for serialization)
     *
     */
    public VenueList() {
        // Does Nothing
    }


    /**
     * Gets the tree item with the given id
     * @param id The id of the item to get
     * @return A VenueTreeItem with the given id, or null if none
     */
    @XmlElement
    public VenueTreeItem getVenue(String id) {
    	return venues.get(id);
    }

    /**
     * Searches the entire trees to find a Venue
     * @param venueId The id of the Venue to find
     * @return A VenueTreeItem with the given id, or null if there is none
     */
    public VenueTreeItem findVenue(String venueId) {
    	VenueTreeItem item = venues.get(venueId);
    	if (item!=null) {
    		return item;
    	}
    	for (VenueTreeItem vti: venues.values()){
    		item = vti.findVenueTreeItem(venueId);
    		if (item!=null) {
        		return item;
        	}
    	}
    	for (VenueTreeItem vti:roots){
    		item = vti.findVenueTreeItem(venueId);
    		if (item!=null) {
        		return item;
        	}
    	}
    	return null;
    }

    /**
     * Sets the current venue
     * @param url The url of the venue
     * @param name The name of the venue
     */
    public void setCurrentVenue(String url, String name) {
        this.currentVenueUrl = url;
        this.currentVenueName = name;
    }

    /**
     * Gets the name of the current venue
     * @return The name
     */
    @XmlElement
    public String getCurrentVenueName() {
        return currentVenueName;
    }

    /**
     * Sets the name of the current venue
     * @param currentVenueName The name to set
     */
    public void setCurrentVenueName(String currentVenueName) {
        this.currentVenueName = currentVenueName;
    }

    /**
     * Gets the url of the current venue
     * @return The url
     */
    @XmlElement
    public String getCurrentVenueUrl() {
        return currentVenueUrl;
    }

    /**
     * Sets the url of the current venue
     * @param currentVenueUrl The url to set
     */
    public void setCurrentVenueUrl(String currentVenueUrl) {
        this.currentVenueUrl = currentVenueUrl;
    }

    /**
     * Gets the currently selected venue
     * @return The currently selected venue
     */
    @XmlElement
    public String getSelectedVenueId() {
        return selectedVenueId;
    }

    /**
     * Sets the venues
     * @param venues The venues
     */
    public void setVenues(HashMap<String, VenueTreeItem> venues) {
        this.venues = venues;
    }

    /**
     * Gets the venues
     * @return The venues
     */
    @XmlElement
    public HashMap<String, VenueTreeItem> getVenues() {
        return venues;
    }

    /**
     * Sets the current mode of the list
     * @param mode one of MODE_ALL_VENUES, MODE_EXITS or MODE_MY_VENUES
     * @throws IOException
     * @throws SoapException
     */
	public void selectMode(int mode, Vector<VenueTreeItem> roots) throws IOException, SoapException {
        this.mode = mode;
        this.roots = roots;
        this.venues.clear();
        for (VenueTreeItem venue: roots){
        	this.venues.put(venue.getId(), venue);
        }
    }

    @XmlElement
	public int getMode(){
		return mode;
	}

	public void addVenue(VenueTreeItem venue){
		 venues.put(venue.getId(), venue);
	}

    public boolean removeVenue(String venueId){
    	return (venues.remove(venueId)!=null);
    }


    /**
     * Gets the root venue items
     * @return The root venue items
     */
    @XmlElement
    public Vector<VenueTreeItem> getRoots() {
        return roots;
    }

    /**
     * Sets the roots
     * @param roots The roots to set
     */
    public void setRoots(Vector<VenueTreeItem> roots) {
        this.roots = roots;
    }

    public void addRoot(VenueTreeItem root) {
    	roots.add(root);
        Collections.sort(roots);
	}

    public boolean removeRoot(VenueTreeItem root){
    	return roots.remove(root);
    }

    /**
     * Sets the current vertical scroll position
     * @param position The position
     */
    public void setVerticalScrollPosition(int position) {
        this.verticalScrollPosition = position;
    }

    /**
     * Sets the current horizontal scroll position
     * @param position The position
     */
    public void setHorizontalScrollPosition(int position) {
        this.horizontalScrollPosition = position;
    }

    /**
     * Gets the current vertical scroll position
     * @return The position
     */
    @XmlElement
    public int getVerticalScrollPosition() {
        return verticalScrollPosition;
    }

    /**
     * Gets the current horizontal scroll position
     * @return The position
     */
    @XmlElement
    public int getHorizontalScrollPosition() {
        return horizontalScrollPosition;
    }

    public String toString(){
    	String out = "";
    	for (String id:venues.keySet()) {
    		out += "\nID: " + id +" - " + venues.get(id).toString("");
    	}
    	return out;
    }


    /**
     * Sets the venue that is currently selected in the list
     * @param venueId The currently selected venue id
     */
    public void setSelectedVenueId(String venueId) {
        VenueTreeItem venue = venues.get(venueId);
        if (venue != null) {
            if (selectedVenueId != null) {
                VenueTreeItem currentSelection =
                    venues.get(selectedVenueId);
                if (currentSelection != null) {
                    currentSelection.setSelected(false);
                }
            }
            venue.setSelected(true);
            selectedVenueId = venue.getId();
        }
    }

}
