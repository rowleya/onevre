/*
 * @(#)BridgeClient.java
 * Created: 29 Aug 2007
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

package com.googlecode.onevre.ag.agbridge;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import com.googlecode.onevre.ag.types.network.NetworkLocation;


/**
 * Represents a bridge
 * @author Andrew G D Rowley
 * @version 1.0
 */
public interface BridgeClient {

    /**
     * Best bridge in order - to be tried before anything else (equivalent to
     *     using multicast)
     */
    final int ORDER_BEST_BRIDGE = Integer.MIN_VALUE / 2;

    /**
     * A great bridge in order
     */
    final int ORDER_GREAT_BRIDGE = (Integer.MIN_VALUE / 6) * 2;

    /**
     * A good bridge in order
     */
    final int ORDER_GOOD_BRIDGE = (Integer.MIN_VALUE / 6) * 1;

    /**
     * An average bridge in order
     */
    final int ORDER_AVERAGE_BRIDGE = 0;

    /**
     * A poor bridge in order
     */
    final int ORDER_POOR_BRIDGE = (Integer.MAX_VALUE / 6) * 1;

    /**
     * A rubbish bridge in order
     */
    final int ORDER_RUBBISH_BRIDGE = (Integer.MAX_VALUE / 6) * 2;

    /**
     * Worst bridge in order - use only as a last resort
     */
    final int ORDER_WORST_BRIDGE = Integer.MAX_VALUE / 2;

    /**
     * Initialises the connection to the bridge
     * @param host The host of the bridge
     * @param port The port of the bridge
     */
    void init(InetAddress host, int port);

    /**
     * Contacts the bridge and waits for a response
     * Used to time the RTT of the bridge
     * @throws IOException
     */
    void ping() throws IOException;

    /**
     * Joins a bridge
     * @param locations The locations to create bridges for
     * @throws IOException
     */
    void joinBridge(NetworkLocation[] locations) throws IOException;

    /**
     * Leaves a bridge
     * @throws IOException
     */
    void leaveBridge() throws IOException;

    /**
     * Determines if the bridge receives packets in a single stream or as
     * separate streams.
     *
     * If the bridge receives packets in a single stream, only receivePacket()
     * will ever be called to receive a packet.
     *
     * If the bridge receives packets in separate streams, only
     * receievePacket(MulticastNetworkLocation) will ever be called to receive
     * a packet.
     *
     * @return True if the bridge is a single-stream bridge, false if the
     *     bridge is a multi-stream bridge
     */
    boolean isSinglePacketStream();

    /**
     * Gets the next packet received from a multi-stream bridge.
     *
     * This function may block until a packet is received or leaveBridge
     * is called.
     *
     * @param location The location from which to receive a packet (must be
     *     one of the locations passed to joinBridge)
     *
     * @return The next packet, or null if leaveBridge is called before the
     *     next packet is received.
     * @throws IOException
     */
    DatagramPacket receivePacket(NetworkLocation location)
        throws IOException;

    /**
     * Gets the next packet received from a single-stream bridge.
     *
     * This function may block until a packet is received or leaveBridge
     * is called.
     *
     * @return The next packet, or null if leaveBridge is called before the
     *     next packet is received.  A call to getSocketAddress on the returned
     *     packet must return one of the address/port combinations passed to
     *     joinBridge
     * @throws IOException
     */
    DatagramPacket receivePacket() throws IOException;

    /**
     * Sends a packet to the bridge
     *
     * @param packet The packet to send
     * @param location The location to send the packet to (must be
     *     one of the locations passed to joinBridge)
     * @throws IOException
     */
    void sendPacket(DatagramPacket packet, NetworkLocation location)
        throws IOException;

    /**
     * Sets the timeout on calls to receivePacket
     *
     * SocketTimeoutException is thrown from the call if a packet is not
     * received within this time
     *
     * @param timeout The timeout in Milliseconds, or 0 for no timeout
     * @throws IOException
     */
    void setReceiveTimeout(int timeout) throws IOException;

    /**
     * Gets a warning to issue if this bridge is being used (for example if
     * the bridge gets you through a firewall, but results in bad service)
     * @return A string containing the warning or null if none
     */
    String getWarning();

    /**
     * Gets the order in which the bridge should be considered for use
     * @return An integer representing the order.
     *         Smaller integers will consider this bridge type before others.
     */
    int getOrder();

    /**
     * Sets whether to receive packets sent with this bridge back at the bridge again
     * @param doLoopback true to perform loopback, false to stop loopback
     * @throws SocketException
     */
    void setDoLoopback(boolean doLoopback) throws SocketException;


}
