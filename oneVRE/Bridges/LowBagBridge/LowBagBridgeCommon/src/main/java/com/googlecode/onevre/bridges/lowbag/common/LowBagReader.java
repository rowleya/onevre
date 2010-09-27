/*
 * @(#)Reader.java
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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;



import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.vicovre.media.rtp.RTCPHeader;
import com.googlecode.vicovre.media.rtp.RTCPReceiverReport;
import com.googlecode.vicovre.media.rtp.RTCPReport;
import com.googlecode.vicovre.media.rtp.RTCPSenderReport;
import com.googlecode.vicovre.media.rtp.RTPHeader;
import com.sun.media.rtp.RTCPPacket;


/**
 * Reads incomming packets from the bridge and passes them on if required.
 * @author Sebastian Starke
 * @version 1.0
 */
public class LowBagReader extends Thread {

    /** Minimal delay to drop packets. */
    private static final int MIN_DELAY_TO_DROP = -500;

    /** Minimal delay to sleep. */
    private static final int MIN_DELAY_TO_SLEEP = 10;

    /** Size of an IP-Address in byte. */
    private static final int ADDRESS_SIZE = 4;

    /** Size of a standard port in byte. */
    private static final int PORT_SIZE = 2;

    /** Size of a timestamp in byte. */
    private static final int TIME_SIZE = 8;

    /** Buffer length in milliseconds. */
    private static final int BUFFER_LENGTH = 100;

    /** The reader input socket. */
    private DatagramSocket input = null;

    /** The reader output socket. */
    private MulticastSocket output = null;

    /** Thread control variable. */
    private boolean done = false;

    /** Queue of packets read. */
    private LinkedList<DatagramPacket> packetQueue =
        new LinkedList<DatagramPacket>();

    /** Queue of timestamps with packet read times. */
    private LinkedList<Long> timeQueue = new LinkedList<Long>();

    /** Control variable for buffering. */
    private boolean buffering = false;

    /** Object of the current packet. */
    private DatagramPacket currentPacket = null;

    /** Timestamp of the current packet. */
    private long currentPacketTime = 0;

    /** Timestamp of the first packet sent. */
    private long firstPacketSendTime = 0;

    /** Timestamp of the first packet received. */
    private long firstPacketTime = 0;

    /** Default TTL. */
    private static final int DEFAULT_TTL = 127;

    /** Object ot the streams handler. */
    private StreamsHandler streamsHandler = null;

    /** Defines reader input socket as server socket.
     *  (will not be closed on disconnect) */
    private boolean isServerSocket = false;

    /** Hash map of source and destinaion addresses. */
    private HashMap<InetSocketAddress, InetSocketAddress> forwardMap =
        new HashMap<InetSocketAddress, InetSocketAddress>();

    /** Hash map of addresses with the corresponding TTL. */
    private HashMap<InetSocketAddress, Integer> ttlMap =
        new HashMap<InetSocketAddress, Integer>();

    /** Default size for packet buffer. */
    public static final int DEFAULT_BUFFER_SIZE = 8196;

    /**
     * Creates a new reader.
     * @param input2 Input socket to read from
     * @throws IOException MulticastSocket()
     */
    public LowBagReader(final DatagramSocket input2)
            throws IOException {
       input = input2;
       output = new MulticastSocket();
    }

    /**
     * Creates a new reader.
     * @param port Input port to read from
     * @throws IOException MulticastSocket()
     */
    public LowBagReader(final int port)
            throws IOException {
        this(new DatagramSocket(port));
    }

    /**
     * Set the streams handler.
     * @param streamsHandler2 Object of the streams handler
     */
    public final void setStreamsHandler(final StreamsHandler streamsHandler2) {
        streamsHandler = streamsHandler2;
    }

    /**
     * Sets the reader socket to a server socket
     * (will not be closed on disconnect).
     * @param socket2 True/False server output
     */
    public final void setServerSocket(final boolean socket2) {
        isServerSocket = socket2;
    }

