package com.googlecode.onevre.ag.agserver;

import java.util.Date;

import com.googlecode.onevre.ag.types.ClientProfile;

public class VenueClientState {

    private ClientProfile clientProfile = null;

    private Venue venue = null;

    private long timeout = new Date().getTime();

    public VenueClientState(Venue venue, long timeout, ClientProfile clientProfile){
        this.venue=venue;
        this.timeout = new Date().getTime()+timeout;
        this.clientProfile=clientProfile;
    }

    public ClientProfile getClientProfile(){
        return clientProfile;
    }

    public long getTimeout(){
        return timeout;
    }

    public boolean hasTimedOut(){
    	 return new Date().after(new Date(timeout));
    }

    public void setTimeout(long timeout){
        this.timeout=timeout;
    }

    public Venue getVenue(){
        return venue;
    }

    public String getConnectionId(){
        if (clientProfile!=null){
            return clientProfile.getConnectionId();
        }
        return null;
    }

    /**
     * updates the clientProfile if the clientId matches
     * @param clientProfile the new clientProfile
     *
     * @return true if the profile was changed false otherwise
     */
    public boolean updateClientProfile(ClientProfile clientProfile){
        if (this.clientProfile.equals(clientProfile)){
            this.clientProfile = clientProfile;
            return true;
        }
        return false;
    }

}
