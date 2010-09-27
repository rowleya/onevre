/*
 * Copyright (c) 2004-2007, Identity Theft 911, LLC.  All rights reserved.
 */
package net.sf.ufsc;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Paul Ferraro
 */
public class OutputStreamAdapter extends OutputStream
{
	private OutputStream output;
	private StreamClosedListener listener;
	
	public OutputStreamAdapter(OutputStream output, StreamClosedListener listener)
	{
		this.output = output;
		this.listener = listener;
	}
	
	/**
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public void close() throws IOException
	{
		this.output.close();
		
		this.listener.closed(new StreamClosedEvent(this.output));
	}

	/**
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException
	{
		this.output.flush();
	}

	/**
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		this.output.write(b, off, len);
	}

	/**
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException
	{
		this.output.write(b);
	}

	/**
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int arg0) throws IOException
	{
		this.output.write(arg0);
	}
}
