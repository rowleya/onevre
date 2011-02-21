package com.googlecode.onevre.gwt.client.ui.panels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.ServerVenueManager;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ui.ButtonPanel;
import com.googlecode.onevre.gwt.client.ui.Icons;
import com.googlecode.onevre.gwt.client.ui.buttons.CloseButton;

public class VenuePanel extends FlexTable implements ClickHandler {

    private ClientPanel clientPanel;
    private TabPanel statePanel;
    private DataPanel dataPanel;
    private ServicePanel servicePanel;
    private ApplicationPanel applicationPanel;
    private JabberPanel jabberPanel;

    private ButtonPanel buttonPanel;

    public VenuePanel(VenueState state, ServerVenueManager serverVenueManager) {

        buttonPanel = new ButtonPanel("venue", ButtonPanel.RIGHT);
        buttonPanel.addButton(new CloseButton(state, serverVenueManager));
        this.setWidth("100%");
        this.setText(0, 0, state.getName());
        this.getFlexCellFormatter().setColSpan(0, 0, 2);
        this.getRowFormatter().setStyleName(0, "gwt-PushButton gwt-PushButton-up");
        this.setText(1, 0, state.getDescription());
        this.getFlexCellFormatter().setColSpan(1, 0, 3);
        this.addClickHandler(this);
        this.setWidget(0, 2, buttonPanel.getPanel());
        this.getFlexCellFormatter().setHorizontalAlignment(0, 2, HorizontalPanel.ALIGN_RIGHT);

        clientPanel = new ClientPanel(state.getClients());
        Application.getUserManager().setClientPanel(state, clientPanel);

        this.setWidget(2, 0 , clientPanel);

        statePanel = new TabPanel();
        statePanel.setWidth("100%");
        dataPanel = new DataPanel(state);
        Application.getDataManager().setDataPanel(state, dataPanel);
        servicePanel = new ServicePanel(state.getServices());
        applicationPanel = new ApplicationPanel(state.getApplications());
        jabberPanel = new JabberPanel(state.getUri());
        Application.getJabberManager().setJabberPanel(state, jabberPanel);

        HorizontalPanel dataTab = new HorizontalPanel();
        Image dataIcon = new Image(Icons.folderIcon);
        dataIcon.setHeight("16px");
        dataTab.add(dataIcon);
        dataTab.setCellVerticalAlignment(dataIcon, HorizontalPanel.ALIGN_MIDDLE);
        dataTab.add(new Label("Data"));
        statePanel.add(dataPanel, dataTab);
        statePanel.selectTab(0);

        HorizontalPanel servicesTab = new HorizontalPanel();
        Image serviceIcon = new Image(Icons.serviceIcon);
        serviceIcon.setHeight("16px");
        servicesTab.add(serviceIcon);
        servicesTab.setCellVerticalAlignment(serviceIcon, HorizontalPanel.ALIGN_MIDDLE);
        servicesTab.add(new Label("Services"));
        statePanel.add(servicePanel, servicesTab);

        HorizontalPanel applicationTab = new HorizontalPanel();
        Image appIcon = new Image(Icons.applicationIcon);
        appIcon.setHeight("16px");
        applicationTab.add(appIcon);
        applicationTab.setCellVerticalAlignment(appIcon, HorizontalPanel.ALIGN_MIDDLE);
        applicationTab.add(new Label("Applications"));
        statePanel.add(applicationPanel, applicationTab);

        HorizontalPanel jabberTab = new HorizontalPanel();
        Image jabberIcon = new Image(Icons.jabberIcon);
        jabberIcon.setHeight("16px");
        jabberTab.add(jabberIcon);
        jabberTab.add(new Label("Jabber"));
        statePanel.add(jabberPanel, jabberTab);

        this.setWidget(2, 1 , statePanel);
        this.getFlexCellFormatter().setColSpan(2, 1, 3);


    }

    public void onClick(ClickEvent event) {
        Cell cell = getCellForEvent(event);
        if (cell != null) {
            if (cell.getRowIndex() == 0) {
                if (cell.getCellIndex() != (getCellCount(0) - 1)) {
                    boolean vis = this.getRowFormatter().isVisible(1);
                    vis = !vis;
                    this.getRowFormatter().setVisible(1, vis);
                    this.getRowFormatter().setVisible(2, vis);
                }
            }
        }
    }
}
