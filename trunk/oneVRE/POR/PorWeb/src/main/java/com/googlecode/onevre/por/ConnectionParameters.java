/*
 * Copyright (c) 2008, University of Manchester All rights reserved.
 * See LICENCE in root directory of source code for details of the license.
 */

package com.googlecode.onevre.por;

import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;


/**
 * Parameters used to test a bridge
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ConnectionParameters {

    private MulticastNetworkLocation location = null;

    private long multissrc = 0;

    private long ssrc = 0;

    /**
     * Creates a new ConnectionParameters
     * @param location The location to test using
     * @param multissrc The ssrc that will be received
     * @param ssrc The ssrc to use to send
     */
    public ConnectionParameters(MulticastNetworkLocation location,
            long multissrc, long ssrc) {
        this.location = location;
        this.multissrc = multissrc;
        this.ssrc = ssrc;
    }

    /**
     * Returns the location
     * @return the location
     */
    public MulticastNetworkLocation getLocation() {
        return location;
    }

    /**
     * Returns the multissrc
     * @return the multissrc
     */
    public long getMultissrc() {
        return multissrc;
    }

    /**
     * Returns the ssrc
     * @return the ssrc
     */
    public long getSsrc() {
        return ssrc;
    }
}
