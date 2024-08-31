package org.fogbeam.example.jsound.v2;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Deque;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;

public class AudioFileWriterRunnable implements Runnable
{
	private boolean ready = false;
	private Deque<Byte> q;
	private CyclicBarrier barrier;	
	private AudioFormat format;
	private Long totalFramesRead;
	
	
	@Value( "${output.file.slug}" )
	private String outputFileSlug;

	@Value( "${output.file.extension}" )
	private String outputFileExtension;

	
	private AudioFileWriterRunnable()
	{}
	
	public AudioFileWriterRunnable( Deque<Byte> q, CyclicBarrier barrier, AudioFormat format, Long totalFramesRead )
	{
		this.q = q;
		this.barrier = barrier;
		this.format = format;
		
		// this variable is shared between this Runnable and the MicReaderRunnable as a means
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
		
		while( true )
		{
			try
			{
				// handle the flush to disk.
				// first, copy the buffer to a temporary buffer and immediately notify the MicReaderRunnable thread
				// to continue, so we minimize lost data
				
				System.out.println( Thread.currentThread().getName() + " wait()'ing");
				barrier.await();
		
				System.out.println( Thread.currentThread().getName() + " thread operating");
				// copy the available data to another buffer
				// and then notify() the producer thread to resume work
				
				Byte[] elements = Arrays.copyOf( q.toArray(), q.size(), Byte[].class );
				q.clear();
				
				System.out.println( Thread.currentThread().getName() + " copied buffer" );
				
				System.out.println( Thread.currentThread().getName() + " notify()'ing producer thread" );
				barrier.await();
				
				// now flush the copied buffer to disk at our leisure...
				// so long as we can do it before the consumer notifies us again
				System.out.println( Thread.currentThread().getName() + " flushing buffer to disk" );		
				
				
				// once we have a copy of the buffer and have notified the other thread to continue, we can
				// flush our buffer to disk (more or less) at our leisure. So long as we finish before the
				// MicReaderRunnable thread calls us again. 
			
				// write the temporary buffer to disk...
				Instant nowInstant = Instant.now();
				
				OffsetDateTime now = nowInstant.atOffset( ZoneOffset.UTC );
				
				int year = now.get( ChronoField.YEAR );
				int month = now.get( ChronoField.MONTH_OF_YEAR );
				int day = now.get( ChronoField.DAY_OF_MONTH );
				int hour = now.get( ChronoField.HOUR_OF_DAY );
				int minute = now.get( ChronoField.MINUTE_OF_HOUR );
				int second = now.get(  ChronoField.SECOND_OF_MINUTE );
				
				DecimalFormat df = new DecimalFormat("00");
				
				// generate file name from current date/time and create outputstream
				FileOutputStream out = new FileOutputStream( 	df.format( year ) 
																+ "-" 
																+ df.format( month )
																+ "-"
																+ df.format( day )
																+ "-"
																+ df.format( hour )
																+ "-"
																+ df.format( minute )
																+ "-"
																+ df.format(  second )
																+ "-" 
																+ outputFileSlug 
																+ "." 
																+ outputFileExtension );			
				
				
				
				// temporary code, delete once the audio write stuff is fully set up
				/* 
				BufferedOutputStream buffOut = new BufferedOutputStream(  out );
				
				buffOut.write( ArrayUtils.toPrimitive( elements ) );
				buffOut.flush();
				buffOut.close();
				*/
				
				
				
				byte[] bytes = ArrayUtils.toPrimitive( elements );
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				
				int bytesPerFrame = format.getFrameSize();
				long totalFramesRead2 = bytes.length / bytesPerFrame;
				
				System.out.println( "At flush, totalFramesRead = " + totalFramesRead2 );
				AudioInputStream audioInputStream = new AudioInputStream(bais, format, totalFramesRead2);
				
				AudioSystem.write( audioInputStream, Type.WAVE, out );
				
				out.flush();
				out.close();
				
			}
			catch( InterruptedException | BrokenBarrierException e )
			{
				e.printStackTrace();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public void setReady( boolean ready )
	{
		this.ready = ready;
	}
}
