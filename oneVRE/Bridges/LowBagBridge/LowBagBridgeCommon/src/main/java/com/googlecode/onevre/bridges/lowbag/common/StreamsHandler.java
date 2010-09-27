/*
 * @(#)StreamsHandler.java
 * Created: 11 Feb 2010
 * Version: 1.0
 * Copyright (c) 2005-2010, University of Manchester All rights reserved.
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

package com.googlecode.onevre.bridges.lowbag.common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.media.Format;



import com.googlecode.onevre.protocols.rtp.RtpWriter;
import com.googlecode.vicovre.media.rtp.RTCPReport;

/**
 * @author Sebastian Starke
 * @version 1.0
 */
public class StreamsHandler extends Thread  {

    /** The calling client object. */
    private LowBagClientInterface client = null;

    /** Hash map of all streams. */
    private HashMap<String, Object> streams = new HashMap<String, Object>();

    /** Interval how often the stream information is being updated. */
    private long updateInterval =
        LowBagDefaults.DEFAULT_REPORT_UDPATE_INTERVAL;

    /** Stream syncronization variable. */
    private Boolean streamsSync = true;

    /**
     * Sets the the client if class was invoked by it.
     * @param client The class calling client object.
     */
    public final void setClient(final LowBagClientInterface client) {
        this.client = client;
    }

    /**
     * Removes a stream of the stream handler.
     * @param ssrc Unique stream identifier
     * @throws IOException Error, with the handed over SSRC!
     * @throws IOException Stream does not exist!
     */
    public final void removeStream(final String ssrc) throws IOException {
        if (ssrc == null) {
            throw new IOException("Error, with the handed over SSRC!");
        }
        synchronized (streamsSync) {
            if (!streams.containsKey(ssrc)) {
                throw new IOException("Stream does not exist!");
            }
            if (client != null) {
                client.removeReportSSRC(ssrc);
            }
            streams.remove(ssrc);
            streamsSync.notifyAll();
        }
    }

    /**
     * Checks if the stream was active in a certain amount of time,
     * if not it removes the stream of the stream handler.
     */
    public final void checkStreamActivity() {
        StreamHandler streamHandler = null;
        int n = 0;
        long timeLastSend = 0;
        Map.Entry e = null;
        String[] remove = null;
        synchronized (streamsSync) {
            long time = System.currentTimeMillis();
            remove = new String[streams.size()];
            Iterator i = streams.entrySet().iterator();
            while (i.hasNext()) {
                e = (Map.Entry) i.next();
                streamHandler = (StreamHandler) e.getValue();
                timeLastSend = streamHandler.getTimeLastSend();
                if (timeLastSend != 0) {
                    if (time > (timeLastSend +
                            LowBagDefaults.RTP_UPDATE_TIME)) {
                        remove[n++] = e.getKey().toString();
                    }
                }
            }
            streamsSync.notifyAll();
        }
        //- remove keys
        for (int j = 0; j < n; j++) {
            try {
                removeStream(remove[j]);
            } catch (IOException e1) {
                // Do Nothing
            }
        }
    }

    /**
     * Gets a hash map of all available streams.
     * @return Hash map of all available streams.
     */
    public final HashMap<String, Object> getStreams() {
        HashMap<String, Object> streams2 = null;
        synchronized (streamsSync) {
            streams2 = streams;
            streamsSync.notifyAll();
        }
        return streams2;
    }

    /**
     * Gets the interval how often the stream
     * information is beeing updated.
     * @return The update interval
     */
    public final long getUpdateInterval() {
        return updateInterval;
    }

    /**
     * Sets the interval how often the stream
     * information should be updated.
     * @param interval New interval to be set
     */
    public final void setUpdateInterval(final long interval) {
        updateInterval = interval;
    }

