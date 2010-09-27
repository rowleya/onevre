/*
 * @(#)DataSource.java
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

import javax.media.Time;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

/**
 * This DataSource reads the new encoded AG stream.
 * @author Andrew G D Rowley, Sebastian Starke
 * @version 1.0
 */
public class DataSource extends PushBufferDataSource {

    /** True if the source has started. */
    private boolean started = false;

    /** The content type of the source. */
    private String contentType = "raw";

    /** True if the source has been connected. */
    private boolean connected = false;

    /** The duration of the source. */
    private Time duration = DURATION_UNBOUNDED;

    /** The streams of the source. */
    private AGStream[] streams = null;

    /** The stream of the source. */
    private AGStream stream = null;

    /**
     * Creates a new Screen datasource.
     */
    public DataSource() {
        streams = new AGStream[1];
        streams[0] = new AGStream();
        stream = streams[0];
    }

    /**
     * @see javax.media.protocol.DataSource#getContentType()
     */
    public final String getContentType() {
        if (!connected) {
            System.err.println("Error: DataSource not connected");
            return null;
        }
        contentType = stream.getFormat().getEncoding();
        return contentType;
    }

    /**
     * @see javax.media.protocol.DataSource#connect()
     */
    public final void connect() {
        if (connected) {
            return;
        }
        connected = true;
    }

    /**
     * @see javax.media.protocol.DataSource#disconnect()
     */
    public final void disconnect() {
        if (started) {
            stop();
        }
        connected = false;
    }

    /**
     * @see javax.media.protocol.DataSource#start()
     */
    public final void start() {
        // we need to throw error if connect() has not been called
        if (!connected) {
            throw new java.lang.Error(
                    "DataSource must be connected before it can be started");
        }
        if (started) {
            return;
        }
        started = true;
    }

    /**
     * @see javax.media.protocol.DataSource#stop()
     */
    public final void stop() {
        if ((!connected) || (!started)) {
            return;
        }
        started = false;
    }

    /**
     * @see javax.media.Controls#getControls()
     */
    public final Object[] getControls() {
        return stream.getControls();
    }

    /**
     * @see javax.media.Controls#getControl(java.lang.String)
     */
    public final Object getControl(final String controlType) {
        return stream.getControl(controlType);
    }

    /**
     * @see javax.media.Duration#getDuration()
     */
    public final Time getDuration() {
        return duration;
    }

    /**
     * @see javax.media.protocol.PushBufferDataSource#getStreams()
     */
    public final PushBufferStream[] getStreams() {
        return streams;
    }
}
