package com.googlecode.onevre.ag.agclient.venue;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

public class Group extends Thread {

    private LinkedList<String> messageQueue =
        new LinkedList<String>();

    private LinkedList<GroupClient> clients = new LinkedList<GroupClient>();

    private String groupId="";

    private boolean done = false;

    public Group(String groupId){
        this.groupId = groupId;
    }

    public String getGroupId(){
        return groupId;
    }

    public void addClient(GroupClient client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    public void close(GroupClient client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    public void closeAll() {
        System.out.println("closeAll");
        synchronized (messageQueue) {
            done = true;
            messageQueue.notifyAll();
            synchronized (clients) {
                Vector<GroupClient> clientList = new Vector<GroupClient>(clients);
                Iterator<GroupClient> iter = clientList.iterator();
                while  (iter.hasNext()) {
                    iter.next().close();
                }
            }
        }
    }

    public void addMessage(String message) {
        synchronized (messageQueue) {
            if (!done) {
                messageQueue.addLast(message);
                messageQueue.notifyAll();
            }
        }
    }

    public void run() {
        done = false;
        while (!done) {
            processMessage(false);
        }
    }


    private void processMessage(boolean clientsChanged) {
        String message = null;
        synchronized (messageQueue) {
           if (!done && messageQueue.isEmpty()) {
                try {
                    messageQueue.wait();
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
            if (!messageQueue.isEmpty()) {
                message = messageQueue.removeFirst();
            }
            messageQueue.notifyAll();
        }
        if (message != null) {
            synchronized (clients) {
                Iterator<GroupClient> iter = clients.iterator();
                while  (iter.hasNext()) {
                    iter.next().addMessage(message);
                }
            }
        }
    }



}
