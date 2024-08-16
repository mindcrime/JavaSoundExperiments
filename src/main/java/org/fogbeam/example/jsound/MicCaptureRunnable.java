package org.fogbeam.example.jsound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoField;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

public class MicCaptureRunnable implements Runnable
{
	private Object lock;
	private boolean ready = false;
	private TargetDataLine line;
	private AudioFormat format;
	private long secondsToCapture;
	
	private MicCaptureRunnable()
	{}
	
	MicCaptureRunnable( Object lock, TargetDataLine line, AudioFormat format, long secondsToCapture )
	{
		this.lock = lock;
		this.line = line;
		this.format = format;
		this.secondsToCapture = secondsToCapture;
	}
	
	@Override
	public void run()
	{
		String fileName = Thread.currentThread().getName() + ".wav";
		
		File outFile = new File( fileName );
		if( outFile.exists() )
		{
			System.out.println( "deleting old file" );
			outFile.delete();
		}
		
		outFile = new File( fileName );		
		
		try
		{
			Thread.sleep( Long.MAX_VALUE );
		}
		catch( InterruptedException e )
		{
			if( ready )
			{
				// just continue
			}
			else
			{
				// we were interrupted too soon, treat this as an error
				throw new RuntimeException( e );
			}
		}
	
		System.out.println( Thread.currentThread().getName() + " started at " + Instant.now().get( ChronoField.MICRO_OF_SECOND) );

		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int numBytesRead;
		
		System.out.println( "bufferSize: " + line.getBufferSize() );
		
		byte[] data = new byte[line.getBufferSize()];
		
		// Begin audio capture
		int bytesPerFrame = format.getFrameSize();
		int totalFramesRead = 0;
		
		System.out.println( "Opening line" );
		
		try
		{
			line.open( format );
		
			if( !line.isOpen() )
			{
				System.out.println( "Line is NOT open!!!!!" );
			}
			else
			{
				System.out.println( "Line IS open!!" );
			}
		}
		catch( Exception e )
		{
			throw new RuntimeException( e );
		}
		
		System.out.println( "Starting line!" );
		line.start();
		
		// capture data until ... ? 
		long startMillis = System.currentTimeMillis();
		while( true )
		{
			
			// System.out.println( Thread.currentThread().getName() + " Capturing data!" );
			
			System.out.println( Thread.currentThread().getName() + " : " + "Available: " + line.available() );
			// System.out.println( Thread.currentThread().getName() + " : " + "data.length: " + data.length );
			
			// Read the next chunk of data from the TargetDataLine.
			numBytesRead = line.read( data, 0, data.length );
			
			System.out.println( Thread.currentThread().getName() + " : " + "numBytesRead " + numBytesRead );
			
			// Save this chunk of data.
			out.write( data, 0, numBytesRead );
			
			
			int framesRead = numBytesRead / bytesPerFrame;
			totalFramesRead += framesRead;
			
			
			long currentMillis = System.currentTimeMillis();
			if( ((currentMillis - startMillis) / 1000) > this.secondsToCapture )
			{
				break;
			}
		}	
		
		try
		{
			Thread.sleep(  2000 );
		
			System.out.println( Thread.currentThread().getName() + " : " + "totalFramesRead: " + totalFramesRead );
		
			ByteArrayInputStream bais = new ByteArrayInputStream(out.toByteArray());
			AudioInputStream audioInputStream = new AudioInputStream(bais, format, totalFramesRead);
			AudioSystem.write( audioInputStream, Type.WAVE, outFile );
			
		}
		catch( Exception e )
		{
			throw new RuntimeException( e );
		}
	}

	public void setReady( boolean ready )
	{
		this.ready = ready;
	}
}
