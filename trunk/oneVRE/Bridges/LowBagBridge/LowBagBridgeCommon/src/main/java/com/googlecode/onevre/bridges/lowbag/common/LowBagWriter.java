/*
 * @(#)Writer.java
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.media.Format;
import javax.media.format.VideoFormat;


import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.protocols.rtp.RtpWriter;
import com.googlecode.vicovre.media.rtp.RTCPHeader;
import com.googlecode.vicovre.media.rtp.RTPHeader;
import com.googlecode.vicovre.repositories.rtptype.RTPType;



/**
 * Listens for incomming packets from the client or the AG and passes
 * them on if reqired in a different format or rather qualtiy.
 * @author Sebastian Starke
 * @version 1.0
 */
public class LowBagWriter  extends Thread implements RtpWriter {

    /** Maximal queue length before dropping packets. */
    private static final int MAX_QUEUE_LENGTH = 10000;

    /** Sum of the size in byte over IP, Port and Timestamp. */
    private static final int MYHEADER_SIZE = 4+2+8;

    /** Writer output socket. */
    private DatagramSocket output = null;

    /** Address to listen on. */
    private Vector<InetSocketAddress> addresses =
        new Vector<InetSocketAddress>();

    /** Sockets to receive data from the addesses listening on. */
    private Vector<MulticastSocket> sockets =
        new Vector<MulticastSocket>();

    /** Thread control variable. */
    private boolean done = false;

    /** The packet queue. */
    private LinkedList<DatagramPacket> packetQueue =
        new LinkedList<DatagramPacket>();

    /** The time queue. */
    private LinkedList<Long> timeQueue = new LinkedList<Long>();

    /** The address queue. */
    private LinkedList<InetSocketAddress> addressQueue =
        new LinkedList<InetSocketAddress>();

    /** Address of the current packet processing. */
    private InetSocketAddress currentAddress = null;

    /** Timestamp of the current packet processing. */
    private long currentTime = 0;

    /** Local port, which should be ignored to receive not the own packets. */
    private int localIgnorePort = 0;

    /** The local address. */
    private InetAddress localAddress = null;

    /** The brdige output format rather the format transmitted to the client. */
    private Format outputFormat = new VideoFormat(
            LowBagDefaults.DEFAULT_OUTPUT_FORMAT);

    /** Object of the streams handler. */
    private StreamsHandler streamsHandler = null;

    /** Defines writer input socket as client socket. */
    private boolean isClientSocket = false;

    /**
     * Creates a new Client.
     * @param address The destination address to write to
     * @param port The destination port to write to
     * @throws IOException MulticastSocket(), getLocalHost()
     */
    public LowBagWriter(final InetAddress address, final int port)
            throws IOException {
        if (address == null) {
            output = new DatagramSocket(port);
        } else {
            output = new DatagramSocket();
            output.connect(address, port);
        }
        localAddress = InetAddress.getLocalHost();
    }

    /**
     * Sets the address and socket array to listen on.
     * @param locations AG stream loctions (RTP+RTCP sockets)
     * @throws IOException MulticastSocket()
     */
    public final void setListenLocations(
                final Vector<NetworkLocation> locations)
            throws IOException {
        for (int i = 0; i < locations.size(); i++) {
            addresses.add(new InetSocketAddress(
                    locations.get(i).getHost(),
                    locations.get(i).getPort()));
            sockets.add(new MulticastSocket(locations.get(i).getPort()));
        }
    }

    /**
     * Returns the status of the writer.
     * @return Status of the writer
     */
    public final boolean getStatus() {
        return done;
    }

    /**
     * Returns the status of the packet queue.
     * @return Status of the packet queue.
     */
    public final boolean isPacketQueueEmpty() {
        synchronized (packetQueue) {
            return packetQueue.isEmpty();
        }
    }

