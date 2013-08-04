package main;

import java.awt.Desktop;
import java.net.URI;

import www.WebServer;

public class Main 
{
	public static Config config = new Config();
	
	public static void main(String[] args) 
	{
		String interfaceIP = Main.config.getProperty("webserver.interface");
		int port = Integer.parseInt(Main.config.getProperty("webserver.port"));
		
		WebServer webServer = new WebServer(interfaceIP, port);		
		webServer.start();
		
		System.out.println("Waiting for web server to start...");
		while(!webServer.isListening)
		{
			try
			{
				Thread.sleep(50);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		
		System.out.println("Launching web browser...");
		try
		{
			URI url = new URI("http://"+interfaceIP+":"+port+"/");
			System.out.println("URL: "+url);
			if (Desktop.isDesktopSupported())
			{
				Desktop.getDesktop().browse(url);
				System.out.println("Launched web browser.");
			}
			else
			{
				System.out.println("Launching web browser is not supported.");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		while (true)
		{
			try
			{
				Thread.sleep(1000);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			
		}
	}

}
