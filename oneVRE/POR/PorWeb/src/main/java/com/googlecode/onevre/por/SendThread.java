/*
 * Copyright (c) 2008, University of Manchester All rights reserved.
 * See LICENCE in root directory of source code for details of the license.
 */

package com.googlecode.onevre.por;

import java.io.IOException;
import java.net.DatagramPacket;

import com.googlecode.onevre.ag.agbridge.BridgeClient;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;

/**
 * A Thread for sending packets
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class SendThread extends Thread {

    private static final int SAMPLES_PER_PACKET = 10;

    private static final int RTP_HEADER_SIZE = 12;

    private static final int MS_BETWEEN_PACKETS = 250;

    private static final int MAX_SEQUENCE = 65535;

    private static final long MAX_TIMESTAMP = 2L * Integer.MAX_VALUE;

    private static final byte VERSION = (byte) 0x80;

    private static final int INT_LENGTH = 4;

    private static final int SHORT_LENGTH = 2;

    private static final int BYTE_MASK = 0xFF;

    private static final int BITS_PER_BYTE = 8;

    private static final int SSRC_POSITION = 8;

    private static final int SEQUENCE_POSITION = 2;

    private static final int TIMESTAMP_POSITION = 4;

    private byte[] rtpPacket = new byte[SAMPLES_PER_PACKET + RTP_HEADER_SIZE];

    private BridgeClient bridge = null;

    private int sequenceNumber = (int) (Math.random() * MAX_SEQUENCE);

    private long timestamp = (long) (Math.random() * MAX_TIMESTAMP);

    private DatagramPacket rtpDatagram = new DatagramPacket(rtpPacket,
            rtpPacket.length);

    private MulticastNetworkLocation location = null;

    private boolean done = false;

    private Integer doneSync = new Integer(0);

    private static void putInt(byte[] array, int offset, long value) {
        for (int i = 0; i < INT_LENGTH; i++) {
            array[offset + i] = (byte)
                ((value >> (BITS_PER_BYTE * (INT_LENGTH - i - 1))) & BYTE_MASK);
        }
    }

    private static void putShort(byte[] array, int offset, int value) {
        for (int i = 0; i < SHORT_LENGTH; i++) {
            array[offset + i] = (byte)
                ((value >> (BITS_PER_BYTE * (SHORT_LENGTH - i - 1)))
                        & BYTE_MASK);
        }
    }

    /**
     * Creates a new SendThread
     * @param bridge The bridge to send to
     * @param location The location to send to
     * @param ssrc The ssrc to send with
     */
    public SendThread(BridgeClient bridge, MulticastNetworkLocation location,
            long ssrc) {
        this.bridge = bridge;
        this.location = location;
        rtpPacket[0] = VERSION;
        putInt(rtpPacket, SSRC_POSITION, ssrc);
    }

    /**
     *
     * @see java.lang.Thread#run()
     */
    public void run() {
        synchronized (doneSync) {
            while (!done) {
                try {
                    putShort(rtpPacket, SEQUENCE_POSITION, sequenceNumber);
                    putInt(rtpPacket, TIMESTAMP_POSITION, timestamp);
                    bridge.sendPacket(rtpDatagram, location);
                    sequenceNumber = (sequenceNumber + 1) % MAX_SEQUENCE;
                    timestamp = (timestamp + SAMPLES_PER_PACKET)
                        % MAX_TIMESTAMP;
                    try {
                        doneSync.wait(MS_BETWEEN_PACKETS);
                    } catch (InterruptedException e) {
                        // Does Nothing
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    done = true;
                }
                Thread.yield();
            }
        }
    }

    /**
     * Closes the sender
     */
    public void close() {
        synchronized (doneSync) {
            done = true;
            doneSync.notifyAll();
        }
    }

}
