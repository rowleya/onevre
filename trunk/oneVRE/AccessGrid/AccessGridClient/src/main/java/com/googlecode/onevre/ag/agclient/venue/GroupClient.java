
package com.googlecode.onevre.ag.agclient.venue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Vector;

import javax.net.ssl.SSLException;




public class GroupClient extends Thread {

    public static final int AG_GROUP_MESSAGE_PROTOCOL_VERSION = 1;

    public static final int AG_GROUP_MESSAGE_PROTOCOL_ITERATION = 0; // to identify updates

    private LinkedList<String> queue = new LinkedList<String>();

    private boolean done = false;

    private String clientId = null;

    private String privateId = null;

    private String groupId = null;

    private String connectionId = null;

    private static final int WAIT_TIME = 10000;

    private boolean firstMessageReceived = false;
    private boolean firstMessageSent = false;

    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;
    private Group group = null;

    // Gets all the messages in the queue
    private String getEventMessage() {
        String eventMessage = null;
        synchronized (queue) {
            if (!done && queue.isEmpty()) {
                try {
                    queue.wait(WAIT_TIME);
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
            if (!queue.isEmpty()) {
                eventMessage = queue.removeFirst();
			} else if (!done) {
                eventMessage = null;
            }
        }
        return eventMessage;
    }

    // Adds a message to the queue
    public void addMessage(String message) {
        synchronized (queue) {
            queue.addLast(message);
            queue.notifyAll();
        }
    }

    private void sendString(DataOutputStream output, String string)
    throws IOException {
//        System.err.println("Sending " + string);
        output.writeInt(string.length());
        output.write(string.getBytes("UTF-8"));
        output.flush();
    }

    private String receiveString(DataInputStream input)
    throws IOException {
        int length = input.readInt();
        byte[] stringarray = new byte[length];
        input.read(stringarray);
        return new String(stringarray,"UTF-8");
    }

    public String getGroupId(){
        return groupId;
    }

    public String getClientId() {
        return clientId;
    }
    public String getConnectionId() {
        return connectionId;
    }

    public Vector<String> getSessions() {
        Vector<String> sessions = new Vector<String>();
        return sessions;
    }

    /**
     * Creates a new Client
     *
     */
    public GroupClient(Socket socket ) {
        this.socket=socket;
        InputStream inputstream;
        try {
            inputstream = socket.getInputStream();
            BufferedInputStream bufferedin = new BufferedInputStream(inputstream);
            input = new DataInputStream(bufferedin);
            OutputStream outputstream = socket.getOutputStream();
            BufferedOutputStream buffered = new BufferedOutputStream(outputstream);
            output = new DataOutputStream(buffered);

            String firstLine=receiveString(input);
            int groupIdLength = Integer.parseInt(firstLine.substring(0,2));
            groupId = firstLine.substring(2,groupIdLength+2);
            clientId = socket.getInetAddress().getCanonicalHostName()+":"+socket.getLocalPort();
            int connectionIdLength = Integer.parseInt(firstLine.substring(groupIdLength+2,groupIdLength+4));
            connectionId = firstLine.substring(groupIdLength+4, groupIdLength+4+connectionIdLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setGroup(Group grp){
        this.group = grp;
        String versionString = AG_GROUP_MESSAGE_PROTOCOL_VERSION + AG_GROUP_MESSAGE_PROTOCOL_ITERATION + connectionId;
        try {
            sendString(output, versionString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread receive = new Thread() {
            public void run() {
                String message = null;
                while (!done) {
                    try {
                        message = receiveString(input);
                        group.addMessage(message);
                    } catch (SocketException e) {
                        System.err.println("Event client closing after"
                                + " disconnection from client");
                        done = true;
                    } catch (EOFException e) {
                        System.err.println("Event client closing after"
                                + " disconnection from client");
                        done = true;
                    } catch (SSLException e) {
                        System.err.println("Event client closing after"
                                + " disconnection from client");
                        done = true;
                    } catch (IOException e) {
                         e.printStackTrace();
                    }
                }
            }
        };
        receive.start();
        Runtime.getRuntime().addShutdownHook(new DoShutdown());
    }

    public void run() {
//        String message = null;
        done = false;
        while (!done) {
            String ann = getEventMessage();
            if (ann != null) {
/*                if (!firstMessageSent){
                    firstMessageSent=true;
                    NumberFormat format = NumberFormat.getInstance();
                    format.setMaximumFractionDigits(0);
                    format.setMinimumIntegerDigits(2);
                    String data = format.format(clientId.length()) + clientId;
                    data += format.format(connectionId.length()) + connectionId;
                    try {
                        sendString(output, data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
*/                try {
                    sendString(output, ann);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Waits until the messages are available and then gets them
     *
     * @return The messages in the queue
     */
    public String getMessage() {
        GroupClient client = null;
        String out = "<type>None</type>";
        String ann = getEventMessage();
        if (ann != null) {
            out = ann;
        }
        return out;
    }

    /**
     * Closes the connection to the server
     */
    public void close() {
        done = true;
        group.close(this);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class DoShutdown extends Thread {
        public void run() {
            close();
        }
    }

}
