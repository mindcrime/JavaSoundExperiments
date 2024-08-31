package org.fogbeam.example.jsound.v2;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CyclicBarrier;

import javax.annotation.Resource;
import javax.sound.sampled.AudioFormat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MicCaptureApplication implements CommandLineRunner
{
	
	@Resource
	private ApplicationContext context;
	
	
	public static void main( String[] args ) throws Exception
	{
		SpringApplication.run( MicCaptureApplication.class, args );
	}
	
	@Override
	public void run( String... args ) throws Exception
	{
		// capture from our microphone, with persistence to disk in discrete chunks
		
		
		AudioFormat format = new AudioFormat( 44100.0f, 16, 1, true, false );
		
		Deque<Byte> q = new ArrayDeque<Byte>();
		CyclicBarrier barrier = new CyclicBarrier( 2 );		
		Long totalFramesRead = 0L;
		
		MicReaderRunnable micReader = new MicReaderRunnable( q, barrier, format, totalFramesRead );
		context.getAutowireCapableBeanFactory().autowireBean( micReader );
		
		
		
		AudioFileWriterRunnable audioFileWriter = new AudioFileWriterRunnable( q, barrier, format, totalFramesRead );
		context.getAutowireCapableBeanFactory().autowireBean( audioFileWriter );
		
		ThreadGroup audioThreads = new ThreadGroup( "AudioThreads" );
		
		Thread micReaderThread = new Thread( audioThreads, micReader, "MicReaderThread" );
		Thread fileWriterThread = new Thread( audioThreads, audioFileWriter, "AudioFileWriterThread" );
		
		micReader.setReady( true );
		audioFileWriter.setReady( true );
		
		micReaderThread.start();
		fileWriterThread.start();
		
		audioThreads.interrupt();
		
		// run until Ctrl+C'd. Need to add the "Stoppable" stuff here to allow
		// for clean shutdown
		
		micReaderThread.join();
		fileWriterThread.join();
		
		System.out.println( "done" );
		
	}
	
}
