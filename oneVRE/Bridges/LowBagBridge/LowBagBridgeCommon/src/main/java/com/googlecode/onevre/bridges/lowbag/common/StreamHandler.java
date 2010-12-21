/*
 * @(#)StreamHandler.java
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

import java.awt.Component;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import javax.media.Buffer;
import javax.media.Effect;
import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.format.VideoFormat;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.renderer.VideoRenderer;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SendStream;
import javax.swing.JLabel;




import com.googlecode.onevre.protocols.rtp.RtpSink;
import com.googlecode.onevre.protocols.rtp.RtpWriter;
import com.googlecode.vicovre.media.Misc;
import com.googlecode.vicovre.media.processor.SimpleProcessor;
import com.googlecode.vicovre.media.renderer.RGBRenderer;
import com.googlecode.vicovre.media.rtp.LocalRTPConnector;
import com.googlecode.vicovre.media.rtp.RTCPReport;
import com.googlecode.vicovre.media.rtp.RTPHeader;
import com.googlecode.vicovre.repositories.rtptype.RTPType;

/**
 * Handles a single stream (decoding/proccessing).
 * @author Sebastian Starke
 * @version 1.0
 */
public class StreamHandler extends Thread  {

    /** Object of the stream. */
    private AGStream stream = null;

    /** Object of the rtpWriter. */
    private RtpWriter rtpWriter = null;

    /** Packet destination address. */
    private InetSocketAddress address = null;

    /** Unique stream identifier. */
    private long ssrc = 0;

    /** Processing control variable. */
    private boolean failed = false;

    /** Procssing output format. */
    private Format outputFormat = new VideoFormat(
            LowBagDefaults.DEFAULT_OUTPUT_FORMAT);

    /** Object of the stream renderer. */
    private RGBRenderer renderer = null;

    /** Time of the last update. */
    private long lastUpdate = 0;

    /** The time of the initiation. */
    private long initTime = 0;

    /** Object of the RTCP report. */
    private RTCPReport report = null;

    /** Report box object. */
    private Object reportBox = null;

    /** If stream is in a supported format. */
    private boolean isSupported = false;

    /** If stream is audio stream. */
    private boolean isAudio = false;

    /** Frame rate of the last frame rate query. */
    private float lastFrameRate = 0.0f;

    /** Bit rate of the last bit rate query. */
    private long lastBitRate = 0;

    /** Defines if data will be received or not by this stream. */
    private boolean isReceiving = true;

    /** The time of the last packet send. */
    private long lastSend = 0;

    /**
     * Constructor.
     * @param ssrc Stream identifier
     */
    public StreamHandler(final long ssrc) {
        this.ssrc = ssrc;
    }

    /**
     * Constructor.
     * @param rtpWriter Corresponding RtpWriter
     * @param address Destination address for the packets
     * @param ssrc Stream identifier
     * @param outputFormat Required output format
     */
    public StreamHandler(final RtpWriter rtpWriter, final InetSocketAddress address,
            final long ssrc, final Format outputFormat) {
        this.rtpWriter = rtpWriter;
        this.address = address;
        this.ssrc = ssrc;
        this.outputFormat = outputFormat;
    }

    /**
     * Process a packet.
     * @param packet Packet to proccess.
     */
    public final void process(final DatagramPacket packet) {
        synchronized (this) {
            if (failed) {
                rtpWriter.addPacket(packet, address);
                return;
            }
            while (stream == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                stream.process(packet);
            } catch (Exception e) {
                failed = true;
                rtpWriter.addPacket(packet, address);
            }
            notifyAll();
        }
    }

    /**
     * Gets if stream is encoded in a supported video format.
     * @return If stream is encoded in a supported video format.
     */
    public final boolean isSupported() {
        return isSupported;
    }

    /**
     * Gets if stream is encoded in a supported audio format.
     * @return If stream is encoded in a supported audio format.
     */
    public final boolean isAudio() {
        return isAudio;
    }

