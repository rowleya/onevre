package com.googlecode.onevre.web.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableModel;

import com.googlecode.onevre.ag.common.interfaces.ServiceManager;
import com.googlecode.onevre.ag.interfaces.ServiceManagerInterface;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.server.AGNodeService;
import com.googlecode.onevre.ag.types.server.AGServiceManager;
import com.googlecode.onevre.ag.types.service.AGServiceDescription;
import com.googlecode.onevre.ag.types.service.AGServicePackageDescription;
import com.googlecode.onevre.ag.types.service.parameter.AGParameter;
import com.googlecode.onevre.utils.Preferences;

/**
 * Node Management user interface
 * @version 1.0
 */
public class NodeManagementUI extends JFrame implements ActionListener,
        MouseListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_SERVICE_MANAGER_PORT = 11000;
    private static final String TITLE = "Access Grid Node Management";
    private static final String FILE = "File";
    private static final String SERVICEMANAGER = "ServiceManager";
    private static final String SERVICE = "Service";
    private static final String HELP = "Help";
    private static final String CONNECTTONODE = "Connect to Node";
    private static final String LOADCONFIG = "Load Configuration";
    private static final String STORECONFIG = "Store Configuration";
    private static final String EXIT = "Exit";
    private static final String ADD = "Add...";
    private static final String REMOVE = "Remove";
    private static final String ENABLE = "Enable";
    private static final String DISABLE = "Disable";
    private static final String CONFIGURE = "Configure...";
    private static final String ABOUT = "About";
    private static final String NAME = "Name";
    private static final String STATUS = "Status";
    private static final String DISCONNECTED = "Disconnected";
    private static final String CONNECTED = "Connected";
    private static final String ENABLED = "Enabled";
    private static final String DISABLED = "Disabled";
    private static final String NOSERVICES = "No services";
    private static final String SMADD = "Service Manager add";
    private static final String SMREMOVE = "Service Manager remove";
    private static final String SADD = "Service add";
    private static final String SREMOVE = "Service remove";
    private static final String ROOT = "Service Managers";
    private static final String SELECTCONFIG =
        "Select a configuration file to load";
    private static final String LOADCONFIGDLG = "Load Configuration Dialog";
    private static final String INFO_MESSAGE =
        "You are using the Portal Access Grid client developed at "
        + "the University of Manchester";

    private AGNodeService nodeServiceHandle = null;

    private JLabel statusBar;

    // private JTreeTable tree;
    private JXTreeTable tree;
    private Vector<ListItem> list;
    private JScrollPane pane;

    private Vector<String> recentServiceManagerList;

    private HashMap<String, AGServicePackageDescription> services = null;

    private ClientProfile profile = null;

    private JPopupMenu serviceMenu = null;

    private JPopupMenu managerMenu = null;

    /**
     * Creates a new NodeManagementUI
     * @param services The services to manage
     * @param profile The client profile
     */
    public NodeManagementUI(HashMap<String,
            AGServicePackageDescription> services,
            ClientProfile profile) {
        this.services = services;
        this.profile = profile;
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(Color.WHITE);
        setJMenuBar(createMenuBar());
        setIconImage(loadIcon("/org/jdesktop/swingx/icon/ag.png"));
        setContentPane(createContentPane());

        setSize(450, 300);
        setVisible(true);
        setLocationRelativeTo(null);
        toFront();
    }

    private JMenuItem createMenuItem(String name, String description) {
        return createMenuItem(name, description, name, -1);
    }

    private JMenuItem createMenuItem(String name, String description,
            int mneumonic) {
        return createMenuItem(name, description, name, mneumonic);
    }

    private JMenuItem createMenuItem(String name, String description,
            String actionCommand) {
        return createMenuItem(name, description, actionCommand, -1);
    }

    private JMenuItem createMenuItem(
            String name, String description, String actionCommand,
            int mnemonic) {
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

    private JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;

        // Create the menu bar.
        menuBar = new JMenuBar();
        serviceMenu = new JPopupMenu();
        managerMenu = new JPopupMenu();

        // Build the first menu.
        menu = new JMenu(FILE);
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("File menu");
        menuBar.add(menu);

        // a group of JMenuItems
        menu.add(createMenuItem(CONNECTTONODE, "Connect to a Node Service"));
        menu.addSeparator();
        menu.add(createMenuItem(LOADCONFIG,
                "Load a Node Service Configuration"));
        menu.add(createMenuItem(STORECONFIG,
                "Store a Node Service Configuration"));
        menu.addSeparator();
        menu.add(createMenuItem(EXIT, "Leave the service manager",
                KeyEvent.VK_X));

        menu = new JMenu(SERVICEMANAGER);
        menu.setMnemonic(KeyEvent.VK_S);
        menu.getAccessibleContext().setAccessibleDescription(
                "Service Manager options");
        menuBar.add(menu);

        menu.add(createMenuItem(ADD, "Add a Service Manager", SMADD));
        menu.add(createMenuItem(REMOVE, "Remove the selected Service Manager",
                SMREMOVE));
        managerMenu.add(createMenuItem(ADD, "Add a Service", SADD));
        managerMenu.add(createMenuItem(REMOVE,
                "Remove the selected Service Manager", SMREMOVE));

        menu = new JMenu(SERVICE);
        menu.setMnemonic(KeyEvent.VK_S);
        menu.getAccessibleContext().setAccessibleDescription("Service options");
        menuBar.add(menu);

        menu.add(createMenuItem(ADD, "Add a Service", SADD));
        menu.add(createMenuItem(REMOVE, "Remove the selected Service",
                SREMOVE));
        menu.addSeparator();
        menu.add(createMenuItem(ENABLE, "Enables the selected Service"));
        menu.add(createMenuItem(DISABLE, "Disables the selected Service"));
        menu.addSeparator();
        menu.add(createMenuItem(CONFIGURE, "Configure the selected Service"));

        serviceMenu.add(createMenuItem(REMOVE, "Remove the selected Service",
                SREMOVE));
        serviceMenu.addSeparator();
        serviceMenu.add(createMenuItem(ENABLE, "Enables the selected Service"));
        serviceMenu.add(createMenuItem(DISABLE,
                "Disables the selected Service"));
        serviceMenu.addSeparator();
        serviceMenu.add(createMenuItem(CONFIGURE,
                "Configure the selected Service"));

        menu = new JMenu(HELP);
        menu.setMnemonic(KeyEvent.VK_H);
        menu.getAccessibleContext().setAccessibleDescription(
                "Information about this program");
        menuBar.add(menu);

        menu.add(createMenuItem(ABOUT, "More information about this program",
                KeyEvent.VK_A));

        return menuBar;
    }

    private void addHost() {
        System.err.println("add Host");
        if (recentServiceManagerList == null) {
            recentServiceManagerList = new Vector<String>();
        }
        ServiceChoiceDialog scd = new ServiceChoiceDialog(this,
                "Add Service Manager", recentServiceManagerList);
        scd.setVisible(true);
        String url = scd.getComboValue();
        if (url == null) {
            return;
        }
        url = buildServiceUrl(url, "http", DEFAULT_SERVICE_MANAGER_PORT,
                "ServiceManager");
        ServiceManagerInterface serviceManager = null;
        try {
            serviceManager = new ServiceManager(url);
            url = serviceManager.getDescription().getUri();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!recentServiceManagerList.contains(url)) {
            recentServiceManagerList.add(url);
        }
        try {
            System.err.println("Exception in AddHost");
            nodeServiceHandle.addServiceManager(serviceManager);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        updateUI();
    }

    private void removeServiceManager() {
        Vector<ListItem> selections = this.getSelectedServiceManagers();
        System.err.println("NodemanagementUI removeServiceManager "
                + selections.size());
        if (selections.size() == 0) {
            System.err.println("No service manager selected!");
            return;
        }
        try {
            for (int i = 0; i < selections.size(); i++) {
                try {
                    ServiceManagerInterface manager =
                        selections.get(i).getServiceManager();
                    if (manager instanceof AGServiceManager) {
                        JOptionPane.showMessageDialog(this,
                            "Cannot delete the built-in manager!");
                    } else {
                        nodeServiceHandle.removeServiceManager(
                            manager);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Vector<ListItem> getSelectedServiceManagers() {
        Vector<ListItem> retSel = new Vector<ListItem>();
        int[] selection = tree.getSelectedRows();
        for (int i = 0; i < selection.length; i++) {
            if (list.get(selection[i] + 1).isNode()) {
                if (!retSel.contains(list.get(selection[i] + 1))) {
                    retSel.add(list.get(selection[i] + 1));
                    System.err.println("add sm "
                            + list.get(selection[i] + 1).getName());
                }
            } else {
                System.err.println("parentpath "
                        + tree.getPathForRow(selection[i]).getParentPath()
                                .toString());
                int parent = tree.getRowForPath(tree
                        .getPathForRow(selection[i]).getParentPath());
                if (parent != -1
                        && !retSel.contains(list.get(parent + 1))) {
                    retSel.add(list.get(parent + 1));
                    System.err.println("add parent "
                            + list.get(parent + 1).getName());
                }
            }
        }
        return retSel;
    }

    private String buildServiceUrl(String url, String defaultproto,
            int defaultPort, String defaultPath) {
        Pattern hostre = Pattern.compile("^[\\w.-]*$");
        Pattern hostportre = Pattern.compile("^[\\w.-]*:[\\d]*$");
        Pattern protohostre = Pattern.compile("^[\\w]*://[\\w.-]*$");
        Pattern protohostportre = Pattern.compile("^[\\w]*://[\\w.-]*:[\\d]*$");

        // check for host only
        if (hostre.matcher(url).matches()) {
            String host = url;
            url = defaultproto.concat("://").concat(host).concat(":").concat(
                    Integer.toString(defaultPort)).concat("/").concat(
                    defaultPath);
        } else if (hostportre.matcher(url).matches()) {
            String hostport = url;
            url = defaultproto.concat("://").concat(hostport).concat("/")
                    .concat(defaultPath);
        } else if (protohostre.matcher(url).matches()) {
            String protohost = url;
            url = protohost.concat(":").concat(Integer.toString(defaultPort))
                    .concat("/").concat(defaultPath);
        } else if (protohostportre.matcher(url).matches()) {
            String protohostport = url;
            url = protohostport.concat("/").concat(defaultPath);
        }
        return url;
    }

    private void addService() {
        Vector<ListItem> selections = this.getSelectedServiceManagers();
        if (selections.size() == 0) {
            System.err.println("No Service Manager selected for service!");
            return;
        }
        if (selections.size() > 1) {
            System.err.println("Multiple Hosts selected");
            return;
        }

        ListItem serviceManager = selections.firstElement();

        // Get services available
        try {
            Vector<AGServicePackageDescription> servicePackages =
                new Vector<AGServicePackageDescription>(services.values());
            String[] availServices = new String[servicePackages.size()];
            for (int i = 0; i < servicePackages.size(); i++) {
                availServices[i] = servicePackages.get(i).getName();
            }
            String ret = (String) JOptionPane.showInputDialog(this,
                    "Select Service to Add", "Add Service: Select Service",
                    JOptionPane.INFORMATION_MESSAGE, new ImageIcon(
                            loadIcon("/org/jdesktop/swingx/icon/ag.png")),
                    availServices, null);
            if (ret != null) {
                AGServicePackageDescription serviceToAdd = null;
                for (int i = 0; i < servicePackages.size(); i++) {
                    if (servicePackages.get(i).getName().compareTo(ret) == 0) {
                        serviceToAdd = servicePackages.get(i);
                        break;
                    }
                }
                if (serviceToAdd == null) {
                    System.err.println("Can't add NULL service");
                    return;
                }
                System.err.println("NodeManagementUI service to add "
                        + serviceToAdd);
                serviceManager.getServiceManager().addService(serviceToAdd,
                                new AGParameter[0], profile);
            }
        } catch (Exception e) {
            System.err.println("Add service failed");
            e.printStackTrace();
            return;
        }
        updateUI();

    }

    /**
     * Enable the selected service(s)
     *
     */
    private void enableService() {
        // Integer[] selection=tree.getSelectionRows();
        int[] selection = tree.getSelectedRows();
        if (selection == null || selection.length == 0) {
            System.err.println("no service selected!");
            return;
        }
        for (int i = 0; i < selection.length; i++) {
            ListItem item = list.get(selection[i] + 1);
            if (!item.isNode()) {
                try {
                    ServiceManagerInterface manager = item.getServiceManager();
                    manager.enableService(item.getDescription(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        updateUI();
    }

    private void disableService() {
        System.err.println("disable Service");
        // Integer[] selection=tree.getSelectionRows();
        int[] selection = tree.getSelectedRows();
        if (selection == null || selection.length == 0) {
            System.err.println("no service selected!");
            return;
        }
        for (int i = 0; i < selection.length; i++) {
            ListItem item = list.get(selection[i] + 1);
            if (!item.isNode()) {
                try {

                    // nodeServiceHandle.setServiceEnabled(item.getUrl(),0);
                    ServiceManagerInterface manager = item.getServiceManager();
                    manager.enableService(item.getDescription(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        updateUI();
    }

    private void loadConfiguration() {
        String[] configs;
        try {
            configs = nodeServiceHandle.getConfigurations();
        } catch (Exception e) {
            System.err.println("error getting config");
            e.printStackTrace();
            return;
        }
        if (configs == null) {
            return;
        }
        String selectedConfig = (String) JOptionPane.showInputDialog(this,
                SELECTCONFIG, LOADCONFIGDLG, JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon(loadIcon("/org/jdesktop/swingx/icon/ag.png")),
                configs, configs[0]);
        if (selectedConfig == null) {
            return;
        }
        for (int i = 0; i < configs.length; i++) {
            if (configs[i].compareTo(selectedConfig) == 0) {
                try {
                    System.err.println("loading config " + selectedConfig);
                    nodeServiceHandle.loadConfiguration(configs[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                break;
            }
        }
        updateUI();
    }

    /**
     *
     * @see java.awt.event.ActionListener#actionPerformed(
     *     java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String source = e.getActionCommand();
        System.err.println("Menu item: " + source);
        if (source.equals(EXIT)) {
            // Do Nothing
        } else if (source.equals(CONNECTTONODE)) {
            // Do Nothing
        } else if (source.equals(SMADD)) {
            addHost();
        } else if (source.equals(SADD)) {
            addService();
        } else if (source.equals(SMREMOVE)) {
            removeServiceManager();
        } else if (source.equals(SREMOVE)) {
            removeService();
        } else if (source.equals(ENABLE)) {
            enableService();
        } else if (source.equals(DISABLE)) {
            disableService();
        } else if (source.equals(LOADCONFIG)) {
            loadConfiguration();
        } else if (source.equals(STORECONFIG)) {
            storeConfiguration();
        } else if (source.equals(ABOUT)) {
            showAboutDialog();
        } else if (source.equals(CONFIGURE)) {
            configureService();
        }
    }

    private void configureService() {
        int[] selection = tree.getSelectedRows();
        if (selection == null || selection.length != 1) {
            System.err.println("no service selected or too many!");
            return;
        }

        ListItem item = list.get(selection[0] + 1);
        if (!item.isNode()) {
            try {
                AGServiceDescription desc = item.getDescription();
                ServiceManagerInterface manager = item.getServiceManager();
                AGParameter[] config = manager.getServiceConfiguration(desc);
                if (config.length > 0) {
                    ConfigureDialog dialog = new ConfigureDialog(this,
                            "Configure " + desc.getName(), config);
                    dialog.setVisible(true);
                    if (dialog.isOK()) {
                        config = dialog.getParams();
                        Vector<AGParameter> conf = new Vector<AGParameter>();
                        for (int i = 0; i < config.length; i++) {
                            conf.add(config[i]);
                        }
                        manager.setServiceConfiguration(desc, conf.toArray(new AGParameter[0]));
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            desc.getName() + " has no configuration options");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this, INFO_MESSAGE, "PAG",
                JOptionPane.INFORMATION_MESSAGE, new ImageIcon(
                        loadIcon("/org/jdesktop/swingx/icon/ag.png")));

    }

    private void storeConfiguration() {
        try {
            String[] configs = nodeServiceHandle
                    .getConfigurations();
            for (int i = 0; i < configs.length; i++) {
                System.err.println("nodeConfigDescr. " + configs[i]);
            }
            StoreConfigDialog scd = new StoreConfigDialog(this,
                    "Store Configuration", configs);
            scd.setVisible(true);
            if (scd.getValue() == null) {
                return;
            }
            SaveItem save = scd.getValue();
            String config = null;
            for (int i = 0; i < configs.length; i++) {
                if (save.name.compareTo(configs[i]) == 0) {
                    config = configs[i];
                }
            }
            if (config == null) {
                config = save.name;
            }
            try {
                nodeServiceHandle.storeConfiguration(config);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (save.defaultchecked) {
                Preferences.getInstance().setStringValue(
                    Preferences.DEFAULT_NODE_CONFIG, config);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void removeService() {
        System.err.println("removeService");
        int[] selections = tree.getSelectedRows();
        if (selections.length == 0) {
            System.err.println("No service selected!");
            return;
        }
        System.err.println("selected rows " + selections.length + " "
                + selections);
        try {
            for (int i = selections.length - 1; i >= 0; i--) {
                ListItem item = list.get(selections[i] + 1);
                if (!item.isNode()) {
                    ServiceManagerInterface manager = item.getServiceManager();
                    manager.removeService(item.getDescription());
                    list.remove(selections[i] + 1);
                }
            }
            updateUI();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Container createContentPane() {
        // Create the content-pane-to-be.
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);
        contentPane.setBackground(Color.WHITE);

        list = new Vector<ListItem>();
        ListItem root = new ListItem();
        root.setName(ROOT);
        list.add(root);

        tree = new JXTreeTable(new NodeManagementTableModel());
        tree.setRowHeight(25);
        tree.addMouseListener(this);

        statusBar = new JLabel(DISCONNECTED, JLabel.RIGHT);
        statusBar.setOpaque(true);
        // statusBar.setBackground(Color.LIGHT_GRAY);
        statusBar.setPreferredSize(new Dimension(450, 15));

        contentPane.add(statusBar, BorderLayout.PAGE_END);
        pane = new JScrollPane(tree);
        pane.setBackground(Color.WHITE);
        contentPane.add(pane, BorderLayout.CENTER);

        return contentPane;
    }

    /**
     * Attaches the user interface to a node
     * @param nodeService The node to attach to
     */
    public void attachToNode(AGNodeService nodeService) {
        nodeServiceHandle = nodeService;
        try {
            if (nodeServiceHandle != null) {
                statusBar.setText(CONNECTED);
                updateUI();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateUI() {

        // get the current selected items from the user interface
        // Vector<Object> selItems=getSelectedItems();
        // Vector<String> selectionsURLs=new Vector<String>();
        // for(int i=0;i<selItems.size();i++) {
        // selectionsURLs.add(((AGService)selItems).getUri());
        // }
        synchronized (tree.getTreeLock()) {
            try {
                ServiceManagerInterface[] serviceManagers =
                    nodeServiceHandle.getServiceManagers();
                list.removeAllElements();
                ListItem root = new ListItem();
                root.setName(ROOT);
                list.add(root);
                System.err.println("serviceManagers " + serviceManagers.length);
                for (int i = 0; i < serviceManagers.length; i++) {
                    ListItem listItem;
                    if (list.size() == 1) {
                        listItem = list.get(0);
                        listItem.setNode(true);
                    }
                    listItem = new ListItem();

                    if (serviceManagers[i] instanceof ServiceManager) {
                        listItem.setName(
                                ((ServiceManager) serviceManagers[i]).getUrl());
                    } else if (serviceManagers[i] instanceof AGServiceManager) {
                        listItem.setName("Local Service Manager");
                    }
                    listItem.setServiceManager(serviceManagers[i]);
                    listItem.setNode(true);
                    list.add(listItem);
                    AGServiceDescription[] serviceList =
                        serviceManagers[i].getServices();
                    for (int j = 0; j < serviceList.length; j++) {
                        listItem = new ListItem();
                        String name = serviceList[j].getName();
                        listItem.setName(name);
                        listItem.setServiceManager(serviceManagers[i]);
                        listItem.setDescription(serviceList[j]);
                        if (serviceManagers[i].isServiceEnabled(serviceList[j])) {
                            listItem.setStatus(ENABLED);
                        } else {
                            listItem.setStatus(DISABLED);
                        }
                        list.add(listItem);
                    }
                    if (serviceList.length == 0) {
                        listItem = new ListItem();
                        listItem.setName(NOSERVICES);
                        System.err.println("add " + NOSERVICES);
                        list.add(listItem);
                    }
                }
                tree.setTreeTableModel(new NodeManagementTableModel());
                tree.expandAll();
                tree.repaint();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    Vector<Object> getSelectedItems() {
        return new Vector<Object>();
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = NodeManagementUI.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        }
        System.err.println("Couldn't find file: " + path);
        return null;
    }

    private Image loadIcon(String image) {
        URL imgURL = this.getClass().getResource(image);
        System.err.println("URL " + imgURL);
        return new ImageIcon(this.getClass().getResource(image)).getImage();
    }

    private class SaveItem {
        private String name;
        private boolean defaultchecked = false;
    }

    private class ListItem extends Object {

        /**
         *
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return name;
        }

        private String name = "";
        private String status = "";
        private boolean node = false;
        private AGServiceDescription description = null;
        private ServiceManagerInterface serviceManager = null;

        private boolean isNode() {
            return node;
        }

        private void setNode(boolean node) {
            this.node = node;
        }

        private String getName() {
            return name;
        }

        private String getStatus() {
            return status;
        }

        private void setName(String name) {
            this.name = name;
        }

        private void setStatus(String status) {
            this.status = status;
        }

        private AGServiceDescription getDescription() {
            return description;
        }

        private void setDescription(AGServiceDescription description) {
            this.description = description;
        }

        private ServiceManagerInterface getServiceManager() {
            return serviceManager;
        }

        private void setServiceManager(ServiceManagerInterface serviceManager) {
            this.serviceManager = serviceManager;
        }
    }

    private class NodeManagementTableModel implements TreeTableModel {

        private EventListenerList listenerList;

        private NodeManagementTableModel() {
            this.listenerList = new EventListenerList();
        }

        /**
         *
         * @see org.jdesktop.swingx.treetable.TreeTableModel#getColumnClass(int)
         */
        public Class< ? > getColumnClass(int arg0) {
            return String.class;
        }

        /**
         *
         * @see org.jdesktop.swingx.treetable.TreeTableModel#getColumnCount()
         */
        public int getColumnCount() {
            return 2;
        }

        /**
         *
         * @see org.jdesktop.swingx.treetable.TreeTableModel#getColumnName(int)
         */
        public String getColumnName(int arg0) {
            switch (arg0) {
            case 0:
                return NAME;
            case 1:
                return STATUS;
            default:
                return "";
            }
        }

        /**
         *
         * @see org.jdesktop.swingx.treetable.TreeTableModel#
         *     getHierarchicalColumn()
         */
        public int getHierarchicalColumn() {
            return 0;
        }

        /**
         *
         * @see org.jdesktop.swingx.treetable.TreeTableModel#getValueAt(
         *     java.lang.Object, int)
         */
        public Object getValueAt(Object arg0, int arg1) {
            // System.err.println("getValueAt called " + arg0+" " +arg1);
            switch (arg1) {
            case 0:
                return ((ListItem) arg0).getName();
            case 1:
                return ((ListItem) arg0).getStatus();
            default:
                return null;
            }
        }

        /**
         *
         * @see org.jdesktop.swingx.treetable.TreeTableModel#isCellEditable(
         *     java.lang.Object, int)
         */
        public boolean isCellEditable(Object arg0, int arg1) {
            return false;
        }

        /**
         *
         * @see org.jdesktop.swingx.treetable.TreeTableModel#setValueAt(
         *     java.lang.Object, java.lang.Object, int)
         */
        public void setValueAt(Object arg0, Object arg1, int arg2) {
            // Does Nothing
        }

        /**
         *
         * @see javax.swing.tree.TreeModel#addTreeModelListener(
         *     javax.swing.event.TreeModelListener)
         */
        public void addTreeModelListener(TreeModelListener l) {
            listenerList.add(TreeModelListener.class, l);
        }

        /**
         *
         * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
         */
        public Object getChild(Object parent, int index) {
            if (parent.equals(list.firstElement())) {
                for (int i = 1, j = 0; i < list.size(); i++) {
                    if (list.get(i).isNode()) {
                        if (index == j) {
                            System.err.println("return child "
                                    + list.get(i).toString());
                            return list.get(i);
                        }
                        j++;
                    }
                }
            }
            if (parent instanceof ListItem) {
                for (int i = 0; i < index - 1; i++) {
                    if (list.get(list.indexOf(parent) + (i + 1)).isNode()) {
                        return null;
                    }
                }
                return list.get(list.indexOf(parent) + index + 1);
            }
            return null;
        }

        /**
         *
         * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
         */
        public int getChildCount(Object parent) {
            if (((ListItem) parent).equals(list.firstElement())) {
                System.err.println("is root");
                int count = 0;
                for (int i = 1; i < list.size(); i++) {
                    if (list.get(i).isNode()) {
                        count++;
                    }
                }
                // System.err.println("count " + count);
                return count;
            }
            for (int i = list.indexOf(parent) + 1; i < list.size(); i++) {
                if (list.get(i).isNode()) {
                    return i - list.indexOf(parent) - 1;
                }
            }
            return list.size() - list.indexOf(parent) - 1;
        }

        /**
         *
         * @see javax.swing.tree.TreeModel#getIndexOfChild(
         *     java.lang.Object, java.lang.Object)
         */
        public int getIndexOfChild(Object parent, Object child) {
            for (int i = list.indexOf(parent) + 1, j = 0; i < list.size();
                    i++, j++) {
                if (list.get(i).equals(child)) {
                    return j;
                }
            }
            return 0;
        }

        /**
         *
         * @see javax.swing.tree.TreeModel#getRoot()
         */
        public Object getRoot() {
            return list.firstElement();
        }

        /**
         *
         * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
         */
        public boolean isLeaf(Object node) {
            return !((ListItem) node).isNode();
        }

        /**
         *
         * @see javax.swing.tree.TreeModel#removeTreeModelListener(
         *     javax.swing.event.TreeModelListener)
         */
        public void removeTreeModelListener(TreeModelListener l) {
            listenerList.remove(TreeModelListener.class, l);

        }

        /**
         *
         * @see javax.swing.tree.TreeModel#valueForPathChanged(
         *     javax.swing.tree.TreePath, java.lang.Object)
         */
        public void valueForPathChanged(TreePath path, Object newValue) {
            // Does Nothing
        }

    }

    private class StoreConfigDialog extends JDialog implements ActionListener,
            ListSelectionListener, KeyListener {
        private static final long serialVersionUID = 1L;
        private static final String OK = "OK";
        private static final String CANCEL = "Cancel";
        private static final String LABEL = "Configuration name";

        private HashMap<String, String> configs;
        private JButton ok;
        private JButton cancel;
        private JCheckBox defaultBox;
        private JTextField input;
        private SaveItem value;

        private StoreConfigDialog(JFrame parent, String title,
                String[] choices) {
            super(parent, title, true);
            this.setLayout(new BorderLayout(20, 20));

            value = new SaveItem();
            Vector<String> userConfigs = new Vector<String>();
            configs = new HashMap<String, String>();
            for (int i = 0; i < choices.length; i++) {
                String displayName = choices[i];
                configs.put(displayName, choices[i]);
                userConfigs.add(displayName);
            }

            JPanel buttons = new JPanel(new GridLayout(1, 2, 20, 20));
            GridBagLayout gridbag = new GridBagLayout();
            JPanel main = new JPanel(gridbag);
            main.getInsets().set(10, 10, 10, 10);

            ok = new JButton(OK);
            ok.setEnabled(false);
            ok.addActionListener(this);
            cancel = new JButton(CANCEL);
            cancel.addActionListener(this);
            buttons.add(ok);
            buttons.add(cancel);
            this.add(buttons, BorderLayout.SOUTH);

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.insets = new Insets(5, 5, 5, 5);
            c.weightx = 1;
            c.gridwidth = GridBagConstraints.REMAINDER;

            JLabel label = new JLabel(LABEL);
            gridbag.setConstraints(label, c);
            main.add(label);

            JList list = new JList(userConfigs);
            list.setBorder(
                    BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.addListSelectionListener(this);
            c.gridheight = 5;
            c.weighty = 1;
            gridbag.setConstraints(list, c);
            main.add(list);

            input = new JTextField();
            input.setBorder(BorderFactory
                    .createBevelBorder(BevelBorder.LOWERED));
            input.addActionListener(this);
            input.addKeyListener(this);
            c.gridheight = 1;
            c.weighty = 0;
            gridbag.setConstraints(input, c);
            main.add(input);

            defaultBox = new JCheckBox("Set as default");
            gridbag.setConstraints(defaultBox, c);
            main.add(defaultBox);

            main.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 10, 10, 10),
                    BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));

            this.add(main, BorderLayout.CENTER);
            this.getInsets().set(30, 30, 30, 30);
            this.setSize(300, 400);
            setLocationRelativeTo(parent);

        }

        private SaveItem getValue() {
            return value;
        }

        /**
         *
         * @see java.awt.event.ActionListener#actionPerformed(
         *     java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JTextField) {
                JTextField text = (JTextField) e.getSource();
                if (text.getText().length() > 0) {
                    ok.setEnabled(true);
                } else {
                    ok.setEnabled(false);
                }
            }
            if (e.getSource() instanceof JButton) {
                if (((JButton) e.getSource()).getText() == OK) {
                    value.name = input.getText();
                    if (configs.get(value.name) != null) {
                        int choice = JOptionPane.showConfirmDialog(this,
                                "Overwrite " + value.name + "?", "Confirm",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                        if (choice == JOptionPane.CANCEL_OPTION) {
                            return;
                        }
                        value.name = configs.get(value.name);
                    }
                    value.defaultchecked = defaultBox.isSelected();
                } else if (((JButton) e.getSource()).getText() == CANCEL) {
                    value = null;
                }
                this.dispose();
            }

        }

        /**
         *
         * @see javax.swing.event.ListSelectionListener#valueChanged(
         *     javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e) {
            ok.setEnabled(true);
            JList source = (JList) e.getSource();
            String curr = configs.get(
                    source.getSelectedValue());
            if (curr != null) {
                input.setText(curr);
            }
        }

        /**
         *
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        public void keyPressed(KeyEvent e) {
            // Does Nothing
        }

        /**
         *
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
         */
        public void keyReleased(KeyEvent e) {
            // Does Nothing
        }

        /**
         *
         * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
         */
        public void keyTyped(KeyEvent e) {
            JTextField text = (JTextField) e.getSource();
            if (text.getText().length() > 0) {
                ok.setEnabled(true);
            } else {
                ok.setEnabled(false);
            }
        }

    }

    private class ServiceChoiceDialog extends JDialog implements
            ActionListener, KeyListener {

        private static final long serialVersionUID = 1L;
        private static final String OK = "OK";
        private static final String CANCEL = "Cancel";
        private JComboBox comboBox;
        private JButton ok;
        private JButton cancel;
        private String comboValue;

        private ServiceChoiceDialog(JFrame parent, String title,
                Vector<String> choices) {
            super(parent, title, true);
            this.getInsets().set(10, 10, 10, 10);
            comboBox = new JComboBox(choices);
            comboBox.setEditable(true);
            comboBox.getInsets().set(10, 10, 10, 10);
            comboBox.setBorder(new EmptyBorder(10, 10, 0, 10));
            comboBox.addKeyListener(this);
            setSize(450, 130);
            setLocationRelativeTo(parent);
            ((java.awt.Frame) this.getOwner())
                    .setIconImage(new ImageIcon(this.getClass().getResource(
                            "/org/jdesktop/swingx/icon/ag.png")).getImage());
            this.setLayout(new BorderLayout(10, 10));
            this.add(comboBox, BorderLayout.NORTH);
            JPanel buttons = new JPanel();
            ok = new JButton(OK);
            ok.addActionListener(this);
            cancel = new JButton(CANCEL);
            cancel.addActionListener(this);
            buttons.add(ok);
            buttons.add(cancel);
            this.add(buttons, BorderLayout.SOUTH);
            // this.pack();

        }

        private String getComboValue() {
            return comboValue;
        }

        /**
         *
         * @see java.awt.event.ActionListener#actionPerformed(
         *     java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            if (((JButton) e.getSource()).getText() == OK) {
                comboValue = (String) comboBox.getSelectedItem();
                this.dispose();
            }
            if (((JButton) e.getSource()).getText() == CANCEL) {
                comboValue = null;
                this.dispose();
            }
        }

        /**
         *
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        public void keyPressed(KeyEvent e) {
            // Does Nothing

        }

        /**
         *
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
         */
        public void keyReleased(KeyEvent e) {
            System.err.println("Key " + e.getKeyCode() + " compare "
                    + KeyEvent.VK_ENTER);
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                comboValue = (String) comboBox.getSelectedItem();
                this.dispose();
            }
        }

        /**
         *
         * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
         */
        public void keyTyped(KeyEvent e) {
            // Does Nothing
        }

    }

    private class ConfigureDialog extends JDialog implements ActionListener {

        private AGParameter[] params = null;

        private boolean isOK = false;

        private ConfigureDialog(JFrame parent, String title,
                AGParameter[] params) {
            super(parent, title, true);
            this.params = params;
            JPanel content = new JPanel();
            content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            content.setLayout(new GridLayout(params.length + 1, 2, 5, 5));
            for (int i = 0; i < params.length; i++) {
                if (params[i].getVisualComponent() != null) {
                    content.add(new JLabel(params[i].getName() + ":"));
                    content.add(params[i].getVisualComponent());
                }
            }
            JButton ok = new JButton("OK");
            JButton cancel = new JButton("Cancel");
            content.add(ok);
            content.add(cancel);
            add(content);
            ok.addActionListener(this);
            cancel.addActionListener(this);
            pack();
        }

        /**
         *
         * @see java.awt.event.ActionListener#actionPerformed(
         *     java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            String button = e.getActionCommand();
            if (button.equals("OK")) {
                for (int i = 0; i < params.length; i++) {
                    params[i].setValueFromComponent();
                }
                isOK = true;
                dispose();
            } else if (button.equals("Cancel")) {
                dispose();
            }
        }

        private AGParameter[] getParams() {
            return params;
        }

        private boolean isOK() {
            return isOK;
        }
    }

    /**
     *
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
        // Do Nothing
    }

    /**
     *
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
        // Do Nothing
    }

    /**
     *
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
        // Do Nothing
    }

    /**
     *
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        if (path != null) {
            tree.getTreeSelectionModel().setSelectionPath(path);
        }
    }

    /**
     *
     * @see java.awt.event.MouseListener#mouseReleased(
     *     java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int[] selection = tree.getSelectedRows();
            if (selection == null || selection.length != 1) {
                System.err.println("no service selected or too many!");
                return;
            }

            ListItem item = list.get(selection[0] + 1);
            if (item.isNode()) {
                managerMenu.show(tree, e.getX(), e.getY());
            } else {
                serviceMenu.show(tree, e.getX(), e.getY());
            }
        }
    }

}
