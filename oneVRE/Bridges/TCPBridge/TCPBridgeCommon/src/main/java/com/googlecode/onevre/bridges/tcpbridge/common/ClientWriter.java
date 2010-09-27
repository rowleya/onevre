/*
 * @(#)ClientWriter.java
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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.LinkedList;

/**
 * Handles a Client
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ClientWriter extends Thread {

    private static final int MAX_QUEUE_LENGTH = 10000;

    private InetAddress localAddress = null;

    private DataOutputStream output = null;

    private InetSocketAddress[] addresses = new InetSocketAddress[0];

    private MulticastSocket[] sockets = new MulticastSocket[0];

    private boolean done = false;

    private LinkedList<DatagramPacket> packetQueue =
        new LinkedList<DatagramPacket>();

    private LinkedList<Long> timeQueue = new LinkedList<Long>();

    private LinkedList<InetSocketAddress> addressQueue =
        new LinkedList<InetSocketAddress>();

    private InetSocketAddress currentAddress = null;

    private long currentTime = 0;

    private int localIgnorePort = 0;

    /**
     * Creates a new Client
     * @param output The output stream to write to
     * @param addresses A list of addresses to listen to
     * @throws IOException
     */
    public ClientWriter(OutputStream output, InetSocketAddress[] addresses)
            throws IOException {
        this(output);
        this.addresses = addresses;
        this.sockets = new MulticastSocket[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            sockets[i] = new MulticastSocket(addresses[i].getPort());
        }
    }

    /**
     * Creates a new Client
     * @param output The output stream to write to
     * @throws IOException
     */
    public ClientWriter(OutputStream output) throws IOException {
        this.output = new DataOutputStream(output);
        localAddress = InetAddress.getLocalHost();
    }

    /**
     * Create a new ClientWriter
     * @param addresses The addresses to listen to
     * @throws IOException
     */
    public ClientWriter(InetSocketAddress[] addresses)
            throws IOException {
        this.addresses = addresses;
        this.sockets = new MulticastSocket[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            sockets[i] = new MulticastSocket(addresses[i].getPort());
        }
        localAddress = InetAddress.getLocalHost();
    }

    // Gets the next packet in the queue
    private DatagramPacket getNextPacket() {
        synchronized (packetQueue) {
            while (!done && packetQueue.isEmpty()) {
                try {
                    packetQueue.wait();
                } catch (InterruptedException e) {
                    // Does Nothing
                }
            }
            if (!packetQueue.isEmpty()) {
                currentTime = timeQueue.removeFirst();
                currentAddress = addressQueue.removeFirst();
                return packetQueue.removeFirst();
            }
            return null;
        }
    }

    /**
     * Adds a packet to send
     * @param packet The packet to add
     * @param address The address from which the packet was received
     */
    public void addPacket(DatagramPacket packet, InetSocketAddress address) {
        synchronized (packetQueue) {
            if (packetQueue.size() > MAX_QUEUE_LENGTH) {
                packetQueue.removeFirst();
                timeQueue.removeFirst();
                addressQueue.removeFirst();
            }
            packetQueue.addLast(packet);
            timeQueue.addLast(System.currentTimeMillis());
            addressQueue.addLast(address);
            packetQueue.notifyAll();
        }
    }

    /**
     *
     * @see java.lang.Thread#run()
     */
    public void run() {
        done = false;
        startListening();

        while (!done) {
            writeNextPacket(output);
        }

        stopListening();
    }

    /**
     * Starts listening and capturing packets
     *
     */
    public void startListening() {
        done = false;
        for (int i = 0; i < sockets.length; i++) {
            System.err.println("Listening on " + addresses[i]);
            if (addresses[i].getAddress().isMulticastAddress()) {
                try {
                    sockets[i].joinGroup(addresses[i].getAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            final int socket = i;
            Thread listener = new Thread() {
                public void run() {
                    while (!done) {
                        try {
                            byte[] data = new byte[
                                   sockets[socket].getReceiveBufferSize()];
                            DatagramPacket packet = new DatagramPacket(data,
                                    data.length);
                            sockets[socket].receive(packet);
                            if ((packet.getPort() != localIgnorePort)
                                    || !packet.getAddress().equals(
                                            localAddress)) {
                                addPacket(packet, addresses[socket]);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            listener.start();
        }
    }

    /**
     * Stops listening an capturing packets
     *
     */
    public void stopListening() {
        done = true;
        for (int i = 0; i < sockets.length; i++) {
            if (addresses[i].getAddress().isMulticastAddress()) {
                try {
                    sockets[i].leaveGroup(addresses[i].getAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Writes the next packet from this writer to an output stream
     * @param output The output stream to write to
     */
    public void writeNextPacket(DataOutputStream output) {
        DatagramPacket packet = getNextPacket();
        if (packet != null) {
            byte[] data = packet.getData();
            int offset = packet.getOffset();
            int length = packet.getLength();
            try {
                output.writeInt(length);
                output.writeLong(currentTime);
                output.write(currentAddress.getAddress().getAddress());
                output.writeInt(currentAddress.getPort());
                output.write(data, offset, length);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }
    }

    /**
     * Closes the writer
     *
     */
    public void close() {
        synchronized (packetQueue) {
            done = true;
            packetQueue.notifyAll();
        }
        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the port to ignore on the local host
     * @param localIgnorePort The port to ignore on the local host
     */
    public void setLocalIgnorePort(int localIgnorePort) {
        this.localIgnorePort = localIgnorePort;
    }

}
