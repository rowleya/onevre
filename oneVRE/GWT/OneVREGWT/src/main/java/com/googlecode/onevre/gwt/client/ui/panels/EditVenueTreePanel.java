package com.googlecode.onevre.gwt.client.ui.panels;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import com.googlecode.onevre.gwt.client.ag.types.ClientProfile;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;
import com.googlecode.onevre.gwt.client.ag.types.VenueTreeItem;
import com.googlecode.onevre.gwt.client.xmlrpc.GetConnections;

import com.googlecode.onevre.gwt.common.client.MessageResponseHandler;
import com.googlecode.onevre.gwt.common.client.ModalPopup;

public class EditVenueTreePanel extends ModalPopup<DockPanel>
    implements ClickHandler, OpenHandler<TreeItem> , VenueTreePanel {

    private Tree venueTree = new Tree();

    private VenueTreeItem currentItem = null;

    private PushButton ok = new PushButton("OK", this);

    private ClientProfile clientProfile = null;

    private GetConnections getConnections = null;

    private MessageResponseHandler handler = null;

    private VenueServerType venueServerType = null;

    private boolean changed = false;

    private boolean init = true;

    public boolean hasChanged() {
        return changed;
    }

    public ClientProfile getClientProfile() {
        return clientProfile;
    }

    public Panel getHeading(VenueServerType vs) {

        HorizontalPanel imgPanel = new HorizontalPanel();
        Image image = vs.getImage();
        image.setHeight("30px");
        imgPanel.add(image);
        imgPanel.setCellHorizontalAlignment(image, HorizontalPanel.ALIGN_CENTER);
        imgPanel.setCellVerticalAlignment(image, HorizontalPanel.ALIGN_MIDDLE);
        imgPanel.setWidth("75px");
        imgPanel.setHeight("40px");
        HorizontalPanel lblPanel = new HorizontalPanel();
        Label title = new Label("Venue Selection for " + vs.getDescription());
        title.setStyleName("portlet-title");
        lblPanel.add(title);
        FlexTable pnl = new FlexTable();
        pnl.setStyleName("gwt-StackPanelItem");
        pnl.setWidth("100%");
        pnl.setWidget(0, 0, imgPanel);
        pnl.getFlexCellFormatter().setWidth(0, 0, "80px");
        pnl.setWidget(0, 1, lblPanel);
        return pnl;
    }

    public void getVenueTree(VenueServerType vs) {
        GetConnections getConnections = new GetConnections(this);
        getConnections.getConnections(vs.getDefaultVenue());
    }


    public EditVenueTreePanel(VenueServerType vs) {
        super(new DockPanel());
        this.venueServerType = vs;
        getConnections = new GetConnections(this);
        getConnections.getConnections(vs.toUrl());
        init = false;
        DockPanel panel = getWidget();
        panel.setStyleName("gwt-StackPanel");
        panel.add(getHeading(vs), DockPanel.NORTH);
        Image img = new Image(GWT.getModuleBaseURL() + "images/venueTree.png");
        panel.add(img, DockPanel.WEST);

        panel.setCellVerticalAlignment(img, DockPanel.ALIGN_MIDDLE);
        HorizontalPanel treepanel = new HorizontalPanel();
        treepanel.add(venueTree);
        venueTree.addOpenHandler(this);
        panel.add(treepanel, DockPanel.CENTER);
        panel.setCellHorizontalAlignment(treepanel, HorizontalPanel.ALIGN_CENTER);
        panel.setCellVerticalAlignment(treepanel, HorizontalPanel.ALIGN_MIDDLE);

        HorizontalPanel buttonPanel = new HorizontalPanel();
        ok.setWidth("3em");

        buttonPanel.add(ok);
        buttonPanel.setCellHorizontalAlignment(ok, HorizontalPanel.ALIGN_CENTER);
        buttonPanel.setCellVerticalAlignment(ok, HorizontalPanel.ALIGN_MIDDLE);

        panel.add(buttonPanel, DockPanel.SOUTH);
        panel.setCellHorizontalAlignment(buttonPanel, HorizontalPanel.ALIGN_RIGHT);
        panel.setWidth("100%");
    }

    public void onClick(ClickEvent event) {
        hide();
    }

    public String createVTIPanel(VenueTreeItem vti) {
        return vti.getName();
    }

    public void insertVTI(TreeItem root, Vector<VenueTreeItem> branches) {
        for (VenueTreeItem vti : branches) {
            TreeItem ti = new TreeItem(createVTIPanel(vti));

            root.addItem(ti);
            if (vti.hasConnections()) {
                insertVTI(ti, vti.getConnections());
            }
        }
    }


    public void updateTree(Vector<VenueTreeItem> venueTreeData) {
        for (VenueTreeItem vti : venueTreeData) {
            if (currentItem == null) {
                venueTree.addItem(vti);
            } else {
                currentItem.removeDummy();
                currentItem.addItem(vti);
            }
        }
    }

    public void onOpen(OpenEvent<TreeItem> event) {
        currentItem = (VenueTreeItem) event.getTarget();
        if (currentItem.hasDummy()) {
            getConnections.getConnections(currentItem.getVenueUrl());
        }
    }

    public VenueServerType getVenueServerType() {
        return venueServerType;
    }

}