    /**
     * Sets map for forwarding from locations to clients or opposite.
     * @param locations Vector of audio or video locations
     * @param rtp Socket of audio or video client
     * @throws IOException Error with handed over socket!
     */
    public final void setForwardMap(final Vector<NetworkLocation> locations,
                final InetSocketAddress rtp)
            throws IOException {
        if (rtp == null) {
            throw new IOException("Error with handed over socket!");
        }
        InetSocketAddress rtcp = new InetSocketAddress(
                rtp.getAddress(), rtp.getPort() + 1);
        InetSocketAddress addr = null;
        for (int i = 0; i < locations.size(); i++) {
            addr = new InetSocketAddress(locations.get(i).getHost(),
                    locations.get(i).getPort());
            if ((addr.getPort() % 2) == 1) {
                if (isServerSocket) {
                    forwardMap.put(rtcp, addr);
                } else {
                    forwardMap.put(addr, rtcp);
                }
            } else {
                if (isServerSocket) {
                    forwardMap.put(rtp, addr);
                } else {
                    forwardMap.put(addr, rtp);
                }
            }
            if (locations.get(i) instanceof MulticastNetworkLocation) {
                ttlMap.put(addr, ((MulticastNetworkLocation)
                        locations.get(i)).getTtl());
            }
        }
    }

    /**
     * Returns the status of the reader rather
     * the thread control variable "done".
     * @return Status of the reader
     */
    public final boolean getStatus() {
        return done;
    }

    /**
     * Adds a packet to the queue.
     * @param packet Packet to add
     * @param time Timestamp packet created
     */
    private void addPacket(final DatagramPacket packet, final long time) {
        if (packetQueue.isEmpty()) {
            firstPacketTime = 0;
            firstPacketSendTime = 0;
            buffering = true;
        } else if (buffering
                && ((time - timeQueue.getFirst()) >= BUFFER_LENGTH)) {
            buffering = false;
        }
        synchronized (packetQueue) {
            packetQueue.addLast(packet);
            timeQueue.addLast(time);
            if (!buffering) {
                packetQueue.notifyAll();
            }
        }
    }

    /**
     * Sends the current packet to the output socket.
     */
    private void forwardPacket() {
        if (currentPacket != null) {
            try {
                InetSocketAddress dest = forwardMap.get(
                        currentPacket.getSocketAddress());
                if (dest != null) {
                    if (ttlMap.containsKey(
                            currentPacket.getSocketAddress())) {
                        output.setTimeToLive(ttlMap.get(
                                currentPacket.getSocketAddress()));
                    } else {
                        output.setTimeToLive(DEFAULT_TTL);
                    }
                    currentPacket.setSocketAddress(dest);
                    output.send(currentPacket);
                }
            } catch (IOException e) {
                // Do Nothing
            }
        }
    }

    /**
     * Calculate the delay before sending the next packet.
     * @return Delay to the time packet was created
     */
    private long computeDelay() {
        long timeOffset = 0;
        long delay = 0;
        if (firstPacketSendTime == 0) {
            return 0;
        }
        timeOffset = System.currentTimeMillis() - firstPacketSendTime;
        delay = (currentPacketTime - firstPacketTime) - timeOffset;
        return delay;
    }

    /**
     * Forwards the packets to the mutlicast group
     * or the video consumer.
     */
    public final void startForwarding() {
        done = false;
        if (forwardMap != null) {
           Thread sender = new Thread() {
               public void run() {
                   while (!done) {
                       getNextPacket();
                       forwardPacket();
                   }
               }
           };
           sender.start();
       }
    }

    /**
     * @see java.lang.Thread#run()
     */
    public final void run() {
        done = false;
        if (input != null) {
            while (!done) {
                try {
                    readPacket(input);
                } catch (SocketTimeoutException e) {
                    // Do Nothing
                } catch (IOException e) {
                    if (!done) {
                        e.printStackTrace();
                    }
                    close();
                }
            }
        }
    }

