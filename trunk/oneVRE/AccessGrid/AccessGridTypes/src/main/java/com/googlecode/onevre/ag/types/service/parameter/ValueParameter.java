/*
 * @(#)ValueParameter.java
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

import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * An AG3 ValueParameter
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ValueParameter extends AGParameter {

    private String value = null;

    private JTextField component = new JTextField("");

    /**
     * Creates a new ValueParameter
     */
    public ValueParameter() {
        super("", "ValueParameter", STRING_TYPE);
    }

    /**
     * Creates a new ValueParameter
     * @param name The name of the parameter
     * @param value The value of the parameter
     */
    public ValueParameter(String name, String value) {
        super(name, "ValueParameter", STRING_TYPE);
        this.value = value;
        component.setText(value);
    }

    /**
     * Returns the value of the Parameter
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the value
     * @param value The new value
     */
    public void setValue(Object value) {
        this.value = (String) value;
        component.setText((String) value);
    }

    /**
     * Gets the component used to modify the parameter
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.types.agservice.AGParameter#getVisualComponent()}</dd></dl>
     * @return The component, or null if not modifiable
     */
    public JComponent getVisualComponent() {
        component.setText(value);
        return component;
    }

    /**
     * Sets the value of the parameter to the value of the visual component
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.types.agservice.AGParameter#setValueFromComponent()}</dd></dl>
     */
    public void setValueFromComponent() {
        this.value = component.getText();
    }

}
