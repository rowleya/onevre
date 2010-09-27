/*
 * @(#)ClientReader.java
 * Created: 15 Oct 2007
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

package com.googlecode.onevre.bridges.tcpbridge.common;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Handles a Client
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ClientReader extends Thread {

    private static final int MIN_DELAY_TO_DROP = -500;

    private static final int MIN_DELAY_TO_SLEEP = 10;

    private static final int ADDRESS_SIZE = 4;

    // The default buffer length in milliseconds
    private static final int DEFAULT_BUFFER = 1000;

    private DataInputStream input = null;

    private HashMap<InetSocketAddress, InetSocketAddress> addressMap = null;

    private DatagramSocket socket = null;

    private boolean done = false;

    private LinkedList<DatagramPacket> packetQueue =
        new LinkedList<DatagramPacket>();

    private LinkedList<Long> timeQueue = new LinkedList<Long>();

    private long bufferLength = DEFAULT_BUFFER;

    private boolean buffering = false;

    private DatagramPacket currentPacket = null;

    private long currentPacketTime = 0;

    private long firstPacketSendTime = 0;

    private long firstPacketTime = 0;

    /**
     * Creates a new Client
     * @param input The input stream to read from
     * @param addressMap Map from incoming to outgoing addresses
     * @throws IOException
     */
    public ClientReader(InputStream input,
            HashMap<InetSocketAddress, InetSocketAddress> addressMap)
            throws IOException {
        this(input);
        this.socket = new DatagramSocket();
        this.addressMap = addressMap;
    }

    /**
     * Creates a new Client
     * @param input The input stream to read from
     */
    public ClientReader(InputStream input) {
        setInputStream(input);
    }

    /**
     * Creates a new ClientReader
     * @param addressMap Map from incoming to outgoing addresses
     * @throws IOException
     */
    public ClientReader(
            HashMap<InetSocketAddress, InetSocketAddress> addressMap)
            throws IOException {
        this.socket = new DatagramSocket();
        this.addressMap = addressMap;
    }

    /**
     * Sets the input stream to be used to read packets
     * @param input The new input stream
     */
    public void setInputStream(InputStream input) {
        this.input = new DataInputStream(input);
    }

    // Adds a packet to the queue
    private void addPacket(DatagramPacket packet, long time) {
        if (packetQueue.isEmpty()) {
            firstPacketTime = 0;
            firstPacketSendTime = 0;
            buffering = true;
            System.err.println("Buffering at " + time);
        } else if (buffering
                && ((time - timeQueue.getFirst()) >= bufferLength)) {
            buffering = false;
            System.err.println("Stopped Buffering at " + time);
        }
        synchronized (packetQueue) {
            packetQueue.addLast(packet);
            timeQueue.addLast(time);
            if (!buffering) {
                packetQueue.notifyAll();
            }
        }
    }

    private void sendPacket() {

        if (currentPacket != null) {
            try {
                InetSocketAddress sendAddress = addressMap.get(
                        currentPacket.getSocketAddress());
                if (sendAddress != null) {
                    currentPacket.setSocketAddress(sendAddress);
                    socket.send(currentPacket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private long computeDelay() {
        long timeOffset = 0;
        long delay = 0;

        if (firstPacketSendTime == 0) {
            return 0;
        }

        timeOffset = System.currentTimeMillis() - firstPacketSendTime;

        // Calculate the delay before sending the next packet
        delay = (currentPacketTime - firstPacketTime)
                - timeOffset;

        return delay;
    }

    /**
     *
     * @see java.lang.Thread#run()
     *
     */
    public void run() {
        done = false;

        if (addressMap != null) {
            Thread sender = new Thread() {
                public void run() {
                    while (!done) {
                        getNextPacket();
                        sendPacket();
                    }
                }
            };
            sender.start();
        }

        if (input != null) {
            while (!done) {
                try {
                    readPacket(input);
                } catch (SocketTimeoutException e) {
                    // Do Nothing
                } catch (IOException e) {
                    e.printStackTrace();
                    close();
                }
            }
        }
    }

    /**
     * Reads the next packet from the given stream
     * @param input The next packet to read
     * @throws IOException
     */
    public void readPacket(DataInputStream input) throws IOException {
        int length = input.readInt();
        long time = input.readLong();
        byte[] address = new byte[ADDRESS_SIZE];
        byte[] data = new byte[length];
        input.readFully(address);
        int port = input.readInt();
        input.readFully(data);
        InetSocketAddress listenAddress = new InetSocketAddress(
                InetAddress.getByAddress(address), port);
        DatagramPacket packet = new DatagramPacket(data,
                data.length, listenAddress);
        addPacket(packet, time);
    }

    /**
     * Gets the local port this socket is bound to
     * @return The port number
     */
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    /**
     * Closes the reader
     *
     */
    public void close() {
        if (!done) {
            done = true;
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the next packet in the queue
     * @return The next packet
     */
    public DatagramPacket getNextPacket() {
        boolean timerScheduled = false;
        while (!timerScheduled && !done) {
            synchronized (packetQueue) {
                currentPacket = null;
                while ((packetQueue.isEmpty() || buffering) && !done) {
                    try {
                        packetQueue.wait();
                    } catch (InterruptedException e) {
                        // Do Nothing
                    }
                }
                if (!packetQueue.isEmpty()) {
                    currentPacket = packetQueue.removeFirst();
                    currentPacketTime = timeQueue.removeFirst();
                }
            }

            if (currentPacket != null) {
                long delay = computeDelay();
                if (delay > MIN_DELAY_TO_SLEEP) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        // Do Nothing
                    }
                    timerScheduled = true;
                } else if (delay >= MIN_DELAY_TO_DROP) {
                    timerScheduled = true;
                }
            }
        }
        if (currentPacket != null) {
            if (firstPacketSendTime == 0) {
                firstPacketSendTime = System.currentTimeMillis();
                firstPacketTime = currentPacketTime;
            }
        }
        return currentPacket;
    }
}
