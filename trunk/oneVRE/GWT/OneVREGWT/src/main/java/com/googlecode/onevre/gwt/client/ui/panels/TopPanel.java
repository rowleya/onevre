package com.googlecode.onevre.gwt.client.ui.panels;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;

import com.googlecode.onevre.gwt.client.ui.ButtonPanel;

import com.googlecode.onevre.gwt.client.ui.buttons.AudioButton;
import com.googlecode.onevre.gwt.client.ui.buttons.HelpButton;
import com.googlecode.onevre.gwt.client.ui.buttons.NetworkButton;
import com.googlecode.onevre.gwt.client.ui.buttons.NodeServiceButton;
import com.googlecode.onevre.gwt.client.ui.buttons.ProfileButton;
import com.googlecode.onevre.gwt.client.ui.buttons.TrustedServersButton;
import com.googlecode.onevre.gwt.client.ui.buttons.UploadApplicationButton;
import com.googlecode.onevre.gwt.client.ui.buttons.UploadBridgeButton;
import com.googlecode.onevre.gwt.client.ui.buttons.UploadServiceButton;
import com.googlecode.onevre.gwt.client.ui.buttons.VideoConsumerButton;
import com.googlecode.onevre.gwt.client.ui.buttons.VideoProducerButton;

public class TopPanel{

	DockPanel panel = new DockPanel();

	public TopPanel() {
		ButtonPanel leftButtons = new ButtonPanel("leftButtons", ButtonPanel.LEFT);
        leftButtons.addButton(new AudioButton());
        leftButtons.addButton(new VideoConsumerButton());
        leftButtons.addButton(new VideoProducerButton());
        leftButtons.addButton(new NodeServiceButton());
        leftButtons.addSpacer();
        leftButtons.addButton(new ProfileButton());
        leftButtons.addSpacer();
        leftButtons.addButton(new NetworkButton());
        leftButtons.addSpacer();
        leftButtons.addButton(new TrustedServersButton());

        ButtonPanel rightButtons = new ButtonPanel("rightButtons", ButtonPanel.RIGHT);
        rightButtons.addButton(new UploadServiceButton());
        rightButtons.addButton(new UploadApplicationButton());
        rightButtons.addButton(new UploadBridgeButton());
        rightButtons.addSpacer(8);
        rightButtons.addButton(new HelpButton());

        panel.add(leftButtons.getPanel(), DockPanel.WEST);
        panel.add(rightButtons.getPanel(),DockPanel.EAST);
        panel.setCellHorizontalAlignment(rightButtons.getPanel(),HorizontalPanel.ALIGN_RIGHT);
        panel.setWidth("100%");
	}

	public Panel getPanel(){
		return this.panel;
	}

}
