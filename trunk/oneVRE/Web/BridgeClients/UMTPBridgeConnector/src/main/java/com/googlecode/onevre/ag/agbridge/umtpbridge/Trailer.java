/*
 * @(#)Trailer.java
 * Created: 24 May 2008
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

package com.googlecode.onevre.ag.agbridge.umtpbridge;

/**
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Trailer {

    /**
     * Data packet
     */
    public static final byte COMMAND_DATA = 1;

    /**
     * Join group request
     */
    public static final byte COMMAND_JOIN_GROUP = 2;

    /**
     * Leave group request
     */
    public static final byte COMMAND_LEAVE_GROUP = 3;

    /**
     * Tear down session request
     */
    public static final byte COMMAND_TEAR_DOWN = 4;

    /**
     * Proble request
     */
    public static final byte COMMAND_PROBE = 5;

    /**
     * Ack response to probe
     */
    public static final byte COMMAND_PROBE_ACK = 6;

    /**
     * Nack response to probe
     */
    public static final byte COMMAND_PROBE_NACK = 7;

    /**
     * Request to join RTP session
     */
    public static final byte COMMAND_JOIN_RTP_GROUP = 8;

    /**
     * Request to leave RTP session
     */
    public static final byte COMMAND_LEAVE_RTP_GROUP = 9;

    private byte[] sourceAddress = new byte[4];

    private int sourceCookie = 0;

    private int destinationCookie = 0;

    private byte[] multicastAddress = new byte[4];

    private int port = 0;

    private short ttl = 0;

    private boolean shortHeader = true;

    private byte version = 0;

    private byte command = 0;

    /**
     * Creates a new Trailer
     *
     * @param sourceAddress The SSL source address
     * @param sourceCookie The source cookie
     * @param destinationCookie The destination cookie
     * @param multicastAddress The multicast address
     * @param port The port
     * @param ttl The ttl
     * @param version The version
     * @param command The command
     */
    public Trailer(byte[] sourceAddress, int sourceCookie,
            int destinationCookie, byte[] multicastAddress, int port,
            short ttl, byte version, byte command) {
        this.sourceAddress = sourceAddress;
        this.sourceCookie = sourceCookie;
        this.destinationCookie = destinationCookie;
        this.multicastAddress = multicastAddress;
        this.port = port;
        this.ttl = ttl;
        this.shortHeader = true;
        this.version = version;
        this.command = command;
    }

    /**
     * Creates a new Trailer
     *
     * @param sourceCookie The source cookie
     * @param destinationCookie The destination cookie
     * @param multicastAddress The multicast address
     * @param port The port
     * @param ttl The ttl
     * @param version The version
     * @param command The command
     */
    public Trailer(int sourceCookie, int destinationCookie,
            byte[] multicastAddress, int port, short ttl, byte version,
            byte command) {
        this.sourceCookie = sourceCookie;
        this.destinationCookie = destinationCookie;
        this.multicastAddress = multicastAddress;
        this.port = port;
        this.ttl = ttl;
        this.shortHeader = false;
        this.version = version;
        this.command = command;
    }

    /**
     * Reads a new Trailer
     * @param data The data to read from
     * @param offset The offset that the valid data starts from
     * @param length The length of the packet
     */
    public Trailer(byte[] data, int offset, int length) {
        int flags = data[offset + length - 1];
        command = (byte) (flags & 0x0F);
        version = (byte) ((flags >> 4) & 0x08);
        shortHeader = ((data[offset + length - 1] & 0x80) == 0);
        ttl = data[offset + length - 2];
        port = (data[offset + length - 4] << 8) | (data[offset + length - 3]);
        for (int i = 0; i < multicastAddress.length; i++) {
            multicastAddress[i] = data[offset + length - (8 - i)];
        }
        destinationCookie = (data[offset + length - 10] << 8)
            | (data[offset + length - 9]);
        sourceCookie = (data[offset + length - 12] << 8)
            | (data[offset + length - 11]);
        if (!shortHeader) {
            for (int i = 0; i < multicastAddress.length; i++) {
                sourceAddress[i] = data[offset + length - (16 - i)];
            }
        }
    }

    /**
     * Returns the command
     * @return the command
     */
    public byte getCommand() {
        return command;
    }

    /**
     * Returns the destinationCookie
     * @return the destinationCookie
     */
    public int getDestinationCookie() {
        return destinationCookie;
    }

    /**
     * Returns the multicastAddress
     * @return the multicastAddress
     */
    public byte[] getMulticastAddress() {
        return multicastAddress;
    }

    /**
     * Returns the port
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the shortHeader
     * @return the shortHeader
     */
    public boolean isShortHeader() {
        return shortHeader;
    }

    /**
     * Returns the sourceAddress
     * @return the sourceAddress
     */
    public byte[] getSourceAddress() {
        return sourceAddress;
    }

    /**
     * Returns the sourceCookie
     * @return the sourceCookie
     */
    public int getSourceCookie() {
        return sourceCookie;
    }

    /**
     * Returns the ttl
     * @return the ttl
     */
    public short getTtl() {
        return ttl;
    }

    /**
     * Returns the version
     * @return the version
     */
    public byte getVersion() {
        return version;
    }

    public void addToBytes(byte[] trailer, int offset, int length) {
        if (shortHeader) {
            for (int i = 0; i < sourceAddress.length; i++) {
                trailer[offset + length - (16 - i)] = sourceAddress[i];
            }
        }
        trailer[offset + length - 12] = (byte) ((sourceCookie >> 8) & 0xFF);
        trailer[offset + length - 11] = (byte) (sourceCookie & 0xFF);
        trailer[offset + length - 10] = (byte)
            ((destinationCookie >> 8) & 0xFF);
        trailer[offset + length - 9] = (byte) (destinationCookie & 0xFF);
        for (int i = 0; i < multicastAddress.length; i++) {
            trailer[offset + length - (8 + i)] = multicastAddress[i];
        }
        trailer[offset + length - 4] = (byte) ((port >> 8) & 0xFF);
        trailer[offset + length - 3] = (byte) (port & 0xFF);
        trailer[offset + length - 2] = (byte) ttl;
        trailer[offset + length - 1] = (byte) (((version & 0x8) << 4)
                | (command & 0xFF));
        if (!shortHeader) {
            trailer[offset + 11] |= 0x80;
        }
    }

    public byte[] asBytes() {
        byte[] trailer = null;
        if (shortHeader) {
            trailer = new byte[12];
        } else {
            trailer = new byte[16];
        }

        return trailer;
    }
}
