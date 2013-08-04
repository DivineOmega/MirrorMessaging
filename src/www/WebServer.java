package www;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer extends Thread 
{
	public boolean isListening = false;
	private int port;
	private String interfaceIP;

	public WebServer(String interfaceIP, int port)
	{
		this.interfaceIP = interfaceIP;
		this.port = port;
	}
		
	public void run()
	{
		ServerSocket serverSocket = null;
		try 
		{
			serverSocket = new ServerSocket(port, 0, InetAddress.getByName(interfaceIP));
		} 
		catch (IOException e) 
		{
			System.out.println(e.toString());
			System.out.println("Error starting local server.");
			System.exit(0);
		}
		
		System.out.println("Local web server started on port "+port+".");
		
		while (serverSocket!=null)
		{
			isListening = true;
			
			try 
			{
				Socket socket = serverSocket.accept();

				WebServerConnection wsc = new WebServerConnection(socket);
				wsc.start();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}

		}
		
		isListening = false;
		
	}
}
