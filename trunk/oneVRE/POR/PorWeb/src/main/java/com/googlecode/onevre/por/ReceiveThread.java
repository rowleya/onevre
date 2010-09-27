/*
 * Copyright (c) 2008, University of Manchester All rights reserved.
 * See LICENCE in root directory of source code for details of the license.
 */

package com.googlecode.onevre.por;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;

import com.googlecode.onevre.ag.agbridge.BridgeClient;
import com.googlecode.onevre.ag.types.network.NetworkLocation;

/**
 * A Thread for receiving packets from a multi stream receiver
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ReceiveThread extends Thread {

    private static final float FULL_LOSS = 100;

    private static final int INT_LENGTH = 4;

    private static final int SHORT_LENGTH = 2;

    private static final int BYTE_MASK = 0xFF;

    private static final int BITS_PER_BYTE = 8;

    private static final int SSRC_POSITION = 8;

    private static final int SEQUENCE_POSITION = 2;

    private static final int MAX_SEQUENCE = 65535;

    private HashMap<Long, Integer> lostPackets =
        new HashMap<Long, Integer>();

    private HashMap<Long, Integer> receivedPackets =
        new HashMap<Long, Integer>();

    private HashMap<Long, Integer> lastSequence =
        new HashMap<Long, Integer>();

    private HashMap<Long, Float> lastLoss =
        new HashMap<Long, Float>();

    private BridgeClient client = null;

    private NetworkLocation location = null;

    private boolean done = false;

    private LinkedList<DatagramPacket> packetQueue =
        new LinkedList<DatagramPacket>();

    private static long getInt(byte[] array, int offset) {
        long value = 0;
        for (int i = 0; i < INT_LENGTH; i++) {
            value |= ((array[offset + i] & BYTE_MASK)
                           << (BITS_PER_BYTE * (INT_LENGTH - i - 1)))
                           & 0xFFFFFFFFL;
        }
        return value;
    }

    private static int getShort(byte[] array, int offset) {
        int value = 0;
        for (int i = 0; i < SHORT_LENGTH; i++) {
            value |= (array[offset + i] & BYTE_MASK)
                           << (BITS_PER_BYTE * (SHORT_LENGTH - i - 1));
        }
        return value;
    }

    /**
     * Creates a ReceiveThread
     * @param client The client to use
     * @param location The location to receive
     */
    public ReceiveThread(BridgeClient client, NetworkLocation location) {
        this.client = client;
        this.location = location;
    }

    private void addPacket(DatagramPacket packet) {
        synchronized (packetQueue) {
            packetQueue.addLast(packet);
            packetQueue.notifyAll();
        }
    }

    private DatagramPacket getPacket() {
        synchronized (packetQueue) {
            while (!done && packetQueue.isEmpty()) {
                try {
                    packetQueue.wait();
                } catch (InterruptedException e) {
                    // Does Nothing
                }
            }
            if (!packetQueue.isEmpty()) {
                return packetQueue.removeFirst();
            }
            return null;
        }
    }

    /**
     *
     * @see java.lang.Thread#run()
     */
    public void run() {
        Thread receiver = new Thread() {
            public void run() {
                while (!done) {
                    try {
                        DatagramPacket packet = null;
                        if (client.isSinglePacketStream()) {
                            packet = client.receivePacket();
                        } else {
                            packet = client.receivePacket(location);
                        }
                        addPacket(packet);
                        Thread.yield();
                    } catch (SocketTimeoutException e) {
                        // Do Nothing
                    } catch (IOException e) {
                        if (!done) {
                            e.printStackTrace();
                            done = true;
                        }
                    }
                }
            }
        };
        receiver.start();

        while (!done) {
            DatagramPacket packet = getPacket();
            if (packet != null) {
                byte[] data = packet.getData();
                int offset = packet.getOffset();
                long ssrc = getInt(data, offset + SSRC_POSITION);
                int sequence = getShort(data, offset + SEQUENCE_POSITION);

                synchronized (lastSequence) {
                    if (!lastSequence.containsKey(ssrc)) {
                        lostPackets.put(ssrc, 0);
                        receivedPackets.put(ssrc, 1);
                    } else {
                        receivedPackets.put(ssrc,
                                receivedPackets.get(ssrc) + 1);
                        int last = lastSequence.get(ssrc);
                        if (sequence != (last + 1)) {
                            int lost = lostPackets.get(ssrc);
                            if (sequence < last) {
                                lost += MAX_SEQUENCE - last;
                                last = 0;
                            }
                            lost += sequence - last;
                            lostPackets.put(ssrc, lost);
                        }
                    }
                    if (sequence == MAX_SEQUENCE) {
                        sequence = -1;
                    }
                    lastSequence.put(ssrc, sequence);
                }
                Thread.yield();
            }
        }
    }

    /**
     * Closes the thread
     */
    public void close() {
        done = true;
    }


    /**
     * Gets the current loss for a source
     * @param ssrc The source identifier
     * @return The current loss
     */
    public float getLoss(long ssrc) {
        synchronized (lastSequence) {
            if (!receivedPackets.containsKey(ssrc)
                    || (receivedPackets.get(ssrc) == 0)) {
                if (lastLoss.containsKey(ssrc)) {
                    return lastLoss.get(ssrc);
                }
                return FULL_LOSS;
            }
            float lost = lostPackets.get(ssrc);
            float loss = lost / (receivedPackets.get(ssrc) + lost);
            receivedPackets.put(ssrc, 0);
            lostPackets.put(ssrc, 0);
            lastLoss.put(ssrc, loss);
            return loss;
        }
    }

    /**
     * Removes an ssrc
     * @param ssrc The ssrc to remove
     */
    public void removeSsrc(long ssrc) {
        lastSequence.remove(ssrc);
        receivedPackets.remove(ssrc);
        lostPackets.remove(ssrc);
    }
}
