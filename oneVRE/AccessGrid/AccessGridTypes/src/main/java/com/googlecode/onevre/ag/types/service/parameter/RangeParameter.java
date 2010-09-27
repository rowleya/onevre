/*
 * @(#)RangeParameter.java
 * Created: 06-Nov-2006
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

package com.googlecode.onevre.ag.types.service.parameter;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * An AG3 RangeParameter
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class RangeParameter extends AGParameter implements ChangeListener {

    private static final int TEXT_HEIGHT = 20;

    private static final int TEXT_WIDTH = 20;

    // The default value
    private int value = 0;

    // The maximum value
    private int high = 0;

    // The minimum value
    private int low = 0;

    // The panel containing the range components
    private JPanel component = new JPanel();

    // The slider
    private JSlider slider = new JSlider();

    // The text value
    private JTextField textValue = new JTextField("");

    /**
     * Creates a new RangeParameter
     * @param name The name of the parameter
     * @param value The default value
     * @param low The minimum value
     * @param high The maximum value
     */
    public RangeParameter(String name, int value, int low, int high) {
        super(name, "RangeParameter", INT_TYPE);
        addField("low", INT_TYPE);
        addField("high", INT_TYPE);
        this.value = value;
        this.low = low;
        this.high = high;
        createComponent();
        slider.setMinimum(low);
        slider.setMaximum(high);
        slider.setValue(value);
        textValue.setText(String.valueOf(value));
    }

    /**
     * Creates a new RangeParameter
     *
     */
    public RangeParameter() {
        super("", "RangeParameter", INT_TYPE);
        addField("low", INT_TYPE);
        addField("high", INT_TYPE);
        createComponent();
    }

    private void createComponent() {
        component.setLayout(new BoxLayout(component, BoxLayout.X_AXIS));
        component.add(slider);
        component.add(textValue);
        slider.addChangeListener(this);
        textValue.setMaximumSize(new Dimension(TEXT_WIDTH, TEXT_HEIGHT));
        textValue.setEditable(false);
    }

    /**
     * Gets the value
     * @return The value
     */
    public Object getValue() {
        return new Integer(value);
    }

    /**
     * Gets the minimum value
     * @return The minimum value
     */
    public int getLow() {
        return low;
    }

    /**
     * Gets the maximum value
     * @return The maximum value
     */
    public int getHigh() {
        return high;
    }

    /**
     * Sets the value
     * @param value The new value
     */
    public void setValue(Object value) {
        if (value instanceof Integer) {
            setValue(((Integer) value).intValue());
        } else if (value instanceof String) {
            setValue(Integer.parseInt((String) value));
        }
    }

    /**
     * Sets the value
     * @param value The value
     */
    public void setValue(int value) {
        this.value = value;
        slider.setValue(value);
        textValue.setText(String.valueOf(value));
    }

    /**
     * Sets the high value
     * @param high The high value
     */
    public void setHigh(int high) {
        this.high = high;
        slider.setMaximum(high);
        slider.setValue(value);
    }

    /**
     * Sets the low value
     * @param low The low value
     */
    public void setLow(int low) {
        this.low = low;
        slider.setMinimum(low);
        slider.setValue(value);
    }

    /**
     * Gets the component used to modify the parameter
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.types.agservice.AGParameter#getVisualComponent()}</dd></dl>
     * @return The component, or null if not modifiable
     */
    public JComponent getVisualComponent() {
        slider.setValue(value);
        return component;
    }

    /**
     * Sets the value of the parameter to the value of the visual component
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.types.agservice.AGParameter#setValueFromComponent()}</dd></dl>
     */
    public void setValueFromComponent() {
        this.value = slider.getValue();
    }

    /**
     * Invoked when the target of the listener has changed its state.
     * <dl><dt><b>overrides:</b></dt><dd>{@link javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)}</dd></dl>
     * @param e a ChangeEvent object
     */
    public void stateChanged(ChangeEvent e) {
        textValue.setText(String.valueOf(slider.getValue()));
    }
}
