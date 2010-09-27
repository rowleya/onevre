/*
 * @(#)Convert.java
 * Created: 30 May 2008
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

package com.googlecode.onevre.ag.clientbridge;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.ag.types.network.UnicastNetworkLocation;

import com.googlecode.vicovre.media.rtp.RTCPHeader;

/**
 * Utilities for converting network addresses
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Convert {

    private Convert() {
        // Does Nothing
    }

    /**
     * Gets an RTCP equivalent location for an RTP location
     * @param location The RTP location
     * @return The RTCP location
     */
    public static NetworkLocation getRtcpLocation(NetworkLocation location) {
        NetworkLocation rtcpLocation;
        if (location instanceof MulticastNetworkLocation) {
            rtcpLocation = new MulticastNetworkLocation();
            ((MulticastNetworkLocation) rtcpLocation).setTtl(
                    ((MulticastNetworkLocation) location).getTtl());
        } else {
            rtcpLocation = new UnicastNetworkLocation();
        }
        rtcpLocation.setHost(location.getHost());
        rtcpLocation.setPort(location.getPort() + 1);
        return rtcpLocation;
    }

    /**
     * Gets an RTCP equivalent address for an RTP address
     * @param address The RTP address
     * @return The RTCP address
     */
    public static InetSocketAddress getRtcpSocketAddress(
            InetSocketAddress address) {
        return new InetSocketAddress(address.getAddress(),
                address.getPort() + 1);
    }

    /**
     * Gets a bye packet for an ssrc
     * @param ssrc The ssrc to get the bye for
     * @return The bye packet
     */
    public static byte[] getBye(long ssrc) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);
        try {
            byte[] bye = null;
            RTCPHeader rrHeader =
                new RTCPHeader(false, 0, RTCPHeader.PT_RR, 1, ssrc);
            RTCPHeader byeHeader =
                new RTCPHeader(false, 1, RTCPHeader.PT_BYE, 1, ssrc);
            output.write(rrHeader.getBytes());
            output.write(byeHeader.getBytes());
            output.close();
            bytes.close();
            bye = bytes.toByteArray();
            return bye;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
