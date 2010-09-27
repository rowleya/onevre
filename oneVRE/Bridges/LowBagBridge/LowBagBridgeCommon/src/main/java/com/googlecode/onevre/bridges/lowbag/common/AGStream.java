/*
 * @(#)AGStream.java
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
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Timer;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Format;
import javax.media.PlugIn;
import javax.media.control.FrameRateControl;
import javax.media.format.UnsupportedFormatException;
import javax.media.format.VideoFormat;
import javax.media.format.YUVFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;

import com.googlecode.vicovre.media.processor.SimpleProcessor;
import com.googlecode.vicovre.media.rtp.RTPHeader;
import com.googlecode.vicovre.repositories.rtptype.RTPType;

/**
 * Encoding an AG stream rather a (YUV) frame to a new format.
 * @author Andrew G D Rowley, Sebastian Starke
 * @version 1.0
 */
public class AGStream extends Thread
        implements PushBufferStream, FrameRateControl {

    /** The content descriptor. */
    private ContentDescriptor cd = new ContentDescriptor(
            ContentDescriptor.RAW);

    /** The videoformat to send data in. */
    private VideoFormat format;

    /** The rate of the frames to send. */
    private float frameRate = LowBagDefaults.DEFAULT_FRAME_RATE;

    /** The handler of transfers. */
    private BufferTransferHandler transferHandler;

    /** The number of ms in a second. */
    private static final int SECS_TO_MS = 1000;

    /** The controls of the stream. */
    private Control[] controls = new Control[]{this};

    /** A Timer to time events with. */
    private Timer timer = null;

    /** The processor object. */
    private SimpleProcessor processor = null;

    /** Last sequence number. */
    private long lastSequence = -1;

    /** Timestamp. */
    private long timestamp = 0;

    /** Fist timestamp used. */
    private long firstTimestamp = -1;

    /** Incomming video format. */
    private VideoFormat videoFormat = new YUVFormat(YUVFormat.YUV_420);

    /** Thread control/synchronisation variable for "newFormat". */
    private Boolean formatRead = false;

    /** (New) format for outgoing packes. */
    private Format newFormat = null;

    /** Proccessing result which needs to by synchronized. */
    private int result = 0;

    /** Percentage of frames to be send out. */
    private float framePercent = 1;

    /** Thread control/synchronization variable for "result". */
    private Integer resultSync = 0;

    /** For counting the tranfered frames. */
    private float frameCounter = 0;

    /**
     * Creates a new Stream for sending screens.
     */
    public AGStream() {

    }

    /**
     * @see javax.media.protocol.SourceStream#getContentDescriptor()
     */
    public final ContentDescriptor getContentDescriptor() {
        return cd;
    }

    /**
     * @see javax.media.protocol.SourceStream#getContentLength()
     */
    public final long getContentLength() {
        return LENGTH_UNKNOWN;
    }

    /**
     * @see javax.media.protocol.SourceStream#endOfStream()
     */
    public final boolean endOfStream() {
        return false;
    }

    /**
     * @see javax.media.protocol.PushBufferStream#getFormat()
     */
    public final Format getFormat() {
        synchronized (this) {
            while (!formatRead) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            notifyAll();
            return newFormat;
        }
   }

    /**
     * Reads the input buffer.
     * @see javax.media.protocol.PushBufferStream#read(javax.media.Buffer)
     */
    public final void read(final Buffer buffer) {
        Buffer outputBuffer = processor.getOutputBuffer();
        buffer.copy(outputBuffer);
    }

    /**
     * @see javax.media.protocol.PushBufferStream#setTransferHandler
     *      (javax.media.protocol.BufferTransferHandler)
     */
    public final void setTransferHandler(
            final BufferTransferHandler transferHandler2) {
        synchronized (this) {
            transferHandler = transferHandler2;
            notifyAll();
        }
    }

    /**
     * Gets the timestamp of the current packet.
     * @return The timestamp
     */
    public final long getTimestamp() {
        return (timestamp - firstTimestamp);
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public final void run() {
        synchronized (this) {
            while (transferHandler == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
        }
        synchronized (resultSync) {
            while (result != PlugIn.BUFFER_PROCESSED_OK) {
                try {
                    resultSync.wait();
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
            transferHandler.transferData(this);
        }
    }

    /**
     * Processes a packet rather decoded and encodeds
     * it to the required format.
     * @param packet The packet to process
     * @return Result of the processing.
     * @throws IOException RTPHeader()
     * @throws UnsupportedFormatException SimpleProcessor()
     */
    public final boolean process(final DatagramPacket packet)
            throws IOException, UnsupportedFormatException {

        //- read packet data
        byte[] data = packet.getData();
        int length = packet.getLength();
        int flags = 0;
        RTPHeader header = new RTPHeader(data, 0, length);
        int type = header.getPacketType();
        RTPType rtpType = LowBagDefaults.getSupportedFormats().findRtpType(type);
        format = (VideoFormat) rtpType.getFormat();
        timestamp = header.getTimestamp();
        long sequence = header.getSequence();
        if (lastSequence == -1) {
            lastSequence = sequence - 1;
        }
        if (sequence < lastSequence) {
            if (((lastSequence + 100) > 65535)
                    && (sequence < 100)) {
                lastSequence = 0 - (65536 - sequence);
            }
        }
        if (sequence > lastSequence) {
            lastSequence = sequence;
        } else {
            return false;
        }

        //- perpare buffer for processor
        Buffer inputBuffer = new Buffer();
        inputBuffer.setData(data);
        inputBuffer.setOffset(RTPHeader.SIZE);
        inputBuffer.setLength(length - RTPHeader.SIZE);
        inputBuffer.setTimeStamp(getTimestamp());
        inputBuffer.setSequenceNumber(header.getSequence());
        if (header.getMarker() == 1) {
            flags |= Buffer.FLAG_RTP_MARKER;
        }
        inputBuffer.setFlags(flags);
        inputBuffer.setFormat(format);

        //- prepare processor
        if (processor == null) {
            processor = new SimpleProcessor(format, videoFormat);
        }
        if (processor == null) {
            return false;
        }
        result = processor.process(inputBuffer);
        synchronized (this) {
            newFormat = processor.getOutputBuffer().getFormat();
            formatRead = true;
            notifyAll();
        }

        //- transfer processed data
        synchronized (this) {
            while (transferHandler == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            notifyAll();
        }
        if (result == PlugIn.BUFFER_PROCESSED_OK) {
            frameCounter += framePercent;
            if (frameCounter >= 1) {
                transferHandler.transferData(this);
                frameCounter -= 1;
            }
        }
        return true;
    }

    /**
     * @see javax.media.Controls#getControls()
     */
    public final Object[] getControls() {
        return controls;
    }

    /**
     * @see javax.media.Controls#getControl(java.lang.String)
     */
    public final Object getControl(final String controlType) {
        try {
            Class< ? > cls = Class.forName(controlType);
            Object[] cs = getControls();
            for (int i = 0; i < cs.length; i++) {
                if (cls.isInstance(cs[i])) {
                    return cs[i];
                }
            }
            return null;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @see javax.media.control.FrameRateControl#setFrameRate(float)
     */
    public final float setFrameRate(final float frameRate) {
        this.frameRate = frameRate;
        timer.cancel();
        timer = new Timer();
        timer.scheduleAtFixedRate(new StreamTimerTask(this), 0,
                (long) (SECS_TO_MS / frameRate));
        return frameRate;
    }

    /**
     * Sets the percentage/ratio of frames to be send out.
     * @param percent New percentage/ratio
     * @return Percentage/ratio of frames send out.
     */
    public final float setRatio(final int percent) {
        framePercent = ((float) percent / 100);
        frameCounter = 0;
        return framePercent;
    }

    /**
     * Gets the percentage/ratio of all frames send out.
     * @return Percentage/ratio of all frames send out.
     */
    public final int getRatio() {
        return (int) (framePercent * 100);
    }

    /**
     * @see javax.media.control.FrameRateControl#getFrameRate()
     */
    public final float getFrameRate() {
        return frameRate;
    }

    /**
     * @see javax.media.control.FrameRateControl#getMaxSupportedFrameRate()
     */
    public final float getMaxSupportedFrameRate() {
        return -1;
    }

    /**
     * @see javax.media.control.FrameRateControl#getPreferredFrameRate()
     */
    public final float getPreferredFrameRate() {
        return LowBagDefaults.DEFAULT_FRAME_RATE;
    }

    /**
     * @see javax.media.Control#getControlComponent()
     */
    public final Component getControlComponent() {
        return null;
    }
}
