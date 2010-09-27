/*
 * @(#)Sender.java
 * Created: 26 Oct 2007
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

package com.googlecode.onevre.ag.clientbridge;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


import com.googlecode.onevre.ag.agbridge.BridgeClient;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.protocols.rtcp.RtcpCheckProcessor;
import com.googlecode.onevre.protocols.rtcp.UdpPacketProcessor;
import com.googlecode.onevre.protocols.rtp.RtpCheckProcessor;

import memetic.crypto.RTPCrypt;


/**
 * Receives packets from a local datagram socket and sends them to a bridge
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Sender {

    private BridgeClient bridge = null;

    private HashMap<NetworkLocation, SendThread> senders =
        new HashMap<NetworkLocation, SendThread>();

    private HashMap<NetworkLocation, RTPCrypt> encrypters =
        new HashMap<NetworkLocation, RTPCrypt>();

    private HashMap<NetworkLocation, UdpPacketProcessor> processors =
        new HashMap<NetworkLocation, UdpPacketProcessor>();

    private int ignoreLocalPort = 0;

    private boolean pause = false;

    private Integer pauseSync = new Integer(0);

    private Vector<InetAddress> localAddresses = new Vector<InetAddress>();

    /**
     * Creates a new Sender
     */
    public Sender() {
        try {
            Enumeration<NetworkInterface> interfaces =
                NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface i = interfaces.nextElement();
                Enumeration<InetAddress> addresses = i.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    localAddresses.add(addresses.nextElement());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the bridge
     * @param bridge The new bridge to use
     */
    public void setBridge(BridgeClient bridge) {
        synchronized (this) {
            this.bridge = bridge;
        }
    }

    /**
     * Sets the encrypters to use
     * @param encryption the encryption to use
     */
    public void setEncryption(RTPCrypt encryption) {
        Iterator<NetworkLocation> iter = senders.keySet().iterator();
        while (iter.hasNext()) {
            NetworkLocation location = iter.next();
            encrypters.put(location, encryption);
        }
    }

    /**
     * Sets the local port to ignore traffic from
     * @param localAddress The address from which ignored packets will be sent
     * @param ignoreLocalPort The local port to ignore traffic from
     */
    public void setIgnoreLocalPort(InetAddress localAddress,
            int ignoreLocalPort) {
        this.ignoreLocalPort = ignoreLocalPort;
    }

    private void encrypt(NetworkLocation location, DatagramPacket packet,
            boolean isRtp) {
        RTPCrypt encrypter = encrypters.get(location);
        if (encrypter != null) {
            byte[] out = new byte[encrypter.getEncryptOutputSize(
                    packet.getLength())];
            int length = 0;
            try {
                if (isRtp) {
                    length = encrypter.encryptData(packet.getData(),
                            packet.getOffset(), packet.getLength(), out, 0);
                } else {
                    length = encrypter.encryptCtrl(packet.getData(),
                            packet.getOffset(), packet.getLength(), out, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (length > 0) {
                packet.setData(out, 0, length);
            } else {
                packet.setLength(0);
            }
        }
    }

    private class SendThread extends Thread {

        private InetSocketAddress address = null;

        private NetworkLocation location = null;

        private MulticastSocket socket = null;

        private boolean done = false;

        private SendThread(InetSocketAddress address,
                NetworkLocation location) throws IOException {
            this.address = address;
            this.location = location;
            socket = new MulticastSocket(address.getPort());
        }

        /**
         *
         * @see java.lang.Thread#run()
         */
        public void run() {
            try {
                socket.joinGroup(address.getAddress());
                socket.setTimeToLive(0);
                socket.setLoopbackMode(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!done) {
                synchronized (pauseSync) {
                    while (pause && !done) {
                        try {
                            pauseSync.wait();
                        } catch (InterruptedException e) {
                            // Does Nothing
                        }
                    }
                }
                try {
                    byte[] data = new byte[socket.getReceiveBufferSize()];
                    DatagramPacket packet = new DatagramPacket(data,
                            data.length);
                    socket.receive(packet);
                    if (pause || done || (packet.getPort() == ignoreLocalPort)
                            && (localAddresses.contains(packet.getAddress())
                            || packet.getAddress().isLoopbackAddress())) {
                        // Do Nothing
                    } else {
                        UdpPacketProcessor processor = processors.get(location);
                        if ((processor != null) && processor.process(packet)) {
                            encrypt(location, packet,
                                    (location.getPort() % 2) == 0);
                            synchronized (this) {
                                if (!done && !pause) {
                                    try {
                                        bridge.sendPacket(packet, location);
                                    } catch (Throwable t) {
                                        System.err.println("Error sending packet to " + location);
                                        t.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    if (!done && !pause) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void setLocation(NetworkLocation location) {
            this.location = location;
        }

        private void finish() {
            done = true;
        }

        private void close() {
            try {
                socket.leaveGroup(address.getAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket.close();
        }
    }

    /**
     * Pauses the sender
     */
    public void pause() {
        synchronized (pauseSync) {
            pause = true;
            pauseSync.notifyAll();
        }
    }

    /**
     * Resumes the sender
     */
    public void resume() {
        synchronized (pauseSync) {
            pause = false;
            pauseSync.notifyAll();
        }
    }

    /**
     * Closes the sender
     *
     */
    public void close() {
        Vector<NetworkLocation> locations = new Vector<NetworkLocation>(
                senders.keySet());
        for (int i = 0; i < locations.size(); i++) {
            doRemoveLocation(locations.get(i));
        }
    }

    /**
     * Sends a packet as if received locally
     * @param location The location to send the packet to
     * @param packet The packet to send
     * @throws IOException
     */
    private void sendPacket(NetworkLocation location,
            DatagramPacket packet) throws IOException {
        encrypt(location, packet, (location.getPort() % 2) == 0);
        synchronized (this) {
            bridge.sendPacket(packet, location);
        }
    }

    private void sendByes(RtpCheckProcessor proc, NetworkLocation location) {
        Long[] ssrcs = proc.getSsrcs();
        for (int i = 0; i < ssrcs.length; i++) {
            byte[] bye = Convert.getBye(ssrcs[i]);
            DatagramPacket packet = new DatagramPacket(bye, bye.length);
            try {
                sendPacket(Convert.getRtcpLocation(location), packet);
                System.err.println("Bye sent for local participant " +  ssrcs[i] + " to " + Convert.getRtcpLocation(location));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        proc.clearSources();
    }

    /**
     * Changes the location of a sender to send to a new location
     * @param oldLocation The old location being sent to
     * @param newLocation The new location to send to
     * @param encryption The encryption to use
     */
    public void changeLocation(NetworkLocation oldLocation,
            NetworkLocation newLocation, RTPCrypt encryption) {
        doChangeLocation(oldLocation, newLocation, encryption,
                new RtpCheckProcessor(ClientBridge.RTP_VERSION));
        doChangeLocation(Convert.getRtcpLocation(oldLocation),
                Convert.getRtcpLocation(newLocation), encryption,
                new RtcpCheckProcessor(ClientBridge.RTP_VERSION));
    }

    private void doChangeLocation(NetworkLocation oldLocation,
            NetworkLocation newLocation, RTPCrypt encryption,
            UdpPacketProcessor newProcessor) {
        SendThread sender = senders.get(oldLocation);
        if (sender != null) {
            System.err.println("Changing " + oldLocation + " for " + newLocation);
            UdpPacketProcessor processor = processors.get(oldLocation);
            if (processor instanceof RtpCheckProcessor) {
                RtpCheckProcessor proc = (RtpCheckProcessor) processor;
                sendByes(proc, oldLocation);
            }
            sender.setLocation(newLocation);
            senders.remove(oldLocation);
            senders.put(newLocation, sender);
            encrypters.remove(oldLocation);
            encrypters.put(newLocation, encryption);
            processors.remove(oldLocation);
            processors.put(newLocation, newProcessor);
        } else {
            System.err.println("Error changing location " + oldLocation + " to " + newLocation + ": sender not found");
        }
    }

    /**
     * Adds a new location to send
     * @param newLocation The new location to send to
     * @param localAddress The local address to send from
     * @param encryption The encryption to use
     * @throws IOException
     */
    public void addLocation(NetworkLocation newLocation,
            InetSocketAddress localAddress, RTPCrypt encryption)
            throws IOException {
        doAddLocation(newLocation, localAddress, encryption,
                new RtpCheckProcessor(ClientBridge.RTP_VERSION));
        doAddLocation(Convert.getRtcpLocation(newLocation),
                Convert.getRtcpSocketAddress(localAddress), encryption,
                new RtcpCheckProcessor(ClientBridge.RTP_VERSION));
    }

    private void doAddLocation(NetworkLocation newLocation,
            InetSocketAddress localAddress, RTPCrypt encryption,
            UdpPacketProcessor processor)
            throws IOException {
        System.err.println("Sender created for " + newLocation);
        SendThread sender = new SendThread(localAddress, newLocation);
        senders.put(newLocation, sender);
        encrypters.put(newLocation, encryption);
        processors.put(newLocation, processor);
        sender.start();
    }

    /**
     * Stops sending to a location
     * @param oldLocation The location to stop sending to
     */
    public void removeLocation(NetworkLocation oldLocation) {
        doRemoveLocation(oldLocation);
        doRemoveLocation(Convert.getRtcpLocation(oldLocation));
    }

    private void doRemoveLocation(NetworkLocation oldLocation) {
        SendThread sender = senders.get(oldLocation);
        if (sender != null) {
            System.err.println("Sender removed for " + oldLocation);
            sender.finish();
            UdpPacketProcessor processor = processors.get(oldLocation);
            if (processor instanceof RtpCheckProcessor) {
                RtpCheckProcessor proc = (RtpCheckProcessor) processor;
                sendByes(proc, oldLocation);
            }
            sender.close();
        } else {
            System.err.println("Error finding sender for " + oldLocation);
        }
        senders.remove(oldLocation);
        encrypters.remove(oldLocation);
        processors.remove(oldLocation);
    }

    /**
     * Gets a processor for a location
     * @param location The location
     * @return The processor
     */
    public UdpPacketProcessor getProcessor(NetworkLocation location) {
        return processors.get(location);
    }

    /**
     * Gets the current encryption for a location
     * @param location The location to get the encryption for
     * @return The encryption or null if none
     */
    public RTPCrypt getEncryption(NetworkLocation location) {
        return encrypters.get(location);
    }
}
