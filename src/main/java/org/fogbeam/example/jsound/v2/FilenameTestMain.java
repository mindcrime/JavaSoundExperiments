package org.fogbeam.example.jsound.v2;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

public class FilenameTestMain
{
	public static void main( String[] args ) throws Exception
	{

		Instant foo = Instant.now();
		OffsetDateTime now = foo.atOffset( ZoneOffset.UTC );
		
		int year = now.get( ChronoField.YEAR );
		int month = now.get( ChronoField.MONTH_OF_YEAR );
		int day = now.get( ChronoField.DAY_OF_MONTH );
		int hour = now.get( ChronoField.HOUR_OF_DAY );
		int minute = now.get( ChronoField.MINUTE_OF_HOUR );
		
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
														+ "-right.wav");
		
		
		BufferedOutputStream buffOut = new BufferedOutputStream(  out );
		
		buffOut.write( "Hello Motherfucking World!\n".getBytes() );
		
		// buffOut.write( ArrayUtils.toPrimitive( elements ) );
		
		buffOut.flush();
		buffOut.close();
		
		
	}
}