    /**
     * Gets the next packet in the queue.
     * @return The next packet in the queue.
     */
    private DatagramPacket getNextPacket() {
        synchronized (packetQueue) {
            while (!done && packetQueue.isEmpty()) {
                try {
                    packetQueue.wait();
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
            if (!packetQueue.isEmpty()) {
                currentTime = timeQueue.removeFirst();
                currentAddress = addressQueue.removeFirst();
                DatagramPacket packet = packetQueue.removeFirst();
                return packet;
            }
            return null;
        }
    }

    /**
     * Adds a packet to send.
     * @param packet The packet to add
     * @param address The address from which the packet was received
     */
    public final void addPacket(final DatagramPacket packet,
            final InetSocketAddress address) {
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
     * @see java.lang.Thread#run()
     */
    public final void run() {
        done = false;
        while (!done) {
            try {
                writeNextPacket(output);
            } catch (IOException e) {
                if (!done) {
                    e.printStackTrace();
                }
                close();
            }
        }
    }

    /**
     * Sets the writer socket to a client socket.
     * @param socket2 Is client socket or not
     */
    public final void setClientSocket(final boolean socket2) {
        isClientSocket = socket2;
    }

    /**
     * Starts the listening for packets on all required sockets.
     */
    public final void startListening() {
        done = false;
        for (int i = 0; i < sockets.size(); i++) {
            if (addresses.get(i).getAddress().isMulticastAddress()) {
                try {
                    sockets.get(i).joinGroup(addresses.get(i).getAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isClientSocket || (addresses.get(i).getPort() % 2) == 1) {
                startReceivingingPackets(i);
            } else {
                startProccessingPackets(i);
            }
        }
    }

    /**
     * Receives and adds packets to the queue.
     * @param i Incomming socket
     */
    public final void startReceivingingPackets(final int i) {
        Thread listener = new Thread() {
            public void run() {
                while (!done) {
                    try {
                        byte[] data = new byte[
                               sockets.get(i).getReceiveBufferSize()];
                        DatagramPacket packet = new DatagramPacket(data,
                                data.length);
                        sockets.get(i).receive(packet);
                        if ((packet.getPort() != localIgnorePort)
                                || !packet.getAddress().equals(localAddress)) {
                            addPacket(packet, addresses.get(i));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        listener.start();
    }

    /**
     * Receives, processes and adds packets to the queue.
     * @param i Incomming socket
     */
    public final void startProccessingPackets(final int i) {
        final LowBagWriter me = this;
        Thread listener = new Thread() {
            public void run() {
                while (!done) {
                    try {
                        byte[] data = new byte[
                               sockets.get(i).getReceiveBufferSize()];
                        DatagramPacket packet = new DatagramPacket(data,
                                data.length);
                        sockets.get(i).receive(packet);
                        if (packet.getData() != null
                                && (packet.getPort() != localIgnorePort)
                                || !packet.getAddress().equals(localAddress)) {
                            RTPHeader header = new RTPHeader(packet.getData(),
                                    packet.getOffset(), packet.getLength());
                            StreamHandler streamHandler =
                                streamsHandler.getStream(Long.toString(
                                        header.getSsrc()));
                            if (streamHandler == null) {
                                streamsHandler.addStream(
                                    me, addresses.get(i),
                                    header.getSsrc(), outputFormat);
                            } else {
                                streamHandler.process(packet);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        listener.start();
    }

    /**
     * Stops listening an capturing packets.
     */
    public final void stopListening() {
        done = true;
        for (int i = 0; i < sockets.size(); i++) {
            if (addresses.get(i).getAddress().isMulticastAddress()) {
                try {
                    sockets.get(i).leaveGroup(addresses.get(i).getAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        addresses = null;
        sockets = null;
    }

    /**
     * Writes the next packet from this writer to an output stream.
     * @param output2 The output stream to write to
     * @throws IOException Error with packet data!
     * @throws IOException write()
     * @throws IOException writeShort()
     * @throws IOException writeLong()
     * @throws IOException flush()
     * @throws IOException RTCPHeader()
     * @throws IOException RTPHeader()
     * @throws IOException updateRenderer()
     * @throws IOException send()
     * @throws IOException Error, no streams handler!
     */
    public final void writeNextPacket(final DatagramSocket output2)
            throws IOException {
        RTCPHeader rtcpHeader = null;
        RTPHeader rtpHeader = null;
        StreamHandler streamHandler = null;
        DatagramPacket packet = getNextPacket();
        long time = System.currentTimeMillis();
        if (packet == null) {
            throw new IOException("Error with packet data!");
        }

        //- Read old packet
        byte[] data = packet.getData();
        int offset = packet.getOffset();
        int length = packet.getLength();

        //- Add source address and port to data
        byte[] buf = new byte[length + MYHEADER_SIZE];
        ByteArrayOutputStream src = new ByteArrayOutputStream();
        DataOutputStream srcOut = new DataOutputStream(src);
        try {
            srcOut.write(currentAddress.getAddress().getAddress());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            srcOut.writeShort(currentAddress.getPort());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            srcOut.writeLong(currentTime);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        srcOut.flush();
        System.arraycopy(src.toByteArray(), 0, buf, 0, MYHEADER_SIZE);
        System.arraycopy(data, offset, buf, MYHEADER_SIZE, length);

        //- Create new packet with new data
        DatagramPacket dgram = new DatagramPacket(buf, buf.length,
                output2.getInetAddress(), output2.getPort());

        //- send packets
        if (isClientSocket) {
            output2.send(dgram);
            return;
        }
        try {
            rtcpHeader = new RTCPHeader(packet.getData(),
                    packet.getOffset(), packet.getLength());
            if (streamsHandler == null) {
                throw new IOException("Error, no streams handler!");
            }
            streamHandler = streamsHandler.getStream(
                    Long.toString(rtcpHeader.getSsrc()));
        } catch (IOException e) {
            // Do Nothing
        }
        try {
            rtpHeader = new RTPHeader(packet.getData(),
                    packet.getOffset(), packet.getLength());
            if (streamsHandler == null) {
                throw new IOException("Error, no streams handler!");
            }
            streamHandler = streamsHandler.getStream(
                    Long.toString(rtpHeader.getSsrc()));
            if (streamHandler == null) {
                streamHandler = streamsHandler.addStream(
                        Long.toString(rtpHeader.getSsrc()));
            }
            streamHandler.updateRenderer(packet);
        } catch (IOException e) {
            // Do Nothing
        }

        //- Send packet
        if (streamHandler != null && streamHandler.isReceiving()) {
            try {
                output2.send(dgram);
                streamHandler.setTimeLastSend(time);
            } catch (IOException e) {
                // Do Nothing
            }

        //- Ping packet (only rtcp)
        } else if (streamHandler != null
                && (streamHandler.getTimeLastSend()
                + (LowBagDefaults.RTP_UPDATE_TIME / 3)) < time) {
            try {
                output2.send(dgram);
                streamHandler.setTimeLastSend(time);
            } catch (IOException e) {
                // Do Nothing
            }

        //- Drop packet
        } else {
            // DO Nothing
        }
    }

    /**
     * Closes the writer.
     */
    public final void close() {
        synchronized (packetQueue) {
            done = true;
            packetQueue.notifyAll();
        }
        output.close();
    }

    /**
     * Sets the port to ignore on the local host.
     * @param localIgnorePort2 The port to ignore on the local host
     */
    public final void setLocalIgnorePort(final int localIgnorePort2) {
        localIgnorePort = localIgnorePort2;
    }

    /**
     * Sets the corresponding stream handler.
     * @param streamsHandler2 The stream handler
     */
    public final void setStreamHandler(final StreamsHandler streamsHandler2) {
        streamsHandler = streamsHandler2;
    }

    /**
     * Gets the corresponding stream handler.
     * @return The corresponding stream handler
     */
    public final StreamsHandler getStreamsHandler() {
        return streamsHandler;
    }

    /**
     * Sets the required output format.
     * @param outputFormat2 New output format
     * @throws IOException Unsupported format!
     */
    public final void setOutputFormat(final Format outputFormat2)
            throws IOException {
        if (!checkFormatSupport(outputFormat2)) {
            throw new IOException("Unsupported format!");
        }
        outputFormat = outputFormat2;
    }

    /**
     * Checks if the format is supported.
     * @param outputFormat2 The format to check
     * @return The bridge output format.
     */
    public final boolean checkFormatSupport(final Format outputFormat2) {
        List<RTPType> rtpTypes =
            LowBagDefaults.getSupportedFormats().findRtpTypes();
        for (RTPType rtpType : rtpTypes) {
            if (rtpType.getFormat().matches(outputFormat2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the bridge output format.
     * @return The bridge output format.
     */
    public final Format getOutputFormat() {
        return outputFormat;
    }
}