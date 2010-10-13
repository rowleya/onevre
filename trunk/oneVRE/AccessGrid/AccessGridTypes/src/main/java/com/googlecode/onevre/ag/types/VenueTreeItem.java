/*
 * @(#)VenueTreeItem.java
 * Created: 08-October-2007
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

import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


    /**
     * A venue tree item
     *
     * @author Andrew G D Rowley
     * @version 1.0
     */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class VenueTreeItem implements Comparable <Object> {


    // The number of tree instances created
    private static int treeInstances = 0;

    // Returns a unique id
    private static synchronized String getUniqueId() {
        treeInstances++;
        return String.valueOf(System.currentTimeMillis())
            + String.valueOf(treeInstances);
    }


//	private HashMap<String,VenueTreeItem> venues;
    // The unique id of the node
//    private String selectedVenueId = null;

    private String uniqueId = getUniqueId();

    // The name of the venue
    private String venueName = null;

    // The venue being addressed
    private String venueUrl = null;

    // True if the venue item is expanded
    private boolean isExpanded = false;

    // True if the venue item is selected
    private boolean isSelected = false;

    // The connections from this venue to other venues
    private Vector<VenueTreeItem> connections = null;

    /**
     * Creates a new VenueTreeItem
     * @param name The name of the venue
     * @param url The url of the venue
     */
    public VenueTreeItem(String name, String url) {
        this.venueName = name;
        this.venueUrl = url;
    }

    /**
     * empty constructor
     */
    public VenueTreeItem(){
        // do nothing
    }

    /**
     * Gets an id that uniquely identifies this node
     * @return The id
     */
    @XmlElement
    public String getId() {
        return uniqueId;
    }

    /**
     * Sets the unique Id
     * @param uniqueId the unique Id
     */
    public void setId(String uniqueId){
        this.uniqueId=uniqueId;
    }

    /**
     * Gets the name of the venue
     * @return The name of the venue
     */
    @XmlElement
    public String getName() {
        return venueName;
    }

    /**
     * Add a venueName
     * @param venueName the venue name
     */
    public void setName(String venueName){
        this.venueName=venueName;
    }

    /**
     * Returns the uri of the venue
     * @return The venue uri
     */
    @XmlElement
    public String getUri() {
        return venueUrl;
    }

    /**
     * add a venueUrl
     * @param venueUrl the venue url
     */
    public void setUri(String venueUrl){
        this.venueUrl=venueUrl;
    }

    /**
     * Returns true if the venue is expanded to show its exits
     * @return True if the venue is expanded, false if not
     */
    @XmlElement
    public boolean isExpanded() {
        return isExpanded;
    }

    /**
     * set the expanded flag
     * @param isExpanded the expanded flag
     */
    public void setExpanded(boolean isExpanded){
        this.isExpanded=isExpanded;
    }

    /**
     * Returns true if the connections have been loaded for this node
     * @return True if the connections have been loaded, false if not
     */
    @XmlElement
    public boolean hasConnections() {
        return connections != null;
    }

    /**
     * Gets the venue connections
     * @return The exits from this venue
     */
    @XmlElement
    public Vector<VenueTreeItem> getConnections() {
        return connections;
    }

    /**
     * add connections
     * @param connections the list of connections
     */
    public void setConnections(Vector<VenueTreeItem> connections){
        this.connections=connections;
    }

    public VenueTreeItem findVenueTreeItem(String venueId){
    	if (venueId.equals(uniqueId)){
    		return this;
    	}
    	if (connections != null){
    		for (VenueTreeItem vti : connections){
    			VenueTreeItem out = vti.findVenueTreeItem(venueId);
    			if (out!=null){
    				return out;
    			}
    		}
    	}
    	return null;
    }

    /**
     * Collapses this node
     */
    public void collapse() {
        isExpanded = false;
    }

    /**
     * Returns true if the venue is currently selected
     * @return True if the venue is selected
     */
    @XmlElement
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * set the selected flag
     * @param isSelected the selected flag
     */
    public void setSelected(boolean isSelected){
        this.isSelected=isSelected;
    }

    public String toString(String l){
    	String out="";
   	   	out += l + toString();
   	   	Vector<VenueTreeItem> vti = getConnections();
   	   	if (vti!=null){
	   	   	for (VenueTreeItem conn: vti){
	   	   		out += conn.toString(l + "\t");
	   	   	}
   	   	}
   	   	return out;
    }

    public String toString(){
   	   	return "id:" + getId() +" url: " + getUri() +" = "+ getName() + "\n";
    }
    /**
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        if (o instanceof VenueTreeItem) {
            VenueTreeItem venue = (VenueTreeItem) o;
            return venueName.compareTo(venue.venueName);
        }
        return 0;
    }
}
