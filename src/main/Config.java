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
		properties.setProperty("bitmessage.host", "localhost");
		properties.setProperty("bitmessage.port", "8442");
		properties.setProperty("bitmessage.user", "mirror_messaging");
		properties.setProperty("bitmessage.pass", "123");
		
		properties.setProperty("webserver.interface", "127.0.0.1");
		properties.setProperty("webserver.port", "2940");
		
		properties.setProperty("ui.theme", "main");
	}
	
	private void load()
	{		
		setDefaults();
		
		System.out.println("Loading config file: "+mainConfigFile);
		
		try 
		{
			properties.load(new FileInputStream(mainConfigFile));
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println("Config file not found. It will be created.");
		} 
		catch (IOException e) 
		{
			System.out.println("Error reading config file. Exiting.");
			System.exit(-1);
		}
		
		System.out.println("Config file loaded.");
		save();
	}
	
	private void save()
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

	public String getProperty(String key) 
	{
		return properties.getProperty(key);
	}
	
	public void setProperty(String key, String value) 
	{
		properties.setProperty(key, value);
		save();
	}
}
