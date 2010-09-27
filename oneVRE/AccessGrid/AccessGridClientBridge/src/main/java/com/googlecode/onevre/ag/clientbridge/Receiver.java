/*
 * @(#)Receiver.java
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
import java.net.SocketTimeoutException;
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
 * Receives packets from a bridge and forwards them on to a local datagram
 * socket
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Receiver {

    private BridgeClient bridge = null;

    private HashMap<NetworkLocation, InetSocketAddress> addressMap =
        new HashMap<NetworkLocation, InetSocketAddress>();

    private HashMap<InetSocketAddress, NetworkLocation> inetAddressMap =
        new HashMap<InetSocketAddress, NetworkLocation>();

    private HashMap<Object, UdpPacketProcessor> processors =
        new HashMap<Object, UdpPacketProcessor>();

    private HashMap<NetworkLocation, RTPCrypt> decrypters =
        new HashMap<NetworkLocation, RTPCrypt>();

    private HashMap<NetworkLocation, MultiStreamReceiveThread> receivers =
        new HashMap<NetworkLocation, MultiStreamReceiveThread>();

    private SingleStreamReceiveThread singleReceiver = null;

    private MulticastSocket socket = null;

    private boolean pause = false;

    private Integer pauseSync = new Integer(0);

    /**
     * Creates a new Receiver
     *
     * @throws IOException
     */
    public Receiver()
            throws IOException {
        socket = new MulticastSocket();
        socket.setTimeToLive(0);

        Iterator<NetworkLocation> iterator =
            addressMap.keySet().iterator();
        while (iterator.hasNext()) {
            NetworkLocation location = iterator.next();
//            InetSocketAddress localAddress = addressMap.get(location);
            inetAddressMap.put(getAddress(location), location);
        }
    }

    private static InetSocketAddress getAddress(NetworkLocation location) {
        return new InetSocketAddress(location.getHost(), location.getPort());
    }

    /**
     * Sets the bridge
     * @param bridge The new bridge to use
     */
    public void setBridge(BridgeClient bridge) {
        if ((this.bridge == null) || (this.bridge.isSinglePacketStream()
                != bridge.isSinglePacketStream())) {
            if (bridge.isSinglePacketStream()) {
                Iterator<NetworkLocation> iterator =
                    receivers.keySet().iterator();
                while (iterator.hasNext()) {
                    NetworkLocation location = iterator.next();
                    MultiStreamReceiveThread receiver = receivers.get(location);
                    receiver.finish();
                }
                receivers.clear();
                this.bridge = bridge;
                singleReceiver = new SingleStreamReceiveThread();
                singleReceiver.start();
            } else {
                if (singleReceiver != null) {
                    singleReceiver.finish();
                    singleReceiver = null;
                }
                this.bridge = bridge;
                Iterator<NetworkLocation> iterator =
                    addressMap.keySet().iterator();
                while (iterator.hasNext()) {
                    NetworkLocation location = iterator.next();
                    MultiStreamReceiveThread receiver =
                        new MultiStreamReceiveThread(location);
                    receivers.put(location, receiver);
                    receiver.start();
                }
            }
        } else {
            this.bridge = bridge;
        }
        try {
            this.bridge.setReceiveTimeout(1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the port from which local packets will be sent
     * @return The local port
     */
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    /**
     * Gets the address from which local packets will be sent
     * @return The local address
     */
    public InetAddress getLocalHost() {
        return socket.getLocalAddress();
    }

    /**
     * Sets the decrypter to use
     * @param decryption The decrypter
     */
    public void setDecryption(RTPCrypt decryption) {
        Iterator<NetworkLocation> iter = addressMap.keySet().iterator();
        while (iter.hasNext()) {
            NetworkLocation location = iter.next();
            decrypters.put(location, decryption);
        }
    }

    private boolean decrypt(NetworkLocation location, DatagramPacket packet,
            boolean isRtp) {
        RTPCrypt decrypter = decrypters.get(location);
        if (decrypter != null) {
            if ((packet.getLength() % decrypter.getBlockSize()) != 0) {
                return false;
            }
            byte[] out = new byte[
                decrypter.getDecryptOutputSize(
                        packet.getLength())];
            int length = 0;

            try {
                if (isRtp) {
                    length = decrypter.decryptData(packet.getData(),
                            packet.getOffset(), packet.getLength(),
                            out, 0);

                } else {
                    length = decrypter.decryptCtrl(packet.getData(),
                            packet.getOffset(), packet.getLength(),
                            out, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (length > 0) {
                int maxlen = packet.getLength();
                if (length < packet.getLength()) {
                    maxlen = length;
                }
                System.arraycopy(out, 0, packet.getData(),
                        packet.getOffset(), maxlen);
                packet.setLength(maxlen);
            } else {
                packet.setLength(0);
            }
        }
        return true;
    }

    private class MultiStreamReceiveThread extends Thread {

        private NetworkLocation location = null;

        private boolean done = false;

        private MultiStreamReceiveThread(NetworkLocation location) {
            this.location = location;
        }

        /**
         *
         * @see java.lang.Thread#run()
         */
        public void run() {

            while (!done) {
                synchronized (pauseSync) {
                    while (!done && pause) {
                        try {
                            pauseSync.wait();
                        } catch (InterruptedException e) {
                            // Do Nothing
                        }
                    }
                }
                try {
                    DatagramPacket packet = bridge.receivePacket(location);
                    if (!done) {
                        if (packet != null) {
                            if (decrypt(location, packet,
                                    (location.getPort() % 2) == 0)) {
                                UdpPacketProcessor processor =
                                    processors.get(location);
                                if (processor.process(packet)) {
                                    InetSocketAddress localAddress =
                                        addressMap.get(location);
                                    packet.setSocketAddress(localAddress);
                                    //System.err.println("Forwarding packet to " + localAddress);
                                    socket.send(packet);
                                }
                            }
                        }
                    }
                } catch (SocketTimeoutException e) {
                    // Do Nothing
                } catch (IOException e) {
                    if (!done && !pause) {
                        e.printStackTrace();
                        done = true;
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
    }

    private class SingleStreamReceiveThread extends Thread {

        private boolean done = false;

        /**
         *
         * @see java.lang.Thread#run()
         */
        public void run() {
            while (!done) {
                synchronized (pauseSync) {
                    while (!done && pause) {
                        try {
                            pauseSync.wait();
                        } catch (InterruptedException e) {
                            // Do Nothing
                        }
                    }
                }
                try {
                    DatagramPacket packet = bridge.receivePacket();
                	//System.err.println("### SingleStreamReceiveThread/run (1)");

                    if (!done && (packet != null)) {
                        NetworkLocation location =
                            inetAddressMap.get(packet.getSocketAddress());
                        InetSocketAddress sendAddress =
                            addressMap.get(location);

                        /*
                    	System.err.println("### SingleStreamReceiveThread/run (2): "
                    			+ " packet>" + packet.getSocketAddress()
                    			+ " location>" + location
                    			+ " sendAddress>" + sendAddress
                    			);*/

                        if (sendAddress != null) {
                        	//System.err.println("### SingleStreamReceiveThread/run (3)");

                            UdpPacketProcessor processor = processors.get(
                                    location);
                            if (decrypt(location, packet,
                                    (sendAddress.getPort() % 2) == 0)) {
                            	//System.err.println("### SingleStreamReceiveThread/run (4)");

                                if (processor.process(packet)) {
                                	//System.err.println("### SingleStreamReceiveThread/run (5)");

                                    packet.setSocketAddress(sendAddress);
                                    socket.send(packet);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    if (!done && !pause) {
                        e.printStackTrace();
                        done = true;
                    }
                }
            }
        }

        private void finish() {
            done = true;
        }
    }

    /**
     * Pauses the receiver
     *
     */
    public void pause() {
        synchronized (pauseSync) {
            pause = true;
            pauseSync.notifyAll();
        }
    }

    /**
     * Resumes the receiver
     *
     */
    public void resume() {
        synchronized (pauseSync) {
            pause = false;
            pauseSync.notifyAll();
        }
    }

    /**
     * Closes the socket
     */
    public void close() {
        Vector<NetworkLocation> locations = new Vector<NetworkLocation>(
                addressMap.keySet());
        for (int i = 0; i < locations.size(); i++) {
            doRemoveLocation(locations.get(i));
        }
        socket.close();
    }

    /**
     * Handles a packet as if it was received from the remote location
     * @param location The location to send the packet from
     * @param packet The packet to send
     * @throws IOException
     */
    private void receivePacket(NetworkLocation location,
            DatagramPacket packet) throws IOException {
        InetSocketAddress address = addressMap.get(location);
        if (address != null) {
            packet.setSocketAddress(address);
            socket.send(packet);
        }
    }

    private void sendByes(RtpCheckProcessor proc, NetworkLocation location) {
        Long[] ssrcs = proc.getSsrcs();
        for (int i = 0; i < ssrcs.length; i++) {
            byte[] bye = Convert.getBye(ssrcs[i]);
            DatagramPacket packet = new DatagramPacket(bye, bye.length);
            try {
                receivePacket(Convert.getRtcpLocation(location), packet);
                System.err.println("Bye sent for remote participant " +  ssrcs[i] + " to " + Convert.getRtcpLocation(location));
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

        System.err.println("Changing recevier from " + oldLocation + " to " + newLocation);
        if ((bridge != null) && !bridge.isSinglePacketStream()) {
            MultiStreamReceiveThread receiver = receivers.get(oldLocation);
            if (receiver != null) {
                receiver.setLocation(newLocation);
                receivers.remove(oldLocation);
                receivers.put(newLocation, receiver);
            }
        }
        UdpPacketProcessor processor = processors.get(oldLocation);
        if ((processor != null) && processor instanceof RtpCheckProcessor) {
            RtpCheckProcessor proc = (RtpCheckProcessor) processor;
            sendByes(proc, oldLocation);
        }
        InetSocketAddress localAddress = addressMap.get(oldLocation);
        addressMap.remove(oldLocation);
        addressMap.put(newLocation, localAddress);
        inetAddressMap.remove(getAddress(oldLocation));
        inetAddressMap.put(getAddress(newLocation), newLocation);
        decrypters.remove(oldLocation);
        decrypters.put(newLocation, encryption);
        processors.remove(oldLocation);
        processors.put(newLocation, processor);
    }

    /**
     * Adds a new location to send
     * @param newLocation The new location to send to
     * @param localAddress The local address to send from
     * @param encryption The encryption to use
     */
    public void addLocation(NetworkLocation newLocation,
            InetSocketAddress localAddress, RTPCrypt encryption) {
        doAddLocation(newLocation, localAddress, encryption,
                new RtpCheckProcessor(ClientBridge.RTP_VERSION));
        doAddLocation(Convert.getRtcpLocation(newLocation),
                Convert.getRtcpSocketAddress(localAddress), encryption,
                new RtcpCheckProcessor(ClientBridge.RTP_VERSION));
    }

    private void doAddLocation(NetworkLocation newLocation,
            InetSocketAddress localAddress, RTPCrypt encryption,
            UdpPacketProcessor processor) {
        System.err.println("Adding receiver for " + newLocation);
        decrypters.put(newLocation, encryption);
        processors.put(newLocation, processor);
        addressMap.put(newLocation, localAddress);
        inetAddressMap.put(getAddress(newLocation), newLocation);
        if (bridge != null) {
            if (!bridge.isSinglePacketStream()) {
                System.err.println("Adding multi receiver");
                MultiStreamReceiveThread receiver =
                    new MultiStreamReceiveThread(newLocation);
                receivers.put(newLocation, receiver);
                receiver.start();
            } else if (addressMap.size() == 1) {
                System.err.println("Starting single receiver");
                singleReceiver = new SingleStreamReceiveThread();
                singleReceiver.start();
            }
        }
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
        System.err.println("Removing receiver for " + oldLocation);
        if (bridge != null) {
            if (!bridge.isSinglePacketStream()) {
                MultiStreamReceiveThread receiver = receivers.get(oldLocation);
                receiver.finish();
                receivers.remove(oldLocation);
            } else if (addressMap.size() == 1) {
                System.err.println("Stopping single receiver");
                singleReceiver.finish();
            }
            UdpPacketProcessor processor = processors.get(oldLocation);
            if (processor instanceof RtpCheckProcessor) {
                RtpCheckProcessor proc = (RtpCheckProcessor) processor;
                sendByes(proc, oldLocation);
            }
        }
        InetSocketAddress localAddress = getAddress(oldLocation);
        decrypters.remove(oldLocation);
        processors.remove(oldLocation);
        addressMap.remove(oldLocation);
        inetAddressMap.remove(localAddress);
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
     * Gets the local address for a location
     * @param location The location to get the address for
     * @return The local address
     */
    public InetSocketAddress getLocalAddress(NetworkLocation location) {
        return addressMap.get(location);
    }
}
