/*
 * @(#)SharedPresentationUI.java
 * Created: 22-Mar-2008
 * Version: 1.0
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

package com.googlecode.onevre.web.sharedPresentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import javax.swing.JPanel;
import javax.swing.JFrame;

/**
 * Shared Presentation user interface
 * @author Tobias M Schiebeck
 * @version 1.0
 */

public class SharedPresentationUI extends JFrame implements ActionListener,  KeyListener{

    private static final long serialVersionUID = 1L;
    private static final String TITLE = "Shared Presentation";
    private static final String PREVIOUS = "Previous Slide";
    private static final String NEXT = "Next Slide";
    private static final String GO = "Go To Slide";
    private static final String SLIDE = "Slide No";
    private static final String PRESENTATION = "Presentation: ";
    private static final String MASTER = "Presentation Master";

    private int nSlides=0;
    private int currentSlide=1;

    private JButton prev;
    private JButton next;
    private JButton gotoSlide;
    private JCheckBox masterBox;
    private JTextField slideNumber;
    private JLabel presFile;
    private boolean isMaster = false;
    private SharedPresentation pres=null;

    private SharedPresInterface presentationHandle = null;

    /**
     * Creates a new SharedPresInterfaceUI
     * @param presInterface The shared presentation that the UI talks to
     */
    public SharedPresentationUI(SharedPresInterface presInterface) {
        this.presentationHandle = presInterface;
        this.pres = pres;
        setTitle(TITLE);
        setPreferredSize(new Dimension(350, 200));
        setBackground(Color.WHITE);
        setVisible(true);
        setLocationRelativeTo(null);
        toFront();
        this.setLayout(new BorderLayout(20, 10));
        JPanel buttons = new JPanel(new GridLayout(1, 2, 20, 20));
        GridLayout grid = new GridLayout(0,1, 20, 0);
        JPanel main = new JPanel(grid);
        main.getInsets().set(10, 10, 0, 10);

        prev = new JButton(PREVIOUS);
        prev.addActionListener(this);
        next = new JButton(NEXT);
        next.addActionListener(this);
        masterBox = new JCheckBox(MASTER);
        masterBox.addActionListener(this);
        buttons.add(prev);
        buttons.add(next);
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        this.add(buttons, BorderLayout.SOUTH);

        presFile= new JLabel (PRESENTATION);

        FlowLayout flow1=new FlowLayout(FlowLayout.CENTER,20,5);
        JPanel presentationSelect=new JPanel(flow1);
        JLabel presLabel=new JLabel (PRESENTATION);
        presFile= new JLabel ("");
        presentationSelect.add(presLabel);
        presentationSelect.add(presFile);


        FlowLayout flow2=new FlowLayout(FlowLayout.CENTER,20,5);
        JPanel slideSelect=new JPanel(flow2);
        JLabel slideLabel = new JLabel(SLIDE);
        slideSelect.add(slideLabel);
        slideNumber = new JTextField();
        slideNumber.setBorder(BorderFactory
                .createBevelBorder(BevelBorder.LOWERED));
        slideNumber.addActionListener(this);
        slideNumber.addKeyListener(this);
        slideNumber.setPreferredSize(new Dimension(50,20));
        slideSelect.add(slideNumber);
        gotoSlide = new JButton(GO);
        gotoSlide.setEnabled(false);
        gotoSlide.addActionListener(this);
        slideSelect.add(gotoSlide);

        FlowLayout flow3=new FlowLayout(FlowLayout.CENTER,20,5);
        JPanel presentationMaster=new JPanel(flow3);
        presentationMaster.add(masterBox);

        main.add(presentationSelect);
        main.add(slideSelect);
        main.add(presentationMaster);
        main.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 0, 10),
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));

        this.add(main, BorderLayout.CENTER);
        this.getInsets().set(30, 30, 30, 30);
        doLayout();
        pack();
        updateUI(currentSlide);
    }

    /**
     *
     * @see java.awt.event.ActionListener#actionPerformed(
     *     java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            if (((JButton) e.getSource()).getText()==GO) {
                currentSlide = Integer.parseInt(slideNumber.getText());
                currentSlide =  presentationHandle.GotoSlide(currentSlide);
                pres.sendEvent("goto");
            } else if (((JButton) e.getSource()).getText() == PREVIOUS) {
            	currentSlide = presentationHandle.PreviousSlide();
            	pres.sendEvent("prev");
            } else if (((JButton) e.getSource()).getText() == NEXT) {
            	currentSlide = presentationHandle.NextSlide();
            	pres.sendEvent("prev");
           }
        }
        if (e.getSource() instanceof JCheckBox){
        	if (((JCheckBox) e.getSource()).getSelectedObjects()!=null) {
                isMaster=true;
                pres.sendEvent("master");
        	} else {
        		isMaster=false;
        	}
        }

        updateUI(currentSlide);
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
    	// Does Nothing
    }

	/**
	 * Sets the number of slides
	 * @param nSlides number of slides
	 */
	public void setNoSlides(int nSlides) {
		this.nSlides = nSlides;
	}

	/**
	 * Sets the current Presentation
	 * @param presentation the current presentation
	 */
	public void setPresentation(String presentation) {
		setTitle(TITLE + ": " + presentation);
		presFile.setText(presentation);
		updateUI(currentSlide);
	}

	/**
	 * updates the user interface
	 * @param slide the current slide
	 */
	public void updateUI (int slide) {

		slideNumber.setText(new Integer(slide).toString());
		slideNumber.setEditable(isMaster);
		gotoSlide.setEnabled(isMaster);
		prev.setEnabled(isMaster && true);
		next.setEnabled(isMaster && true);
		if (slide==nSlides){
        	next.setEnabled(false);
        }
        if (currentSlide==1){
        	prev.setEnabled(false);
        }
	}

	public void setSharedPresentation(SharedPresentation sharedPresentation) {
		this.pres=sharedPresentation;

	}



}