    /**
     * @see java.lang.Thread#run()
     */
    public final void run() {
        try {
            Misc.configureCodecs(LowBagDefaults.CODECS_PATH);

            //- Preapare data
            DataSource dataSource = new DataSource();
            dataSource.connect();
            dataSource.start();
            LocalRTPConnector rtpConnector = new LocalRTPConnector();
            rtpConnector.setRTPSink(new RtpSink(rtpWriter, address, ssrc));
            PushBufferStream[] datastreams = (
                    (PushBufferDataSource) dataSource).getStreams();
            synchronized (this) {
                stream = (AGStream) datastreams[0];
                notifyAll();
            }

            //- Check if output format is supported!
            if (!rtpWriter.checkFormatSupport(outputFormat)) {
                throw new IOException("Unsupported format!");
            }

            //- Create our new RTP manager instance
            RTPManager rtpManager = RTPManager.newInstance();
            rtpManager.addFormat(outputFormat, LowBagDefaults.DEFAULT_RTP_TYPE);
            rtpManager.initialize(rtpConnector);

            //- Setup and start the send stream
            SimpleProcessor processor = new SimpleProcessor(
                    stream.getFormat(), outputFormat);
            PushBufferDataSource data = processor.getDataOutput(dataSource, 0);
            SendStream sendStream = rtpManager.createSendStream(data, 0);
            sendStream.start();
            processor.start(dataSource, 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedFormatException e) {
            // Do Nothing
        } catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the frames ratio to be send out in percent.
     * @param rate Percentage required
     */
    public final void setRatio(final int rate) {
        synchronized (this) {
            while (stream == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                stream.setRatio(rate);
            } catch (Exception e) {
                // Do Nothing
            }
            notifyAll();
        }
    }

    /**
     * Updates the renderer, which creates the preview
     * and calulates bit- and framerate.
     * @param packet Packet to read
     * @throws IOException RTPHeader()
     */
    public final void updateRenderer(final DatagramPacket packet)
            throws IOException {

        //- read packet
        byte[] data = packet.getData();
        int length = packet.getLength();
        int flags = 0;
        RTPHeader header = new RTPHeader(data, 0, length);
        short type = header.getPacketType();
        RTPType rtpType = LowBagDefaults.getSupportedFormats().findRtpType(type);
        if (rtpType == null) {
            rtpType = LowBagDefaults.getAllFormats().findRtpType(type);
            if (rtpType != null && rtpType.getFormat() instanceof AudioFormat) {
                isAudio = true;
            }
            return;
        }
        isSupported = true;
        VideoFormat format2 = (VideoFormat) rtpType.getFormat();

        //- prepare buffer
        Buffer inputBuffer = new Buffer();
        inputBuffer.setData(data);
        inputBuffer.setOffset(RTPHeader.SIZE);
        inputBuffer.setLength(length - RTPHeader.SIZE);
        inputBuffer.setTimeStamp(header.getTimestamp());
        inputBuffer.setSequenceNumber(header.getSequence());
        if (header.getMarker() == 1) {
            flags |= Buffer.FLAG_RTP_MARKER;
        }
        inputBuffer.setFlags(flags);
        inputBuffer.setFormat(format2);

        //- prepare renderer and process buffer
        if (renderer == null) {
            renderer = new RGBRenderer(new Effect[0]);
            if (renderer.setInputFormat(inputBuffer.getFormat()) == null) {
                isSupported = false;
                return;
            }
        }
        renderer.process(inputBuffer);
    }

    /**
     * Gets a component with preview of the stream.
     * @param width Required preview width
     * @param height Required preview height
     * @return Component with preview of the stream.
     */
    public final Component getPreview(final int width, final int height) {
        return renderer.getPreviewComponent();
    }

    /**
     * Sets the RTCP report object of this stream.
     * @param report RTCP report
     */
    public final void setReport(final RTCPReport report) {
        this.report  = report;
    }

    /**
     * Gets the RTCP report object of this stream.
     * @return RTCP report object of this stream.
     */
    public final RTCPReport getReport() {
        return report;
    }

    /**
     * Sets the report box object of this stream.
     * @param reportBox2 Report box object
     */
    public final void setReportBox(final Object reportBox2) {
        reportBox = reportBox2;
    }

    /**
     * Gets the report box object of this stream.
     * @return Report box object of this stream.
     */
    public final Object getReportBox() {
        return reportBox;
    }

    /**
     * Gets the time of the last update.
     * @return Time of the last update.
     */
    public final long getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets the time of the last update.
     * @param lastUpdate2 Time of the last update
     */
    public final void setLastUpdate(final long lastUpdate2) {
        lastUpdate = lastUpdate2;
    }

    /**
     * Gets the object of the stream renderer.
     * @return Object of the stream renderer.
     */
    public final RGBRenderer getRenderer() {
        return renderer;
    }

    /**
     * Gets the stream initiation time.
     * @return The time of the initiation.
     */
    public final long getInitTime() {
        return initTime;
    }

    /**
     * Sets the stream initiation time.
     * @param initTime2 Stream initiation time
     */
    public final void setInitTime(final long initTime2) {
        initTime = initTime2;
    }

    /**
     * Gets the bit rate of the last bit rate query.
     * @return The last frame bit query.
     */
    public final long getLastBitRate() {
        return lastBitRate;
    }

    /**
     * Sets/saves the bit rate of the last bit rate query.
     * @param lastBitRate2 Bit rate
     */
    public final void setLastBitRate(final long lastBitRate2) {
        lastBitRate = lastBitRate2;
    }

    /**
     * Gets the frame rate of the last frame rate query.
     * @return The last frame rate query.
     */
    public final float getLastFrameRate() {
        return lastFrameRate;
    }

    /**
     * Sets/saves the frame rate of the last frame rate query.
     * @param lastFrameRate2 Frame rate
     */
    public final void setLastFrameRate(final float lastFrameRate2) {
        lastFrameRate = lastFrameRate2;
    }

    /**
     * Gets if the stream is receiving data or not.
     * @return if the stream is receiving data or not.
     */
    public boolean isReceiving() {
        return isReceiving;
    }

    /**
     * Sets if the stream is receving data or not.
     * @param isReceiving2 If the stream is receving data or not
     */
    public void setReceiving(boolean isReceiving2) {
        isReceiving = isReceiving2;
    }

    /**
     * Gets the time of the last packet send.
     * @return Time of the last packet send.
     */
    public long getTimeLastSend() {
        return lastSend;
    }

    /**
     * Sets the time of the last packet send.
     * @param lastSend2 Time of the last packet send
     */
    public void setTimeLastSend(long lastSend2) {
        lastSend = lastSend2;
    }
}
