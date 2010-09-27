/*
 * @(#)RtpCheckProcessor.java
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

package com.googlecode.onevre.protocols.rtp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;


import com.googlecode.onevre.protocols.rtcp.UdpPacketProcessor;
import com.googlecode.vicovre.media.rtp.RTPHeader;


/**
 * A processor for checking the validity of RTP packets
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class RtpCheckProcessor implements UdpPacketProcessor {

    private HashMap<Long, Integer> receivedPacketCount =
        new HashMap<Long, Integer>();

    private HashMap<Long, Integer> missingPacketCount =
        new HashMap<Long, Integer>();

    private HashMap<Long, Boolean> ssrcMap = new HashMap<Long, Boolean>();

    private HashMap<Long, Integer> typeMap = new HashMap<Long, Integer>();

    private HashMap<Long, Integer> validCountMap = new HashMap<Long, Integer>();


    private HashMap<Long, Integer> lastSequenceMap =
        new HashMap<Long, Integer>();

    private short version = 0;

    /**
     * Creates a new RtpCheckProcessor
     * @param version The RTP version
     */
    public RtpCheckProcessor(short version) {
        this.version = version;
    }

    /**
     * Processes an RTP packet
     * <dl><dt><b>overrides:</b></dt><dd>{@link pag.clientbridge.UdpPacketProcessor#process(java.net.DatagramPacket)}</dd></dl>
     * @param packet The RTP packet to process
     * @return True if the packet is ok and should be forwarded
     */
    public boolean process(DatagramPacket packet) {
        RTPHeader header;
        try {
            header = new RTPHeader(packet);
        } catch (IOException e) {

            //invalid header (package to short)
            return false;
        }
        if (header.getVersion() != version) {
            return false;
        }
        int type = header.getPacketType();
        if ((type >= 72) && (type <= 76)) {
            return false;
        }

        int sequence = header.getSequence();

        final long ssrc = header.getSsrc();
        if (ssrcMap.containsKey(ssrc)) {
            synchronized (receivedPacketCount) {
                receivedPacketCount.put(ssrc,
                        receivedPacketCount.get(ssrc) + 1);
            }
            int nextSequence = lastSequenceMap.get(ssrc) + 1;
            if (nextSequence > RTPHeader.MAX_SEQUENCE) {
                nextSequence = 0;
            }
            if (sequence > nextSequence) {
                if (sequence - nextSequence < 100) {
                    synchronized (missingPacketCount) {
                        missingPacketCount.put(ssrc,
                                missingPacketCount.get(ssrc)
                                + (sequence - nextSequence));
                    }
                    lastSequenceMap.put(ssrc, sequence);
                }
            } else if (sequence == nextSequence) {
                lastSequenceMap.put(ssrc, sequence);
            }
            return true;
        }

        if (!validCountMap.containsKey(ssrc)) {
            validCountMap.put(ssrc, 0);
            typeMap.put(ssrc, type);
            lastSequenceMap.put(ssrc, sequence);
            return false;
        }
        int validCount = validCountMap.get(ssrc);
        int lastType = typeMap.get(ssrc);
        int lastSequence = lastSequenceMap.get(ssrc);
        if (type != lastType) {
            validCountMap.remove(ssrc);
            return false;
        }

        if (sequence + 1 > 65535) {
            sequence = 0 - (65535 - sequence + 1);
        }
        if ((sequence > (lastSequence + 100))
                || (sequence < (lastSequence - 100))) {
            validCountMap.remove(ssrc);
            return false;
        }
        lastSequenceMap.put(ssrc, sequence);

        validCount += 1;
        if (validCount >= 5) {
            ssrcMap.put(ssrc, true);
            synchronized (receivedPacketCount) {
                receivedPacketCount.put(ssrc, validCount);
            }
            synchronized (missingPacketCount) {
                missingPacketCount.put(ssrc, 0);
            }
            return true;
        }
        validCountMap.put(ssrc, validCount);
        return false;
    }

    /**
     * Gets the number of packets received for a source
     * @param ssrc The ssrc of the source
     * @return The number of packets received
     */
    public int getReceivedPacketCount(long ssrc) {
        int recv = 0;
        synchronized (receivedPacketCount) {
            if (receivedPacketCount.containsKey(ssrc)) {
                recv = receivedPacketCount.get(ssrc);
            }
        }
        return recv;
    }

    /**
     * Gets the list of SSRCs seen
     * @return The list of ssrcs
     */
    public Long[] getSsrcs() {
        synchronized (receivedPacketCount) {
            return receivedPacketCount.keySet().toArray(new Long[0]);
        }
    }

    /**
     * Gets the number of packets missed
     * @param ssrc The source to get the missing packets for
     * @return The number of packets missed
     */
    public int getMissingPacketCount(long ssrc) {
        int recv = 0;
        synchronized (missingPacketCount) {
            if (missingPacketCount.containsKey(ssrc)) {
                recv = missingPacketCount.get(ssrc);
            }
        }
        return recv;
    }

    /**
     * Gets the fraction of missing packets to total packets
     * @param ssrc The source to get the count for
     * @return The fraction of packets missing
     */
    public float getMissingPacketFraction(long ssrc) {
        int received = getReceivedPacketCount(ssrc);
        int missing = getMissingPacketCount(ssrc);
        receivedPacketCount.put(ssrc, 0);
        missingPacketCount.put(ssrc, 0);
        if ((missing + received) == 0) {
            return 0;
        }
        return ((float) missing / (float) (missing + received));
    }

    /**
     * Clears the record of sources seen
     *
     */
    public void clearSources() {
        lastSequenceMap.clear();
        missingPacketCount.clear();
        receivedPacketCount.clear();
        ssrcMap.clear();
        typeMap.clear();
        validCountMap.clear();
    }
}
