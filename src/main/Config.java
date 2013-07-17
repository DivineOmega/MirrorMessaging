package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class Config 
{
	private Properties properties = new Properties();
	private File mainConfigFile = null;
	
	public Config()
	{
		File mainConfigFileDirectory = new File(System.getProperty("user.home")+System.getProperty("file.separator")+".mirror_messaging");
		mainConfigFileDirectory.mkdir();
		
		mainConfigFile = new File(mainConfigFileDirectory.getAbsolutePath()+System.getProperty("file.separator")+"config.txt");
		
		load();
	}
	
	private void setDefaults()
	{
		
	}
	
	public void load()
	{		
		setDefaults();
		
		try 
		{
			properties.load(new FileInputStream(mainConfigFile));
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println("Config file not found. It will be created.");
			save();
		} 
		catch (IOException e) 
		{
			System.out.println("Error reading config file. Exiting.");
			System.exit(-1);
		}
	}
	
	public void save()
	{
		System.out.println("Saving config file: "+mainConfigFile);
		
		try 
		{
			properties.store(new FileOutputStream(mainConfigFile), null);
		} 
		catch (IOException e) 
		{
			System.out.println("Error saving config file.");
			System.exit(-1);
		}
		
		System.out.println("Config file saved.");
	}
}
