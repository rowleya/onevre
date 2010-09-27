package com.googlecode.onevre.ag.agserver;

import com.googlecode.onevre.ag.common.interfaces.EventListener;
import com.googlecode.onevre.ag.types.EventDescription;
import com.googlecode.onevre.ag.types.VenueState;


/**
 * Implements the Listener for the VenueServer (the top level events)
 * @author Tobias M Schiebeck
 *
 */
public class VenueEventListener implements EventListener {

    private VenueState venueState=null;

    public String getListenerId() {
        return null;
    }

    public String getListenerUri() {
        return venueState.getUri();
    }

    public String getLocation() {
        return venueState.getEventLocation();
    }

    public void processEvent(EventDescription event, String soapMessage) {
        // TODO Auto-generated method stub

    }

}
