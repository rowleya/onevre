package com.googlecode.onevre.web.servicemanager;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.lang.StringEscapeUtils;


import com.googlecode.onevre.ag.agbridge.BridgeClientCreator;
import com.googlecode.onevre.ag.clientbridge.ClientBridge;
import com.googlecode.onevre.ag.types.server.AGServiceManager;
import com.googlecode.onevre.ag.types.service.AGBridgeConnectorDescription;
import com.googlecode.onevre.protocols.soap.soapserver.SoapServer;
import com.googlecode.onevre.protocols.xmlrpc.common.XMLDeserializer;

/**
 * An AGServiceManager
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class AGServiceManagerMain {

    private static final int SEPARATION = 5;

    private static final int DEFAULT_PORT = 9876;

    private static class DoShutdown extends Thread {

        private AGServiceManager manager = null;

        private SoapServer server = null;

        private DoShutdown(AGServiceManager manager, SoapServer server) {
            this.manager = manager;
            this.server = server;
        }

        /**
         *
         * @see java.lang.Thread#run()
         */
        public void run() {
            try {
                server.end();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                manager.stopServices();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                manager.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The main method that allows you to run a remote service manager
     * @param args The arguments
     */
    public static void main(String[] args) {
        //System.setSecurityManager(null);
        int port = DEFAULT_PORT;
        String name = "ServiceManager";
        for (int i = 0; i < args.length; i++) {
            if (args[i].trim().startsWith("port")) {
                String portstring = args[i].trim();
                port = Integer.parseInt(portstring.substring(
                        portstring.indexOf("=") + 1));
            } else if (args[i].trim().startsWith("name")) {
                name = args[i].trim();
                name = name.substring(name.indexOf("=") + 1);
            } else if (args[i].trim().startsWith("bridgeConnectors")) {
                String bridgeConnectors = args[i].trim();
                bridgeConnectors = bridgeConnectors.substring(
                        bridgeConnectors.indexOf("=") + 1);
                try {
                    BridgeClientCreator.setBridgeConnectors((HashMap<String, AGBridgeConnectorDescription>)
                    		XMLDeserializer.deserialize(StringEscapeUtils.unescapeXml(bridgeConnectors)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            SoapServer server = new SoapServer(port, false);
            server.start();
            AGServiceManager manager = new AGServiceManager();
            manager.setClientBridge(new ClientBridge());
            Runtime.getRuntime().addShutdownHook(
                    new DoShutdown(manager, server));
            server.registerObject("/" + name, manager);
            manager.setUri(server.findURLForObject(manager));
            JFrame ui = new JFrame(name + ":" + port);
            JLabel label = new JLabel(" AGServiceManager running on port "
                    + port + " ");
            JButton close = new JButton("Exit");
            close.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            close.setAlignmentX(Component.CENTER_ALIGNMENT);
            ui.setLayout(new BoxLayout(ui.getContentPane(), BoxLayout.Y_AXIS));
            ui.getContentPane().add(label);
            ui.getContentPane().add(Box.createVerticalStrut(SEPARATION));
            ui.getContentPane().add(close);
            ui.pack();
            ui.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            ui.setLocationRelativeTo(null);
            ui.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
