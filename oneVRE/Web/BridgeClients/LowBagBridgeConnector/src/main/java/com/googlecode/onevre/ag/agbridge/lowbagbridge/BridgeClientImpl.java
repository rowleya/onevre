/*
 * @(#)BridgeClientImpl.java
 * Created: 24 Oct 2007
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

package com.googlecode.onevre.ag.agbridge.lowbagbridge;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import javax.swing.JOptionPane;

import org.apache.xmlrpc.XmlRpcException;

import com.googlecode.onevre.ag.agbridge.BridgeClient;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.bridges.lowbag.client.LowBagClient;


/**
 * A Bridge Client for the LowBAG Bridge.
 * @author Sebastian Starke
 * @version 1.0
 */
public class BridgeClientImpl implements BridgeClient {

    private LowBagClient client = null;

    private InetAddress host = null;

    private int port = 0;

    private boolean running = false;

    /**
     * @see ag3.bridge.BridgeClient#init(java.net.InetAddress, int)
     */
    public final void init(final InetAddress host, final int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * @see ag3.bridge.BridgeClient#ping()
     */
    public void ping() {
        // Do Nothing
    }

    /**
     * @see ag3.bridge.BridgeClient#joinBridge(
     * 			ag3.interfaces.types.NetworkLocation[])
     */
    public final void joinBridge(final NetworkLocation[] locations)
            throws IOException {
        if (client == null) {
            client = new LowBagClient(host.getHostAddress().toString(),
                        port, locations);
        }
        try {
            if (client != null) {
                client.openServerConnecion();
                running = true;
            }
        } catch (XmlRpcException e) {
            JOptionPane.showMessageDialog(null, "Error joining bridge!");
        }
    }

    /**
     * @see ag3.bridge.BridgeClient#leaveBridge()
     */
    public final void leaveBridge() {
        client.closeServerConnecion();
        running = false;
    }

    /**
     * @see ag3.bridge.BridgeClient#receivePacket(
     *     ag3.interfaces.types.NetworkLocation)
     */
    public final DatagramPacket receivePacket(final NetworkLocation location) {
        return null;
    }

    /**
     * @see ag3.bridge.BridgeClient#sendPacket(java.net.DatagramPacket,
     *     ag3.interfaces.types.NetworkLocation)
     */
    public final void sendPacket(final DatagramPacket packet,
            final NetworkLocation location) {
        if (running) {
             InetSocketAddress address = new InetSocketAddress(
                    location.getHost().toString(),
                    location.getPort());
             try {
                client.sendPacket(packet, address);
             } catch (IOException e) {
                leaveBridge();
                JOptionPane.showMessageDialog(
                        null, "Error, could not send packet! Left bridge!");
             }
        }
    }

    /**
     * @see ag3.bridge.BridgeClient#isSinglePacketStream()
     */
    public final boolean isSinglePacketStream() {
        return true;
    }

    /**
     * @see ag3.bridge.BridgeClient#receivePacket()
     */
    public final DatagramPacket receivePacket() {
        return client.receivePacket();
    }

    /**
     * @see ag3.bridge.BridgeClient#setReceiveTimeout(int)
     */
    public final void setReceiveTimeout(final int timeout) {
        try {
            client.setReceiveTimeout(timeout);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null, "Error, could not set timeout!");
        }
    }

    /**
     * @see ag3.bridge.BridgeClient#getWarning()
     */
    public final String getWarning() {
        return null;
    }

    /**
     * @see ag3.bridge.BridgeClient#getOrder()
     */
    public final int getOrder() {
        return BridgeClient.ORDER_WORST_BRIDGE;
    }

    /**
     * @see ag3.bridge.BridgeClient#setDoLoopback(boolean)
     */
    public void setDoLoopback(boolean doLoopback) throws SocketException {
        // Does Nothing
    }
}