package org.fogbeam.example.jsound;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

public class ListMixersWithMicInput1Application implements CommandLineRunner
{
	
	public static void main(String[] args) throws Exception
	{
		SpringApplication.run(  ListMixersWithMicInput1Application.class, args );
	}
	
	public void run( String ... args) throws Exception
	{
		
		Info[] mixerInfos = AudioSystem.getMixerInfo();
		
		List<Mixer> mixersWithMicInput = new ArrayList<Mixer>();
		
		AudioFormat format = new AudioFormat( 44100.0f, 8, 1, false, false );
		
		for( Mixer.Info mxInfo : mixerInfos )
		{
			Mixer mixer = AudioSystem.getMixer( mxInfo );
			
			// System.out.println( mixer.toString() );
			
			mixer.open();
			
			Line.Info[] targetLineInfos = mixer.getTargetLineInfo();
			
			for( Line.Info tlInfo : targetLineInfos )
			{
				// System.out.println( "\t\t-- " + tlInfo.toString() );
				try
				{
					AudioSystem.getTargetDataLine( format, mxInfo );
					// if we got here with no exception, add this mixer to the list
					mixersWithMicInput.add( mixer );
					
				}
				catch( Exception e )
				{
					// this mixer doesn't support what we're looking for, so skip it
					continue;
				}
			}
		}
		
		for( Mixer mixer : mixersWithMicInput )
		{
			System.out.println( "mixer: " + mixer.getClass() + " --> " + printMixerInfo( mixer ) );
		}
		
		System.out.println("done" );
	}

	public static String printMixerInfo( Mixer mixer )
	{
		Mixer.Info mxInfo = mixer.getMixerInfo();
		StringBuilder retStr = new StringBuilder();
		retStr.append( mxInfo.getName() );
		retStr.append( " | " );
		retStr.append( mxInfo.getVendor() );
		retStr.append( " | " );
		retStr.append( mxInfo.getDescription() );
		retStr.append( " | " );
		retStr.append( mxInfo.getVersion() );
		
		
		return retStr.toString();
	}
}
