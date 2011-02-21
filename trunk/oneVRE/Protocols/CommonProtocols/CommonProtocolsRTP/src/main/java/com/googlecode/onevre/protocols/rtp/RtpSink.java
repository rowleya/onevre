/*
 * @(#)RTPSink.java
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

package com.googlecode.onevre.protocols.rtp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import com.googlecode.vicovre.media.rtp.RTPPacketSink;

/**
 * @author Sebastian Starke
 *
 */
public class RtpSink implements RTPPacketSink {

    /** Destination address for packet. */
    private InetSocketAddress address = null;

    /** Object of the ClientWriter. */
    private RtpWriter writer = null;

    /** Size of an SSRC in byte. */
    private static final int SSRC_SIZE = 4;

    /** Size of an BYTE in bit. */
    private static final int BYTE_SIZE = 8;

    /** Stream identifier. */
    private byte[] ssrc = new byte[SSRC_SIZE];

    /**
     * Default constructor.
     * @param rtpWriter Object of the rtpWriter
     * @param address Destination address
     * @param ssrc SSRC to write into packet
     */
    public RtpSink(final RtpWriter rtpWriter,
            final InetSocketAddress address, final long ssrc) {
        this.writer = rtpWriter;
        this.address = address;
        for (int i = 0; i < SSRC_SIZE; i++) {
            this.ssrc[(SSRC_SIZE - 1) - i] = (byte) (ssrc >>> (i * BYTE_SIZE));
        }
    }

    /**
     * Replaces the SSRC and adds packet into the writer queue.
     * @see com.googlecode.vicovre.media.rtp.RTPPacketSink#handleRTPPacket(java.net.DatagramPacket)
     */
    public final void handleRTPPacket(final DatagramPacket packet)
            throws IOException {
        if (address == null || writer == null) {
            throw new IOException();
        }
        //- ssrc: rtp.header offset + 64bit offset, read 32 bit
        byte[] data = packet.getData();
        int offset = packet.getOffset();
        System.arraycopy(ssrc, 0, data, offset + BYTE_SIZE, SSRC_SIZE);
        packet.setData(data);
        writer.addPacket(packet, address);
    }
}