    /**
     * Set the timeout of the packges.
     * @param timeout The timeout to be set
     * @throws IOException IOException()
     */
    public final void setReceiveTimeout(final int timeout) throws IOException {
        input.setSoTimeout(timeout);
    }

    /**
     * Reads the next packet from the given stream.
     * @param input2 The next packet to read
     * @throws IOException receive()
     * @throws IOException read()
     * @throws IOException readShort()
     * @throws IOException getByAddress()
     * @throws IOException readLong()
     */
    public final void readPacket(final DatagramSocket input2)
            throws IOException {
        byte[] packetBuffer = new byte[DEFAULT_BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(
                packetBuffer, packetBuffer.length);

        //- receive packet
        input2.receive(packet);

        //- read packet
        int length = packet.getLength();
        byte[] data = packet.getData();
        int offset = packet.getOffset();

        //- read destination address and port from data in packet
        ByteArrayInputStream a = new ByteArrayInputStream(data,
                offset, ADDRESS_SIZE);
        byte[] b = new byte[ADDRESS_SIZE];
        a.read(b);
        InetAddress address = InetAddress.getByAddress(b);

        DataInputStream p = new DataInputStream(new ByteArrayInputStream(
                data, offset + ADDRESS_SIZE, PORT_SIZE));
        int port = (0x0000FFFF & p.readShort());

        DataInputStream t = new DataInputStream(new ByteArrayInputStream(
                data, offset + ADDRESS_SIZE + PORT_SIZE, TIME_SIZE));
        long time = t.readLong();

        //- create new packet with new data
        byte[] buf = new byte[length - (ADDRESS_SIZE + PORT_SIZE + TIME_SIZE)];
        System.arraycopy(data, offset + (ADDRESS_SIZE + PORT_SIZE + TIME_SIZE),
                buf, 0, buf.length);
        DatagramPacket dgram = new DatagramPacket(buf, buf.length,
                address, port);

        //- rtcp packet
        if ((port % 2) == 1
                && forwardMap.containsKey(dgram.getSocketAddress())) {
            try {
                RTCPHeader header = new RTCPHeader(dgram.getData(),
                            dgram.getOffset(), dgram.getLength());
                RTCPReport report = null;
                if (header.getPacketType() == RTCPPacket.RR) {
                    report = new RTCPReceiverReport(dgram.getData(),
                            dgram.getOffset(), dgram.getLength());
                } else if (header.getPacketType() == RTCPPacket.SR) {
                    report = new RTCPSenderReport(dgram.getData(),
                            dgram.getOffset(), dgram.getLength());
                }
                if (streamsHandler != null && report != null
                        && header.getSsrc() != 0) {
                    streamsHandler.updateStream(
                            Long.toString(header.getSsrc()), null, report);
                }
            } catch (IOException e) {
                // Do Nothing
            }

        //- rtp packet
        } else {
            RTPHeader header = new RTPHeader(dgram.getData(),
                    dgram.getOffset(), dgram.getLength());
            if (streamsHandler != null && header.getSsrc() != 0) {
                streamsHandler.updateStream(
                        Long.toString(header.getSsrc()), dgram, null);
            }
        }

        //- add packet to PAG queue
        addPacket(dgram, time);
    }

    /**
     * Gets the local port where this socket is bound to.
     * @return The port number this socket is bound to.
     */
    public final int getLocalPort() {
        return output.getLocalPort();
    }

    /**
     * Closes the reader.
     */
    public final void close() {
        synchronized (packetQueue) {
            done = true;
            packetQueue.notifyAll();
        }
        if (!isServerSocket) {
            input.close();
        }
    }

    /**
     * Gets the next packet in the queue.
     * @return The next packet.
     */
    public final DatagramPacket getNextPacket() {
        boolean timerScheduled = false;
        while (!timerScheduled && !done) {
            synchronized (packetQueue) {
                currentPacket = null;
                while ((packetQueue.isEmpty() || buffering) && !done) {
                    try {
                        packetQueue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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