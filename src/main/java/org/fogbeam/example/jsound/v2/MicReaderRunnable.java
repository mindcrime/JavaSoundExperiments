package org.fogbeam.example.jsound.v2;

import java.util.Deque;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.springframework.beans.factory.annotation.Value;

import com.google.common.primitives.Bytes;

public class MicReaderRunnable implements Runnable
{
	private boolean ready = false;
	private Deque<Byte> q;
	private CyclicBarrier barrier;	
	private long secondsPerChunk = 15;
	private AudioFormat format;
	private Long totalFramesRead;
	
	
	@Value( "${audio.mixer.name:default}")
	private String mixerName;
	
	
	private MicReaderRunnable()
	{}
	
	public MicReaderRunnable( Deque<Byte> q, CyclicBarrier barrier, AudioFormat format, Long totalFramesRead )
	{
		this.q = q;
		this.barrier = barrier;
		this.format = format;
		
		// this variable is shared between this Runnable and the AudioFileWriterRunnable as a means
		// of communicating the bytesRead value when it's time to flush the accumulated audio
		// data to disk
		this.totalFramesRead = totalFramesRead;
	}
	
	@Override
	public void run()
	{
		
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
		
		System.out.println( "Thread started: " + Thread.currentThread().getName() );
		
		// temporary code
		try
		{
			Thread.sleep(  5000 );
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		
		try
		{

			Mixer mixer = getMixer( this.mixerName );
		
			mixer.open();
			
			TargetDataLine line = AudioSystem.getTargetDataLine( format, mixer.getMixerInfo() );
			System.out.println( "tdl: " + line.toString());
						
			int numBytesRead;
			
			System.out.println( "bufferSize: " + line.getBufferSize() );
			
			byte[] data = new byte[line.getBufferSize()];
			
			// Begin audio capture
			int bytesPerFrame = format.getFrameSize();
			
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
				
				System.out.println( Thread.currentThread().getName() + " Capturing data!" );
				
				System.out.println( Thread.currentThread().getName() + " : " + "Available: " + line.available() );
				
				// Read the next chunk of data from the TargetDataLine.
				numBytesRead = line.read( data, 0, data.length );
				
				System.out.println( Thread.currentThread().getName() + " : " + "numBytesRead " + numBytesRead );
				
				// Save this chunk of data.
				q.addAll( Bytes.asList( data ) );
				
				
				int framesRead = numBytesRead / bytesPerFrame;
				
				System.out.println( "framesRead: " + framesRead );
				
				this.totalFramesRead += framesRead;
				
				System.out.println( "totalFramesRead: " + this.totalFramesRead );
				
				
				long currentMillis = System.currentTimeMillis();
				
				System.out.println( "startMillis = " + startMillis );
				System.out.println( "currentMillis = " + currentMillis );
				
				if( ((currentMillis - startMillis) / 1000) > this.secondsPerChunk )
				{
					System.out.println( "Exceeded secondsPerChunk threshold, notifing audioFileWriter" );
					
					// notifies the fileWriter thread to flush the current buffer to disk
					barrier.await();
				
					// now wait until the filewriter thread notifies us to continue

					// System.out.println( "MicReaderRunnable thread waiting for notifiction from AudioFileWriterRunnable thread" );
					barrier.reset();
					barrier.await();
					
					System.out.println( "MicReaderRunnable resuming operations..." );
					
					startMillis = System.currentTimeMillis();
					this.totalFramesRead = 0L;
					System.out.println( "startMillis reset to: " + startMillis );
				
				}
			}	
			
		}
		catch( LineUnavailableException e )
		{
			e.printStackTrace();	
		}
		catch( BrokenBarrierException | InterruptedException e )
		{
			e.printStackTrace();
		}
		
		
		
		
		
	}
	
	private Mixer getMixer( String name )
	{
		Mixer mixerToReturn = null;
		
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		
		for( Mixer.Info mxInfo : mixerInfos )
		{		
			String mxName = mxInfo.getName();
			
			System.out.println( "mxName: " + mxName );
			System.out.println( "class: " + mxInfo.getClass().getCanonicalName() );
			
			if( mxInfo.getClass().getCanonicalName().toString().equalsIgnoreCase( "com.sun.media.sound.DirectAudioDeviceProvider.DirectAudioDeviceInfo" ) && mxName.matches( name + "\\s+.*" ))
			{
				System.out.println( "mixer: " + mxName + " matches for name: " + name );
				
				mixerToReturn = AudioSystem.getMixer( mxInfo );
				break;
			}
		}
		
		return mixerToReturn;
	}		
	
	public void setReady( boolean ready )
	{
		this.ready = ready;
	}	
	
}
