package org.fogbeam.example.jsound;

import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CaptureSimultaneous1Application implements CommandLineRunner
{
	
	private static final Map<String, String> mixerNames = new HashMap<String, String>();
	
	static
	{
		mixerNames.put( "left", "Audio" );
		mixerNames.put( "right", "Audio_1" );
	}

	@Override
	public void run( String... args ) throws Exception
	{
		SpringApplication.run(CaptureSimultaneous1Application.class, args );		
	}
	
	public static void main( String[] args ) throws Exception
	{		
		
		Mixer right = getMixer( "right" );
		right.open();
		System.out.println( "Got RIGHT mixer: " + right.getMixerInfo());
		
		Mixer left = getMixer( "left" );
		left.open();
		System.out.println( "Got LEFT mixer: " + left.getMixerInfo());
		
		
		AudioFormat format = new AudioFormat( 44100.0f, 16, 1, true, false );
		
		TargetDataLine tdlRight = AudioSystem.getTargetDataLine( format, right.getMixerInfo() );
		System.out.println( "tdlRight: " + tdlRight.toString());
		
		TargetDataLine tdlLeft = AudioSystem.getTargetDataLine( format, left.getMixerInfo() );
		System.out.println( "tdlLeft: " + tdlLeft.toString());
		
		ThreadGroup readerThreads = new ThreadGroup( "ReaderThreads" );
		
		Object lock = new Object();
		
		MicCaptureRunnable rightReader = new MicCaptureRunnable( lock, tdlRight, format, 7 );
		
		Thread rightReaderThread = new Thread(readerThreads, rightReader, "rightReaderThread");
		
		MicCaptureRunnable leftReader = new MicCaptureRunnable( lock, tdlLeft, format, 7 );
		
		Thread leftReaderThread = new Thread( readerThreads, leftReader, "leftReaderThread" );
		
		rightReader.setReady( true );
		leftReader.setReady( true );
		
		rightReaderThread.start();
		leftReaderThread.start();
		
		// send an initial interrupt to all threads to start them reading...
		
		readerThreads.interrupt();
		
		
		System.out.println( "done" );
	}


	public static Mixer getMixer( String name )
	{
		/*
	 		mixer: class com.sun.media.sound.DirectAudioDevice --> Audio [plughw:2,0] | ALSA (http://www.alsa-project.org) | Direct Audio Device: USB Audio, USB Audio, USB Audio | 5.19.0-76051900-generic
			mixer: class com.sun.media.sound.DirectAudioDevice --> Audio_1 [plughw:3,0] | ALSA (http://www.alsa-project.org) | Direct Audio Device: USB Audio, USB Audio, USB Audio | 5.19.0-76051900-generic
		 */
	
		Mixer mixerToReturn = null;
		
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		
		for( Mixer.Info mxInfo : mixerInfos )
		{
				
			String mxName = mxInfo.getName();
			
			// System.out.println( "mxName: " + mxName );
			// System.out.println( "class: " + mxInfo.getClass().getCanonicalName() );
			
			if( mxInfo.getClass().getCanonicalName().toString().equalsIgnoreCase( "com.sun.media.sound.DirectAudioDeviceProvider.DirectAudioDeviceInfo" ) && mxName.matches( mixerNames.get( name ) + "\\s+.*" ))
			{
				// System.out.println( "mixer: " + mxName + " matches for name: " + name );
				
				mixerToReturn = AudioSystem.getMixer( mxInfo );
				break;
			}
		}
		
		return mixerToReturn;
	}
}
