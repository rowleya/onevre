/**
 * Copyright (c) 2009, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the and the University of Manchester nor the names of
 *    its contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
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
 *
 */

package com.googlecode.onevre.gwt.common.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProgressPopup extends ModalPopup<VerticalPanel>
        implements ClickHandler {

    private boolean cancelled = false;

    private Image progressImg = new Image(GWT.getModuleBaseURL()+"images/popups/progress.gif");
    HorizontalPanel border = new HorizontalPanel();

    private Button cancel = new Button("Cancel");

    private long max = 0;
    private long min = 0;

    FlexTable layout = new FlexTable();

    private int width = progressImg.getWidth();
    private int height = progressImg.getHeight();

    public ProgressPopup(String msg, boolean cancellable) {
        super(new VerticalPanel());


        DOM.setStyleAttribute(getElement(), "width", "100%");
        DOM.setStyleAttribute(getElement(), "height", "100%");
        DOM.setStyleAttribute(getElement(), "backgroundColor", "#000");
        DOM.setStyleAttribute(getElement(), "opacity", "0.70");
        DOM.setStyleAttribute(getElement(), "filter",  " alpha(opacity=70)");

        VerticalPanel panel = getWidget();
        panel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        panel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
        panel.add(layout);
        Label message = new Label(msg);
        layout.setWidget(0, 0, message);
        layout.getFlexCellFormatter().setColSpan(0, 0, 3);
        layout.getFlexCellFormatter().setHorizontalAlignment(0, 0, HorizontalPanel.ALIGN_CENTER);
        border.setBorderWidth(1);
        border.setWidth((width+3)+"px");
        border.setHeight((height+3)+"px");
        layout.setWidget(1,1,border);
        layout.getFlexCellFormatter().setHorizontalAlignment(1, 1, HorizontalPanel.ALIGN_CENTER);
        layout.setText(2, 0, "0%");
        layout.setText(2, 2, "100%");
        layout.getFlexCellFormatter().setHorizontalAlignment(2, 1, HorizontalPanel.ALIGN_CENTER);

        progressImg.setSize("0px",height+"px");
        border.add(progressImg);
        if (cancellable) {
            panel.add(cancel);
            cancel.addClickHandler(this);
        }

        DOM.setStyleAttribute(border.getElement(), "marginBottom", "20px");
    }

    public void setMax(long max){
    	this.max = max;
    }

    public void setMin(long min){
    	this.min = min;
    }

    public void setValue(long value){
    	progressImg.setHeight(height+"px");
    	float outwidth = 0;
    	float minval = (float)value-min;
    	float maxval = (float)max-min;
    	if (value>min){
        	outwidth = (minval/maxval)*((float)width);
    	}
    	if (value>max){
    		outwidth = width;
    	}
    	progressImg.setWidth(((int)outwidth) + "px");
    	layout.setText(2, 1, value + " of " + max);
    }

    public void onClick(ClickEvent event) {
        cancelled = true;
        hide();
    }

    public boolean wasCancelled() {
        return cancelled;
    }
}
