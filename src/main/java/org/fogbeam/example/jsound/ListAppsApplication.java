package org.fogbeam.example.jsound;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import com.google.common.reflect.ClassPath;

public class ListAppsApplication implements CommandLineRunner
{
	public static void main( String[] args )
	{
		SpringApplication.run(  ListAppsApplication.class, args );
	}

	@Override
	public void run( String... args ) throws Exception
	{
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		// System.out.println( "loader: " + loader );
		
		ClassPath cp = ClassPath.from(loader);
		
		/* 
		System.out.println( "cp: " + cp );
		
		ImmutableSet<ClassInfo> classes = cp.getAllClasses();
		
		if( classes != null )
		{
			System.out.println( "Classes: " + classes.size() );
		
			for( ClassInfo cpInfo: classes )
			{
				System.out.println( cpInfo.getName() );
			}
		
		}
		else
		{
			System.out.println( "No classes?!???" );
		}
		*/
		
		for(ClassPath.ClassInfo info : cp.getTopLevelClassesRecursive("BOOT-INF.classes.org.fogbeam")) 
		{
			String name = info.getName();
			
			if( name.endsWith( "Application"  ))
			{
				// System.out.println( "Name: " + name );
				System.out.println( StringUtils.substringBeforeLast( StringUtils.substringAfterLast(name, "." ), "Application" ) );
			}
		
		}
		
		System.out.println( "Done" );
	}
	
	
}
