/*
 * Copyright (c) 2008, University of Manchester All rights reserved.
 * See LICENCE in root directory of source code for details of the license.
 */

package com.googlecode.onevre.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A print stream that writes to a file as well as the output stream given
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class PrintOutStream extends PrintStream {

    private OutputStream output = null;

    /**
     * Creates a new PrintOutStream
     * @param outputStream The output stream to write to
     * @param file The file to write to
     * @throws FileNotFoundException
     */
    public PrintOutStream(OutputStream outputStream, File file)
            throws FileNotFoundException {
        super(outputStream, true);
        output = new FileOutputStream(file, true);
    }

    /**
     * Creates a PrintOutStream
     * @param outputStream
     * @param output
     */
    public PrintOutStream(OutputStream outputStream, OutputStream output) {
        super(outputStream, true);
        this.output = output;
    }

    /**
     *
     * @see java.io.PrintStream#write(byte[], int, int)
     */
    public void write(byte[] buf, int off, int len) {
        super.write(buf, off, len);
        if (output != null) {
            try {
                output.write(buf, off, len);
            } catch (IOException e) {
                e.printStackTrace();
                output = null;
            }
        }
    }

    /**
     *
     * @see java.io.PrintStream#write(int)
     */
    public void write(int b) {
        super.write(b);
        if (output != null) {
            try {
                output.write(b);
            } catch (IOException e) {
                e.printStackTrace();
                output = null;
            }
        }
    }

}
