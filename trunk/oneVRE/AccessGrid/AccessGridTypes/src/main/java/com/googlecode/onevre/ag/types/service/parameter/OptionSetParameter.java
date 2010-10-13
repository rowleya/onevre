/*
 * @(#)OptionSetParameter.java
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

import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An AG3 OptionSetParameter
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class OptionSetParameter extends AGParameter {

    // The value
    private String value = null;

    // The set of possible values
    private Vector<String> options = new Vector<String>();

    // The component
    private JComboBox component = new JComboBox();

    /**
     * Creates a new OptionSetParameter
     * @param name The name of the option
     * @param value The default value
     * @param options The set of values to choose from
     */
    public OptionSetParameter(String name, String value, String[] options) {
        super(name, "OptionSetParameter", STRING_TYPE);
        addField("options", STRING_TYPE);
        this.value = value;
        for (int i = 0; i < options.length; i++) {
            this.options.add(options[i]);
            component.addItem(options[i]);
        }
        component.setEditable(false);
        component.setSelectedItem(value);
    }

    /**
     * Creates a new OptionSetParameter
     *
     */
    public OptionSetParameter() {
        super("", "OptionSetParameter", STRING_TYPE);
        addField("options", STRING_TYPE);
        this.value = "";
        component.setEditable(false);
    }

    /**
     * Gets the value
     * @return the value
     */
    @XmlElement
    public Object getValue() {
        return value;
    }

    /**
     * Gets the available options
     * @return The options
     */
    @XmlElement
    public String[] getOptions() {
        return options.toArray(new String[0]);
    }

    /**
     * Sets the value
     * @param value the value
     */
    public void setValue(Object value) {
        this.value = (String) value;
        component.setSelectedItem(value);
    }

    /**
     * Adds an option
     * @param option The option to add
     */
    public void setOptions(String option) {
        this.options.add(option);
        component.addItem(option);
        if ((value != null) && (value.equals(option))) {
            component.setSelectedItem(option);
        }
    }

    /**
     * Gets the component used to modify the parameter
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.types.agservice.AGParameter#getVisualComponent()}</dd></dl>
     * @return The component, or null if not modifiable
     */
    @XmlElement
    public JComponent getVisualComponent() {
        component.setSelectedItem(value);
        return component;
    }

    /**
     * Sets the value of the parameter to the value of the visual component
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.types.agservice.AGParameter#setValueFromComponent()}</dd></dl>
     */
    public void setValueFromComponent() {
        this.value = (String) component.getSelectedItem();
    }
}
