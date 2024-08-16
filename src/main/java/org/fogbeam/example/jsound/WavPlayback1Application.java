
package org.fogbeam.example.jsound;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

public class WavPlayback1Application implements CommandLineRunner
{
	
	public static void main( String[] args ) throws Exception
	{
		SpringApplication.run( WavPlayback1Application.class, args );
	}
	
	public void run( String ... args ) throws Exception
	{
		
		File wavFile = new File( "/extradrive1/development/data/freesound_sounds/480002__craigsmith__r18-31-old-car-ahooga-horn.wav" );
		
		AudioFileFormat wavFileFormat = AudioSystem.getAudioFileFormat( wavFile );
		
		System.out.println( "wavFileFormat: " + wavFileFormat.getFormat() + ", byteLength: " + wavFileFormat.getByteLength() 
							+ ", frameLength: " + wavFileFormat.getFrameLength() + ", type: " + wavFileFormat.getType() );
		
		
		SourceDataLine sdl = AudioSystem.getSourceDataLine( wavFileFormat.getFormat() );
		if( !sdl.isOpen() )
		{
			System.out.println( "SDL not initially open!" );
			sdl.open(wavFileFormat.getFormat() );
		}
		
		if( !sdl.isOpen() )
		{
			throw new RuntimeException( "Error with SDL" );
		}
		
		System.out.println( "SDL open" );
		
		try 
		{
			  AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
			  int bytesPerFrame = audioInputStream.getFormat().getFrameSize();
			  
			  // Set an arbitrary buffer size of 1024 frames.
			  int numBytes = 1024 * bytesPerFrame; 
			  byte[] audioBytes = new byte[numBytes];
			  
			  try 
			  {
				  int numBytesRead = 0;
				  int numFramesRead = 0;
				  
				  sdl.start();
				  
				  // Try to read numBytes bytes from the file.
				  while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) 
				  {
					  
					  numFramesRead++;
					  
					  int bytesWritten = sdl.write( audioBytes, 0, numBytesRead );
					  // System.out.println( "bytesWritten: " + bytesWritten );
				  }
			  	} 
			  catch (Exception ex) 
			  { 
				  // Handle the error...
				  ex.printStackTrace();
			  }
		
		} 
		catch (Exception e) 
		{
			// Handle the error...
			e.printStackTrace();
		}		
		
		System.out.println( "done" );
	}
}
