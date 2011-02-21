/*
 * @(#)RtcpCheckProcessor.java
 * Created: 26 Oct 2007
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

package com.googlecode.onevre.protocols.rtcp;

import java.io.IOException;
import java.net.DatagramPacket;
import com.googlecode.vicovre.media.rtp.RTCPHeader;

/**
 * Class to check if the RTCP packages are valid
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class RtcpCheckProcessor implements UdpPacketProcessor {

    private short version = 0;

    /**
     * Constructor to set the version of the RTCP processor
     * @param version The RTCP version
     */
    public RtcpCheckProcessor(short version) {
        this.version = version;
    }

    /**
     * RTCP package processor
     * <dl><dt><b>overrides:</b></dt><dd>{@link pag.clientbridge.UdpPacketProcessor#process(java.net.DatagramPacket)}</dd></dl>
     * @param packet The RTCP packet to process
     * @return True if the packet is ok and should be forwarded
     */
    public boolean process(DatagramPacket packet) {

        RTCPHeader header;

        try {
            header = new RTCPHeader(packet);
        } catch (IOException e) {

            //invalid header (package to short)
            return false;
        }

        if (header.getVersion() != version) {
            return false;
        }

        if (((header.getLength() + 1) * 4) == packet.getLength()) {
            return false;
        }

        if (((header.getPacketType() != RTCPHeader.PT_SR)
                && (header.getPacketType() != RTCPHeader.PT_RR))) {
            return false;
        }

        int offset = packet.getOffset() + (header.getLength() + 1) * 4;
        int length = packet.getLength() - offset;
        byte[] data = packet.getData();
        while (length > 0) {
            try {
                RTCPHeader subHeader = new RTCPHeader(data, offset, length);
                int len = (subHeader.getLength() + 1) * 4;
                if (len > length) {
                    return false;
                }
                if (subHeader.getVersion() != version) {
                    return false;
                }
                offset += len;
                length -= len;
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

}
