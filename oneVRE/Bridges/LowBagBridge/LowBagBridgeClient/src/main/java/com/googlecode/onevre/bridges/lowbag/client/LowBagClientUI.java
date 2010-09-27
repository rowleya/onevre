/*
 * @(#)LowBagClientUI.java
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

package com.googlecode.onevre.bridges.lowbag.client;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetSocketAddress;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;


import org.apache.xmlrpc.XmlRpcException;

import com.googlecode.onevre.bridges.lowbag.common.StreamHandler;
import com.googlecode.vicovre.media.renderer.RGBRenderer;
import com.googlecode.vicovre.media.rtp.RTCPReport;
import com.googlecode.vicovre.repositories.rtptype.RTPType;

/**
 * A Interface for the LowBAG Bridge.
 * @author Sebastian Starke
 * @version 1.0
 */
public class LowBagClientUI extends JFrame implements ActionListener,
        MouseListener, MenuListener {

    public static final String CONNECTED = "Connected";
    public static final String DISCONNECTED = "Disconnected";
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGTH = 300;
    private static final String TITLE = "LowBAG Bridge Client";
    private static final String FILE = "File";
    private static final String HELP = "Help";
    private static final String CONNECT = "Connect";
    private static final String DISCONNECT = "Disconnect";
    private static final String PROPERTIES1 = "Properties";
    private static final String EXIT = "Exit";
    private static final String ABOUT = "About";
    private static final String INFO_MESSAGE =
        "You are using the LowBAG Bridge client, developed by Sebastian Starke\n at the University of Manchester";
    private static final int PREVIEW_HEIGTH = 100;
    private static final int PREVIEW_WIDTH = 100;
    private static final int BOX_WIDTH = 350;
    private static final int BOX_HEIGTH = 90;
    private LowBagClient client = null;
    private JLabel statusBar;
    private JPanel contentPane = new JPanel();
    private GridLayout contentPaneLayout = new GridLayout();
    private JMenuItem menuItemDisconnect = null;
    private JMenuItem menuItemConnect = null;
    private JMenuItem menuItemVic = null;
    private JMenu menuFile = null;

    /**
     * Creates a new LowBAG bridge interface.
     * @param client2 The client which invokes the interface.
     */
    public LowBagClientUI(final LowBagClient client2) {
        client = client2;
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(Color.WHITE);
        setJMenuBar(createMenuBar());
        setIconImage(loadIcon("/org/jdesktop/swingx/icon/ag.png"));
        setContentPane(createContentPane());
        setSize(WINDOW_WIDTH, WINDOW_HEIGTH);
        setVisible(true);
        setLocationRelativeTo(null);
        toFront();
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        setStatus(DISCONNECTED);
    }

    /**
     * @see javax.swing.event.MenuListener#menuCanceled(
     * 			javax.swing.event.MenuEvent)
     */
    public void menuCanceled(final MenuEvent e) {
        // Do nothing
    }

    /**
     * @see javax.swing.event.MenuListener#menuDeselected(
     * 			javax.swing.event.MenuEvent)
     */
    public void menuDeselected(final MenuEvent e) {
        // Do nothing
    }

    /**
     * @see javax.swing.event.MenuListener#menuSelected(
     * 			javax.swing.event.MenuEvent)
     */
    public final void menuSelected(final MenuEvent e) {
        if (e.getSource() == menuFile && menuItemVic != null) {
            //- vic
            if (!client.isConnected()) {
                menuItemVic.setEnabled(false);
            } else {
                menuItemVic.setEnabled(true);
            }
        }
    }

    /**
     * Gets the client interface dimensions.
     * @param number Number of reports/streams to display.
     * @param width client interface width
     * @param height client interface height
     * @return Dimensions, cols and rows for the client interface.
     */
    public final int[] getDimensions(final int number,
            final int width, final int height) {
        int[] dim = new int[4];
        Dimension s = contentPane.getSize();
        //- cols
        dim[0] = (int) (s.getWidth() / width);
        //- rows
        dim[1] = number / dim[0];
        //- width
        dim[2] = width * dim[0];
        //- height
        dim[3] = height * dim[1];
        return dim;
    }

    protected final void processWindowEvent(final WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            close();
        }
        super.processWindowEvent(e);
    }

    /**
     * Creates a menu item for the bridge client interface menu.
     * @return The created menu item.
     * @param name Item name
     * @param description Item description
     * @param mnemonic Key code
     */
    private JMenuItem createMenuItem(final String name,
            final String description, final int mnemonic) {
        return createMenuItem(name, description, name, mnemonic);
    }

    /**
     * Creates a menu item for the bridge client interface menu.
     * @return The created menu item.
     * @param name Item name
     * @param description Item description
     * @param actionCommand Action command
     * @param mnemonic Key code
     */
    private JMenuItem createMenuItem(final String name,
            final String description, final String actionCommand,
            final int mnemonic) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.getAccessibleContext().setAccessibleDescription(
                description);
        menuItem.setActionCommand(actionCommand);
        menuItem.addActionListener(this);
        if (mnemonic != -1) {
            menuItem.setMnemonic(mnemonic);
        }
        return menuItem;
    }

    /**
     * Creates the menu bar of the bridge client interface.
     * @return The created menu bar.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar;

        //- create the menu bar
        menuBar = new JMenuBar();

        //- file menu
        menuFile = new JMenu(FILE);
        menuFile.addMenuListener(this);
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuFile.getAccessibleContext().setAccessibleDescription("File menu");
        menuItemConnect = createMenuItem(CONNECT,
                "Connect to bridge server", KeyEvent.VK_C);
        menuFile.add(menuItemConnect);
        menuItemDisconnect = createMenuItem(DISCONNECT,
                "Disconnect from bridge server", KeyEvent.VK_D);
        menuFile.add(menuItemDisconnect);
        menuFile.add(createMenuItem(PROPERTIES1,
                "Change bridge properties", KeyEvent.VK_P));
        menuFile.addSeparator();
        menuFile.add(createMenuItem(EXIT,
                "Leave the service manager", KeyEvent.VK_X));
        menuBar.add(menuFile);

        //- help menu
        JMenu menuHelp;
        menuHelp = new JMenu(HELP);
        menuHelp.setMnemonic(KeyEvent.VK_H);
        menuHelp.getAccessibleContext().setAccessibleDescription(
                "Information about this program");
        menuHelp.add(createMenuItem(ABOUT,
                "More information about this program", KeyEvent.VK_A));
        menuBar.add(menuHelp);

        return menuBar;
    }

    /**
     * Creates the content pane of the bridge client interface.
     * @return The created content pane.
     */
    private Container createContentPane() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(true);
        container.setBackground(Color.WHITE);
        JScrollPane pane = new JScrollPane(contentPane);
        container.add(pane, BorderLayout.CENTER);
        statusBar = new JLabel("", JLabel.RIGHT);
        container.add(statusBar, BorderLayout.PAGE_END);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(0, 15));
        return container;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *     java.awt.event.ActionEvent)
     */
    public final void actionPerformed(final ActionEvent e) {
        String source = e.getActionCommand();
        if (source.equals(EXIT)) {
            close();
        } else if (source.equals(CONNECT)) {
            connect();
        } else if (source.equals(DISCONNECT)) {
            disconnect();
        } else if (source.equals(PROPERTIES1)) {
            configureProperties();
        } else if (source.equals(ABOUT)) {
            showAboutDialog();
        }
    }

    /**
     * Closes the bridge client interface and
     * the connection to the server.
     */
    public final void close() {
        disconnect();
        dispose();
    }

    /**
     * Connects the bridge client to the server.
     */
    public final void connect() {
        try {
            client.openServerConnecion();
            setStatus(CONNECTED);
        } catch (XmlRpcException e1) {
            JOptionPane.showMessageDialog(
                this, "Error, could not connect to bridge server!");
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(
                this, "Error, while connecting to bridge server!");
        }
    }

    /**
     * Disconnects the bridge client form the server.
     */
    public final void disconnect() {
        client.closeServerConnecion();
        setStatus(DISCONNECTED);
    }

    /**
     * Opens the configure properties dialog.
     */
    private void configureProperties() {
        PropertiesDialog dialog = new PropertiesDialog(
                this, "Configure Properties");
        dialog.setVisible(true);
    }

    /**
     * Shows the about dialog.
     */
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this, INFO_MESSAGE, "LowBAG",
                JOptionPane.INFORMATION_MESSAGE, new ImageIcon(
                        loadIcon("/org/jdesktop/swingx/icon/ag.png")));
    }

    /**
     * Shows open client error message.
     */
    public static final void errorOpenClient() {
        JOptionPane.showMessageDialog(
                null, "Error, openening client!");
    }

    /**
     * Shows open video client error message.
     */
    public static final void errorOpenVideoClient() {
        JOptionPane.showMessageDialog(
                null, "Error, openening video client!");
    }

    /**
     * Shows server connection error message.
     */
    public final void errorServerConnection() {
        JOptionPane.showMessageDialog(
                null, "Error, with connection to server!");
    }

    /**
     * Sets the bridge client status.
     * @param status The new status
     */
    public final void setStatus(final String status) {
        String s = "";
        if (status == CONNECTED) {
            s = "Connected to " + client.getServerAddress() + ":"
                + client.getServerControlPort();
            menuItemConnect.setEnabled(false);
            menuItemDisconnect.setEnabled(true);
        } else if (status == DISCONNECTED) {
            s = "Disconnected";
            removeAllReports();
            menuItemConnect.setEnabled(true);
            menuItemDisconnect.setEnabled(false);
        } else {
            s = "";
        }
        statusBar.setText(s);
    }

    /**
     * Sets a report for a particular stream.
     * @param ssrc SSRC of the report/stream to remove
     * @throws IOException Error with the handed over SSRC!
     */
    public final void setReport(final String ssrc) throws IOException {
        if (ssrc == null) {
            throw new IOException("Error with the handed over SSRC!");
        }
        StreamHandler streamHandler =
                client.getStreamsHandler().getStream(ssrc);
        int[] dim = getDimensions(
                client.getStreamsHandler().getStreams().size(),
                BOX_WIDTH, BOX_HEIGTH);
        contentPane.setPreferredSize(new Dimension(dim[2], dim[3]));
        contentPaneLayout.setColumns(dim[0]);
        contentPaneLayout.setRows(dim[1]);

        //- get current box or create new one
        ReportBox reportBox = (ReportBox) streamHandler.getReportBox();
        if (reportBox == null) {
            reportBox = new ReportBox(client, ssrc);
            contentPane.add(reportBox);
            streamHandler.setReportBox(reportBox);
        }

        //- update RTCP data
        RTCPReport report = streamHandler.getReport();
        if (report != null) {
            if (report.getName() != null) {
                reportBox.setName(report.getName());
            }
            if (report.getCName() != null) {
                reportBox.setMail(report.getCName());
            }
        }

        //- update RTP data
        long lastUpdate = streamHandler.getLastUpdate();
        RGBRenderer renderer = streamHandler.getRenderer();
        if (lastUpdate != 0 && renderer != null) {
            reportBox.setInfo(streamHandler.getLastBitRate(),
                    streamHandler.getLastFrameRate());
            reportBox.setPreview(streamHandler.getPreview(
                    PREVIEW_WIDTH, PREVIEW_HEIGTH));
            reportBox.setRateEnabled(true);
        }
        contentPane.updateUI();
    }

    /**
     * Removes a particular report/stream of the client interface.
     * @param ssrc SSRC of the report/stream to remove
     * @throws IOException Error with the handed over SSRC!
     */
    public final void removeReport(final String ssrc) throws IOException {
        if (ssrc == null) {
            throw new IOException("Error with the handed over SSRC!");
        }
        ReportBox reportBox = (ReportBox)
            client.getStreamsHandler().getStream(ssrc).getReportBox();
        int[] dim = getDimensions(
                client.getStreamsHandler().getStreams().size(),
                BOX_WIDTH, BOX_HEIGTH);
        contentPaneLayout.setColumns(dim[0]);
        contentPaneLayout.setRows(dim[1]);

        if (reportBox != null) {
            contentPane.remove(reportBox);
            contentPane.updateUI();
        }
    }

    /**
     * Removes all reports of the client interface.
     */
    public final void removeAllReports() {
        contentPane.removeAll();
        contentPane.updateUI();
    }

    /**
     * Gets the current rate of a report rather stream.
     * @param report Report containing the requested stream rate
     * @return Current adjusted rate of the stream.
     * @throws IOException Error with the handed over report!
     */
    public final int getReportRate(final Object report)
            throws IOException {
        if (report == null || !(report instanceof ReportBox)) {
            throw new IOException("Error with the handed over report!");
        }
        return ((ReportBox) report).getRate();
    }

    /**
     * Loads a icon using the class path as root.
     * @param image Path to image/icon
     * @return The loaded image.
     */
    private Image loadIcon(final String image) {
        return new ImageIcon(this.getClass().getResource(image)).getImage();
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(final MouseEvent e) {
        // Do Nothing
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(final MouseEvent e) {
        // Do Nothing
    }

    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(final MouseEvent e) {
        // Do Nothing
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(final MouseEvent e) {
        // Do Nothing
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(
     *     java.awt.event.MouseEvent)
     */
    public void mouseReleased(final MouseEvent e) {
        // Do Nothing
    }

    /**
     * Box for stream settings and information.
     */
    private class ReportBox extends JPanel
            implements ItemListener, ChangeListener {

        private JLabel name = new JLabel();
        private JLabel mail = new JLabel();
        private JLabel info = new JLabel();
        private JPanel preview = new JPanel();
        private Component image = new JLabel("  No Preview!");
        private JCheckBox receive = new JCheckBox("receive", true);
        private JSlider rate = new JSlider(0,100,100);
        private JLabel rateLabel = new JLabel(" 100 %");
        private String ssrc = null;
        private LowBagClient client1 = null;
        private Dimension dimension = null;

        /**
         * Report box constructor.
         * @param client2 Calling client object
         * @param ssrc2 Unique stream identifier
         * @throws IOException SSRC error!
         */
        public ReportBox(final LowBagClient client2, final String ssrc2)
                throws IOException {
            if (ssrc2 == null) {
                throw new IOException("SSRC error!");
            }
            ssrc = ssrc2;
            client1 = client2;

            //- settingsBox
            JPanel settingsBox = new JPanel(new GridLayout(1,3));
            settingsBox.add(receive);
            receive.addItemListener(this);
            settingsBox.add(rate);
            rate.addChangeListener(this);
            rate.setEnabled(false);
            settingsBox.add(rateLabel);

            //- optionsBox
            JPanel optionsBox = new JPanel(new GridLayout(4,1));
            optionsBox.add(name);
            optionsBox.add(mail);
            optionsBox.add(info);
            optionsBox.add(settingsBox);
            dimension = new Dimension(BOX_WIDTH - PREVIEW_WIDTH, BOX_HEIGTH);
            optionsBox.setMaximumSize(dimension);
            optionsBox.setMinimumSize(dimension);
            optionsBox.setPreferredSize(dimension);

            //- previewBox
            preview = new JPanel(new GridLayout(1,1));
            preview.add(image);
            dimension = new Dimension(PREVIEW_WIDTH, BOX_HEIGTH);
            preview.setMaximumSize(dimension);
            preview.setMinimumSize(dimension);
            preview.setPreferredSize(dimension);

            // bufferBox
            JPanel bufferBox = new JPanel();
            dimension = new Dimension(10, BOX_HEIGTH);
            bufferBox.setMaximumSize(dimension);
            bufferBox.setMinimumSize(dimension);
            bufferBox.setPreferredSize(dimension);

            //- layoutBox
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setBorder(BorderFactory.createEtchedBorder());
            add(bufferBox);
            add(preview);
            add(bufferBox);
            add(optionsBox);
        }

        /**
         * @see java.awt.event.ItemListener#itemStateChanged(
         *     java.awt.event.ItemEvent)
         */
        public void itemStateChanged(final ItemEvent e) {
            Object source = e.getItemSelectable();
            if (source == receive) {
                try {
                    if (e.getStateChange() == ItemEvent.DESELECTED) {
                        client1.addUpdate(this.ssrc, "rate", 0);
                        setRate(0);
                    } else {
                        client1.addUpdate(this.ssrc, "rate", 100);
                        setRate(100);
                    }
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(
                            null, "Error, changing status of stream!");
                }
            }
        }

        /**
         * Sets the stream name text.
         * @param s Stream name
         */
        public void setName(final String s) {
            name.setText(s);
        }

        /**
         * Sets the preview image.
         * @param image2 Preview Image
         * @todo Instead of removing and adding use a update method!
         */
        public void setPreview(final Component image2) {
            preview.removeAll();
            preview.add(image2);
        }

        /**
         * Sets the mail text.
         * @param s Mail address
         */
        public void setMail(final String s) {
            mail.setText(s);
        }

        /**
         * Sets the info text.
         * @param bps Packet/data rate in bps
         * @param fps Frame rate in fps
         */
        public void setInfo(final long bps, final float fps) {
            String s = fps + " f/s  " + bps / 1000 + " kb/s";
            info.setText(s);
        }

        /**
         * Sets the value of receive object.
         * @param value Receive on/off
         */
        public void setReceive(final boolean value) {
            if (receive.isSelected() != value) {
                receive.setSelected(value);
            }
        }

        /**
         * Sets the value of rate object.
         * @param n New rate
         */
        public void setRate(final int n) {
            try {
                client1.addUpdate(this.ssrc, "rate", n);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        null, "Error, changing rate of stream!");
            }
            rate.setValue(n);
            setRateLabel(" " + n + " %");
        }

        /**
         * Enables or diables the rate object.
         * @param enabled Enables or diables the rate object
         */
        public void setRateEnabled(final boolean enabled) {
            rate.setEnabled(enabled);
        }

        /**
         * Gets the value of the rate object.
         * @return Value of the rate object.
         */
        public int getRate() {
            return rate.getValue();
        }

        /**
         * Sets the text for rate label.
         * @param s New text
         */
        public void setRateLabel(final String s) {
            rateLabel.setText(s);
        }

        /**
         * @see javax.swing.event.ChangeListener#stateChanged(
         *     javax.swing.event.ChangeEvent)
         */
        public void stateChanged(final ChangeEvent arg0) {
            int n = rate.getValue();
            if (n == 0) {
                setReceive(false);
            } else {
                setReceive(true);
            }
            setRate(n);
        }
    }

    /**
     * Bridge client properties dialog (class).
     */
    private final class PropertiesDialog extends JDialog
            implements ActionListener, KeyListener, ItemListener {

        private JTextField listenPort = null;
        private JTextField serverAddress = null;
        private JTextField serverControlPort = null;
        private JTextField vicAddress = null;
        private JTextField vicPort = null;
        private JTextField ratAddress = null;
        private JTextField ratPort = null;
        private JTextField updateInterval = null;
        private JComboBox bridgeFormat = null;
        private JCheckBox autoRatio = null;
        private JButton ok = null;
        private JButton cancel = null;
        private boolean autoRatioSwitch = false;

        /**
         * Bridge client properties dialog constructor.
         * @param parent The calling frame.
         * @param title The windows/frame title.
         */
        private PropertiesDialog(final JFrame parent, final String title) {
            super(parent, title, true);
            int n = 0;
            JPanel content = new JPanel();

            //- bridge server control port
            content.add(new JLabel("Bridge server address"));
            serverAddress = new JTextField(
                client.getServerAddress());
            serverAddress.addKeyListener(this);
            content.add(serverAddress);
            n++;

            //- bridge server control port
            content.add(new JLabel("Bridge server control port"));
            serverControlPort = new JTextField(
                Integer.toString(client.getServerControlPort()));
            serverControlPort.addKeyListener(this);
            content.add(serverControlPort);
            n++;

            //- bridge client listen port
            content.add(new JLabel("Bridge client listen port"));
            listenPort = new JTextField(
                Integer.toString(client.getListenPort()));
            listenPort.addKeyListener(this);
            content.add(listenPort);
            n++;

            //- video consumer address
            content.add(new JLabel("Video consumer address"));
            vicAddress = new JTextField(
                client.getVicSocket().getAddress().getHostAddress().toString());
            vicAddress.addKeyListener(this);
            content.add(vicAddress);
            n++;

            //- video consumer port
            content.add(new JLabel("Video consumer port"));
            vicPort = new JTextField(
                Integer.toString(client.getVicSocket().getPort()));
            vicPort.addKeyListener(this);
            content.add(vicPort);
            n++;

            //- audio consumer address
            content.add(new JLabel("Audio consumer address"));
            ratAddress = new JTextField(
                client.getRatSocket().getAddress().getHostAddress().toString());
            ratAddress.addKeyListener(this);
            content.add(ratAddress);
            n++;

            //- audio consumer port
            content.add(new JLabel("Audio consumer port"));
            ratPort = new JTextField(
                Integer.toString(client.getRatSocket().getPort()));
            ratPort.addKeyListener(this);
            content.add(ratPort);
            n++;

            //- update interval
            content.add(new JLabel("Preview update interval in seconds"));
            updateInterval = new JTextField(
                Long.toString(client.getStreamsHandler().getUpdateInterval() / 1000));
            updateInterval.addKeyListener(this);
            content.add(updateInterval);
            n++;

            //- encoding format
            content.add(new JLabel("Bridge encoding format"));
            bridgeFormat = new JComboBox(client.getOutputFormats());
            bridgeFormat.setSelectedItem(client.getOutputFormat());
            content.add(bridgeFormat);
            n++;

            //- auto ratio switch
            content.add(new JLabel("Auto ratio"));
            autoRatioSwitch = client.isAutoRatio();
            autoRatio = new JCheckBox("", autoRatioSwitch);
            autoRatio.addItemListener(this);
            content.add(autoRatio);
            n++;

            //- add OK button
            ok = new JButton("OK");
            content.add(ok);
            ok.addKeyListener(this);
            ok.addActionListener(this);

            //- add Cancel button
            cancel = new JButton("Cancel");
            content.add(cancel);
            cancel.addKeyListener(this);
            cancel.addActionListener(this);
            n++;

            //- pack all into one
            content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            content.setLayout(new GridLayout(n, 2, 7, 7));
            content.setSize(WINDOW_WIDTH, WINDOW_HEIGTH);
            add(content);
            setSize(WINDOW_WIDTH, WINDOW_HEIGTH);
            pack();
        }

        /**
         * Saves the new properties.
         */
        public void saveProperties() {
            try {
                if (!Integer.toString(client.getListenPort()
                        ).equals(listenPort.getText().trim())) {
                    client.setListenPort(Integer.parseInt(
                            listenPort.getText().trim()));
                }
                if (!client.getServerAddress().equals(
                        serverAddress.getText().trim())) {
                    client.setServerAddress(
                            serverAddress.getText().trim());
                }
                if (!Integer.toString(client.getServerControlPort()
                        ).equals(serverControlPort.getText().trim())) {
                    client.setServerControlPort(Integer.parseInt(
                            serverControlPort.getText().trim()));
                }
                client.setVicSocket(new InetSocketAddress(
                        vicAddress.getText().trim(),
                        Integer.parseInt(vicPort.getText().trim())));
                client.setRatSocket(new InetSocketAddress(
                        ratAddress.getText().trim(),
                        Integer.parseInt(ratPort.getText().trim())));
                int interval = 0;
                try {
                     interval = Integer.parseInt(updateInterval.getText().trim());
                } catch(NumberFormatException nFE) {
                    // Do Nothing
                }
                if (interval != 0 && interval > 0) {
                    client.getStreamsHandler().setUpdateInterval(interval * 1000);
                }
                client.setOutputFormat((RTPType)
                        bridgeFormat.getSelectedItem());
                client.setAutoRatio(autoRatioSwitch);
            } catch (IOException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(
                        null, "Error, while saving properties!");
            }
        }

        /**
         * @see java.awt.event.KeyListener#keyPressed(
         *     java.awt.event.KeyEvent)
         */
        public void keyPressed(final KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_ENTER && e.getSource() != cancel) {
                saveProperties();
                dispose();
            }
            if (key == KeyEvent.VK_ESCAPE) {
                dispose();
            }
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *     java.awt.event.ActionEvent)
         */
        public void actionPerformed(final ActionEvent e) {
            String button = e.getActionCommand();
            if (button.equals("OK")) {
                saveProperties();
                dispose();
            } else if (button.equals("Cancel")) {
                dispose();
            }
        }

        /**
         * @see java.awt.event.KeyListener#keyReleased(
         *     java.awt.event.KeyEvent)
         */
        public void keyReleased(final KeyEvent arg0) {
            // Do nothing
        }

        /**
         * @see java.awt.event.KeyListener#keyTyped(
         *     java.awt.event.KeyEvent)
         */
        public void keyTyped(final KeyEvent arg0) {
            // Do nothing
        }

        /**
         * @see java.awt.event.ItemListener#itemStateChanged(
         *     java.awt.event.ItemEvent)
         */
        public void itemStateChanged(final ItemEvent e) {
            Object source = e.getItemSelectable();
            if (source == autoRatio) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    autoRatioSwitch = false;
                } else {
                    autoRatioSwitch = true;
                }
            }
        }
    }
}