    /**
     * Updates the information corresponding to a stream.
     * @param ssrc Unique stream identifier
     * @param packet Packet used for the update
     * @param report RTCP Report used for the update
     * @throws IOException Error with handed over SSRC!
     * @throws IOException updateRenderer()
     * @throws IOException setReport()
     * @throws IOException addUpdate()
     * @throws IOException getReportRate()
     */
    public final void updateStream(final String ssrc,
                final DatagramPacket packet, final RTCPReport report)
            throws IOException  {
        if (ssrc == null) {
            throw new IOException("Error with handed over SSRC!");
        }
        long time = System.currentTimeMillis();
        StreamHandler streamHandler = getStream(ssrc);
        if (streamHandler == null) {
            streamHandler = addStream(ssrc);
        }
        if (packet != null) {
            try {
                streamHandler.updateRenderer(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //- supported format
        if (streamHandler.isSupported()) {
            streamHandler.setTimeLastSend(time);
            if (report != null) {
                streamHandler.setReport(report);
            }

            //- check time since last update
            long lastUpdate = streamHandler.getLastUpdate();
            if (lastUpdate != 0) {
                if ((lastUpdate + updateInterval) < time) {
                    client.setReportSSRC(ssrc);
                    if (client.isAutoRatio()) {
                        streamHandler.setLastBitRate(
                               streamHandler.getRenderer().getBitRate());
                        streamHandler.setLastFrameRate(
                               streamHandler.getRenderer().getFrameRate());
                        client.addUpdate(ssrc, "rate",
                               client.getReportRate(
                                       streamHandler.getReportBox()));
                        client.addUpdate(ssrc, "frameRate",
                               streamHandler.getLastFrameRate());
                    }
                    streamHandler.setLastUpdate(time);
                }
            } else {
                long initTime = streamHandler.getInitTime();
                if (initTime != 0) {
                    if ((initTime + LowBagDefaults.REPORT_INIT_DELAY) > time) {
                        client.setReportSSRC(ssrc);
                    } else {
                        streamHandler.setLastUpdate(time);
                        client.setReportSSRC(ssrc);
                    }
                } else {
                    streamHandler.setInitTime(time);
                }
            }

        //- unsupported format
        } else if (!streamHandler.isAudio()) {
            streamHandler.setTimeLastSend(time);
            if (report != null) {
                streamHandler.setReport(report);
            }
            //- check time since last update
            long lastUpdate = streamHandler.getLastUpdate();
            if ((lastUpdate + updateInterval) < time) {
                client.setReportSSRC(ssrc);
                streamHandler.setLastUpdate(time);
            }
        }
        synchronized (streamsSync) {
            streams.put(ssrc, streamHandler);
            streamsSync.notifyAll();
        }
    }

    /**
     * Clears all streams.
     */
    public final void clearStreams() {
        synchronized (streamsSync) {
            streams.clear();
            streamsSync.notifyAll();
        }
    }

    /**
     * Gets the stream handler of a particular requested stream.
     * @param ssrc Unique stream identifier
     * @return Stream handler of requested stream.
     */
    public final StreamHandler getStream(final String ssrc) {
        StreamHandler streamHandler = null;
        if (streams.containsKey(ssrc)) {
            streamHandler = (StreamHandler) streams.get(ssrc);
        }
        return streamHandler;
    }

    /**
     * Adds a new stream to the streams handler.
     * @param ssrc Unique stream identifier
     * @return Stream handler object for the added stream.
     */
    public final StreamHandler addStream(final String ssrc) {
        StreamHandler streamHandler = null;
        synchronized (streamsSync) {
            if (!streams.containsKey(ssrc)) {
                streamHandler = new StreamHandler(Long.parseLong(ssrc));
                streams.put(ssrc, streamHandler);
            } else {
                streamHandler = (StreamHandler) streams.get(ssrc);
            }
            streamsSync.notifyAll();
        }
        return streamHandler;
    }

    /**
     * Adds a new stream to the streams handler.
     * @param me Corresponding RtpWriter
     * @param address Packet destination address
     * @param ssrc Unique stream identifier
     * @param format Destination format
     */
    public final void addStream(final RtpWriter me,
            final InetSocketAddress address,
            final long ssrc, final Format format) {
        synchronized (streamsSync) {
            if (!streams.containsKey(ssrc)) {
                StreamHandler streamHandler = new StreamHandler(
                        me, address, ssrc, format);
                streamHandler.start();
                streams.put(Long.toString(ssrc), streamHandler);
            }
            streamsSync.notifyAll();
        }
    }
}
