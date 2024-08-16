package org.fogbeam.example.jsound;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

public class JSound1Application implements CommandLineRunner
{

	public static void main(String[] args) throws Exception
	{
		SpringApplication.run(  JSound1Application.class, args );
	}
	
	@Override
	public void run( String... args ) throws Exception
	{	
		Info[] mixers = AudioSystem.getMixerInfo();
		
		for( Info mixerInfo : mixers )
		{
			System.out.println( "mixInfo: " + mixerInfo );
		
			
			if( true /* mixerInfo.getName().startsWith("default" ) */ )
			{
				System.out.println( mixerInfo.getName() + " - " + mixerInfo.getVendor() + " : " + mixerInfo.getDescription() );
				
				Mixer mixer = AudioSystem.getMixer(mixerInfo);
				
				/*
				Line.Info[] slInfo = mixer.getSourceLineInfo();
				
				for( Line.Info lineInfo : slInfo )
				{
					System.out.println( "lineInfo: " + lineInfo );
				}	
				
				Line[] sourceLines = mixer.getSourceLines();
			
				for( Line sl : sourceLines )
				{
					System.out.println( sl );
				}
				*/
				
				/*
				Line.Info[] tlInfo = mixer.getTargetLineInfo();
				
				for( Line.Info lineInfo : tlInfo )
				{
					System.out.println( "lineInfo: " + lineInfo );
											       // AudioFormat(float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian)
					TargetDataLine targetDataLine = AudioSystem.getTargetDataLine( new AudioFormat(44000.0f, 8, 1, true, false), mixerInfo );
					System.out.println( "targetDataLine:  " + targetDataLine );
					
				}	
				*/
				
				Line[] targetLines = mixer.getTargetLines();
			
				for( Line tl : targetLines )
				{
					System.out.println( "TargetLine: " + tl );
				}
				
			}
			
		}
		
		/*
		if (AudioSystem.isLineSupported(Port.Info.MICROPHONE )) 
		{
			System.out.println( "Getting microphone" );
			
		    try 
		    {
		        Line line = (Port) AudioSystem.getLine(Port.Info.MICROPHONE);
		        System.out.println( "line: " + line.getLineInfo());
		    }
		    catch( Exception e )
		    {
		    	e.printStackTrace();
		    }
		}
		else
		{
			System.out.println( "No Microphone!" );
		}
		*/
		
		
		/*
		Line.Info[] slInfo = AudioSystem.getSourceLineInfo(Port.Info.MICROPHONE);
		
		for( Line.Info sl : slInfo )
		{
			System.out.println( "sl: " + sl.toString() );
		}
		*/
		
		System.out.println( "done" );
	}

}
