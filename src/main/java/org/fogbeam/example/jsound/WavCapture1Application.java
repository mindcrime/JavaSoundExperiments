
package org.fogbeam.example.jsound;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.TargetDataLine;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;


public class WavCapture1Application implements CommandLineRunner
{
	public static void main( String[] args ) throws Exception
	{
		SpringApplication.run( WavCapture1Application.class, args );
	}

	public void run( String... args ) throws Exception
	{
		TargetDataLine line = null;
		File outFile = new File( "output.wav" );
		if( outFile.exists() )
		{
			System.out.println( "deleting old file" );
			outFile.delete();
		}
		outFile = new File( "output.wav" );
		// AudioFormat(float sampleRate, int sampleSizeInBits, int channels,
		// boolean signed, boolean bigEndian)
		AudioFormat format = new AudioFormat( 24000.0f, 8, 1, true, false );
		DataLine.Info info = new DataLine.Info( TargetDataLine.class, format );
		if( !AudioSystem.isLineSupported( info ) )
		{
			throw new RuntimeException(
					"Line: (" + info.toString() + ") not supported!" );
		}
		// Obtain and open the line.
		try
		{
			// find this specific mixer and use its TargetDataLine?
			// Port PCH [hw:0]
			Info[] mixers = AudioSystem.getMixerInfo();
			for( Info mixerInfo : mixers )
			{
				// System.out.println( "mixInfo: " + mixerInfo );
				/*
				 * Mixer desiredMixer = AudioSystem.getMixer( mixerInfo );
				 * Line.Info targetLineInfo[] =
				 * desiredMixer.getTargetLineInfo();
				 * 
				 * System.out.println( "targetLineInfo.length " +
				 * targetLineInfo.length );
				 * 
				 */
				if( mixerInfo.getName().startsWith( "PCH [plughw:0,0]" ) )
				{
					Mixer desiredMixer = AudioSystem.getMixer( mixerInfo );
					Line.Info targetLineInfo[] = desiredMixer
							.getTargetLineInfo();
					Line.Info tlInfo = targetLineInfo[0];
					System.out.println( "tlInfo: " + tlInfo );
					line = AudioSystem.getTargetDataLine( format, mixerInfo );
				}
			}
			// line = (TargetDataLine) AudioSystem.getLine( info );
			line.open( format );
			// Assume that the TargetDataLine, line, has already
			// been obtained and opened.
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int numBytesRead;
			byte[] data = new byte[line.getBufferSize() / 5];
			// Begin audio capture
			int bytesPerFrame = format.getFrameSize();
			int totalFramesRead = 0;
			System.out.println( "Reading..." );
			line.start();
			boolean stopped = false;
			// Here, stopped is a global boolean set by another thread.
			long milliseconds = System.currentTimeMillis();
			while( !stopped )
			{
				// Read the next chunk of data from the TargetDataLine.
				numBytesRead = line.read( data, 0, data.length );
				// Save this chunk of data.
				out.write( data, 0, numBytesRead );
				int framesRead = numBytesRead / bytesPerFrame;
				totalFramesRead += framesRead;
				long currentMilliseconds = System.currentTimeMillis();
				long delta = currentMilliseconds - milliseconds;
				// capture for 4 seconds
				if( delta > 7000 )
				{
					stopped = true;
				}
			}
			Thread.sleep( 2000 );
			System.out.println( "totalFramesRead: " + totalFramesRead );
			ByteArrayInputStream bais = new ByteArrayInputStream(
					out.toByteArray() );
			AudioInputStream audioInputStream = new AudioInputStream( bais,
					format, totalFramesRead );
			AudioSystem.write( audioInputStream, Type.WAVE, outFile );
		}
		catch( LineUnavailableException ex )
		{
			throw new RuntimeException( ex );
		}
		System.out.println( "done" );
	}
}
