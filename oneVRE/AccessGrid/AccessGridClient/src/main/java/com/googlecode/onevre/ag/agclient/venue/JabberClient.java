/*
 * @(#)Client.java
 * Created: 31-May-2006
 * Version: 1
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

package com.googlecode.onevre.ag.agclient.venue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.GroupChat;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SSLXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;

import com.googlecode.onevre.protocols.events.eventserver.AgEvent;
import com.googlecode.onevre.protocols.events.eventserver.AgEventServer;

/**
 * Represents a client connection to a jabber room
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class JabberClient implements PacketListener {

    private Log log = LogFactory.getLog(this.getClass());

    private XMPPConnection connection = null;

    private GroupChat groupChat = null;

    private boolean done = false;

    private String groupName = null;

    private String clientUri = "";

    private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private Vector<String> roster = new Vector<String>();

    private boolean rosterChanged = false;

    private boolean ignorePastMessages = false;

    private Vector<JabberMessage> history = new Vector<JabberMessage>();

    private AgEventServer agEventServer = null;

    // Adds a message to the queue
    private void addMessage(Date date, String from, String message) {
        from = from.replace(groupName, "");
        if (from.startsWith("/")) {
            from = from.substring(1);
        }
        JabberMessage jabberMessage = new JabberMessage(
                from, message, dateFormat.format(date));
        history.add(jabberMessage);
        agEventServer.addEvent(new AgEvent(clientUri, "jabberAddMessage", jabberMessage));
    }

    private void addRoster(String name) {
        // TODO: Make this add an AG user
    }

    private void removeRoster(String name) {
        // TODO: Make this remove an AG user
    }

    /**
     * Creates a new Client
     * @param server The server to connect to
     * @param port The port to connect to
     * @param secure True if the connection is ssl
     * @param roomname The name of the room to connect to
     * @param nickname The name to display in the room
     * @param xmlRpcServer
     * @throws XMPPException
     */
    @SuppressWarnings("unchecked")
    public JabberClient(String server, int port, boolean secure, String venueUri,
            String roomname, String nickname, AgEventServer agEventServer)
            throws XMPPException {
        this.clientUri = venueUri;
        this.agEventServer = agEventServer;
        if (!secure) {
            connection = new XMPPConnection(server, port);
        } else {
            connection = new SSLXMPPConnection(server, port);
        }
        String username = "user" + System.currentTimeMillis();
        String password = String.valueOf(System.currentTimeMillis());
        AccountManager accounts = connection.getAccountManager();
        accounts.createAccount(username, password);
        connection.login(username, password);

        String conferenceServer = null;
        ServiceDiscoveryManager manager =
            ServiceDiscoveryManager.getInstanceFor(connection);
        DiscoverItems items = manager.discoverItems(
                connection.getServiceName());
        Iterator<DiscoverItems.Item> iterator = items.getItems();
        while (iterator.hasNext() && (conferenceServer == null)) {
            DiscoverItems.Item item = iterator.next();
            DiscoverInfo info = manager.discoverInfo(item.getEntityID());
            Iterator<DiscoverInfo.Identity> iter = info.getIdentities();
            while (iter.hasNext() && (conferenceServer == null)) {
                DiscoverInfo.Identity ident = iter.next();
                if (ident.getCategory().equals("conference")
                        && ident.getType().equals("text")) {
                    conferenceServer = item.getEntityID();
                }
            }
        }

        this.groupName = roomname + "@" + conferenceServer;
        groupChat = connection.createGroupChat(groupName);
        groupChat.addMessageListener(this);
        groupChat.addParticipantListener(this);
        boolean connected = false;
        while (!connected) {
            try {
                groupChat.join(nickname.replace("'", "&apos;"));
                connected = true;
            } catch (XMPPException e) {
                if (e.getXMPPError().getCode() != 409) {
                    throw e;
                }
                nickname += "_";
            }
        }
        agEventServer.addEvent(new AgEvent(clientUri, "jabberClearWindow", clientUri));

        Thread keepalive = new Thread() {
            public void run() {
                Presence presence = new Presence(Presence.Type.AVAILABLE);
                while (!done) {
                    connection.sendPacket(presence);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // Do Nothing
                    }
                }
            }
        };
        keepalive.start();

        if (groupChat != null) {
            Iterator<String> iter = groupChat.getParticipants();
            while (iter.hasNext()) {
                String from = (String) iter.next();
                from = from.replace(groupName, "");
                if (from.startsWith("/")) {
                    from = from.substring(1);
                }
                if (!from.equals("") && !roster.contains(from)) {
                    roster.add(from);
                    addRoster(from);
                }
            }
        }

        ConnectionListener connectionListener = new ConnectionListener() {
            public void connectionClosed() {
                // Don't do anything for expected connection closures
            }
            public void connectionClosedOnError(Exception e) {
                e.printStackTrace();
                addMessage(new Date(), "", "Error: " + e.getMessage());
                close();
            }
        };
        connection.addConnectionListener(connectionListener);
    }

    /**
     *
     * @see org.jivesoftware.smack.PacketListener#processPacket(
     *     org.jivesoftware.smack.packet.Packet)
     */
    public void processPacket(Packet packet) {
        if (packet instanceof Message) {
            Date now = new Date(System.currentTimeMillis() - 1000);
            Date date = new Date();
            PacketExtension delay = packet.getExtension("x", "jabber:x:delay");
            if ((delay != null) && (delay instanceof DelayInformation)) {
                DelayInformation delayinfo = (DelayInformation) delay;
                date = delayinfo.getStamp();
            }
            if (ignorePastMessages && date.before(now)) {
                return;
            }
            Message message = (Message) packet;
            String body = message.getBody();
            String from = message.getFrom();
            log.info("Received message from " + from);
            log.info("    " + body);
            addMessage(date, from, body);
        } else if (packet instanceof Presence) {
            Presence presence = (Presence) packet;
            Presence.Type type = presence.getType();
            String from = packet.getFrom();
            from = from.replace(groupName, "");
            if (from.startsWith("/")) {
                from = from.substring(1);
            }
            if (type != null) {
                if (type == Presence.Type.AVAILABLE) {
                    synchronized (roster) {
                        if (!roster.contains(from)) {
                            roster.add(from);
                            addRoster(from);
                            rosterChanged = true;
                            roster.notify();
                        }
                    }
                } else if (type == Presence.Type.UNAVAILABLE) {
                    synchronized (roster) {
                        if (roster.contains(from)) {
                            roster.remove(from);
                            removeRoster(from);
                            rosterChanged = true;
                            roster.notify();
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the jabber history from this session
     * @return All the messages that have passed so far
     */
    public JabberMessage[] getHistory() {
        return  history.toArray(new JabberMessage[0]);
    }

    /**
     * Closes the connection to the server
     */
    public void close() {
        done = true;
        addMessage(new Date(), "", "Disconnected *** ");
        groupChat.leave();
        AccountManager manager = connection.getAccountManager();
        try {
            manager.deleteAccount();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        connection.close();
    }

    /**
     * Sends a message to the group
     * @param message The message to send
     */
    public void setMessage(String message) {
        try {
//            System.err.print("Sending " + message + "...");
            groupChat.sendMessage(message);
            System.err.println("sent");
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the list of participants
     * @return the list of participants
     */
    public Vector<String> getRoster() {
        return roster;
    }

    /**
     * Waits for the roster to be changed
     * @return True if the roster was changed, false if the client was closed
     */
    public boolean isRosterChanged() {
        synchronized (roster) {
            while (!done && !rosterChanged) {
                try {
                    roster.wait();
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
        }
        if (rosterChanged) {
            rosterChanged = false;
            return true;
        }
        return false;
    }

    /**
     * Changes the nickname of the participant
     * @param newnickname The new nickname
     * @throws XMPPException
     */
    public void setNickname(String newnickname) throws XMPPException {
        groupChat.leave();
        ignorePastMessages = true;
        groupChat.join(newnickname.replace("'", "&apos;"));
    }

    /**
     * Gets the current nickname
     * @return the nickname
     */
    public String getNickname() {
        return groupChat.getNickname();
    }
}
