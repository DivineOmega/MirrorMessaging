package www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import main.Main;

import org.apache.xmlrpc.XmlRpcException;

import bitmsg.Address;
import bitmsg.BitMsgComms;
import bitmsg.Message;

public class WebServerConnection extends Thread 
{
	private Socket socket;

	public WebServerConnection(Socket incomingSocket)
	{
		this.socket = incomingSocket;
	}
	
	private String getTemplateHTML(String templateName) throws IOException
	{
		InputStream is = getClass().getResourceAsStream("/resources/themes/"+Main.config.getProperty("ui.theme")+"/html/"+templateName);

		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		
		String template = "";
		String line;
		while ((line = in.readLine()) != null) 
		{
			template += line;
			template += "\n";
		}
		
		in.close();
		
		return template;
	}
	
	
	private String getCSS(String cssName) throws IOException
	{
		InputStream is = getClass().getResourceAsStream("/resources/themes/"+Main.config.getProperty("ui.theme")+"/css/"+cssName);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		
		String template = "";
		String line;
		while ((line = in.readLine()) != null) 
		{
			template += line;
			template += "\n";
		}
		
		in.close();
		
		return template;
	}
	
	private String getPreparedTemplateHTML(String templateName) throws IOException
	{
		String html = getTemplateHTML(templateName);
		
		html = html.replace("[[sidebar]]", getTemplateHTML("sidebar.html"));
		
		String fromSelect = "";
		fromSelect += "<select name=\"from\">";
		
		try
		{
			BitMsgComms bitMsgComms = new BitMsgComms();
			ArrayList<Address> addresses = bitMsgComms.listAddresses();
			
			for (Address address : addresses) 
			{
				fromSelect += "<option value=\""+address.getAddress()+"\">";
				fromSelect += address.getLabel()+" ("+address.getAddress()+")";
				fromSelect += "</option>";
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		fromSelect += "</select>";
		
		html = html.replace("[[fromSelect]]", fromSelect);
		
		return html;
	}
	
	
	public void run()
	{
		try 
		{
			if (socket==null) return;
			
			socket.setSoTimeout(5000);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String request = in.readLine();
			if (request==null)
			{
				out.close();
				in.close();
				socket.close();
				return;
			}
			String[] requestParts = request.split(" ");
			System.out.println(request);
			
			if (requestParts[1].equals("/send_message"))
			{
				String from = null;
				String to = null;
				String subject = null;
				String body = null;
				
				String line = "";
				while ((line = in.readLine()) != null)
				{
					if (line.equals("Content-Disposition: form-data; name=\"to\""))
					{
						in.readLine();
						to = in.readLine();
					}
					else if (line.equals("Content-Disposition: form-data; name=\"from\""))
					{
						in.readLine();
						from = in.readLine();
					}
					else if (line.equals("Content-Disposition: form-data; name=\"subject\""))
					{
						in.readLine();
						subject = in.readLine();
					}
					else if (line.equals("Content-Disposition: form-data; name=\"body\""))
					{
						in.readLine();
						body = in.readLine();
					}
					if (from!=null && to!=null && subject!=null && body != null) break;
				}
				
				if (from!=null && to!=null && subject!=null && body != null)
				{
					BitMsgComms bitMsgComms = new BitMsgComms();
					Message sentMessage = bitMsgComms.sendMessage(to, from, subject, body);
					
					if (sentMessage!=null && !sentMessage.getAckData().isEmpty())
					{
						out.println("HTTP/1.0 302 Redirect");
						out.println("Location: /sent/message/"+sentMessage.getAckData());
					}
					else
					{
						out.println("HTTP/1.0 200 OK");
						String contentType = "text/html";
						out.println("Content-Type: "+contentType+"; charset=UTF-8");
						out.println();
						out.println("Sorry, there was an error sending this message. Please go back and try again.");
					}
				}
				else
				{
					out.println("HTTP/1.0 200 OK");
					String contentType = "text/html";
					out.println("Content-Type: "+contentType+"; charset=UTF-8");
					out.println();
					out.println("Required message data was missing. Please go back and try again.");
				}
								
				out.flush();
				out.close();
				in.close();
				socket.close();
			}
			else if (requestParts[1].equals("/"))
			{
				out.println("HTTP/1.0 302 Temporary Redirect");
				out.println("Location: /inbox");
				out.println();
			}
			else
			{
				out.println("HTTP/1.0 200 OK");
				String contentType = "text/html";
				if (requestParts[1].startsWith("/css/")) contentType = "text/css";
				out.println("Content-Type: "+contentType+"; charset=UTF-8");
				out.println();
			}
			
			if (requestParts[1].equals("/"))
			{
				out.println("You should be redirected to /inbox");
			}
			else if (requestParts[1].equals("/css/main.css"))
			{
				out.println(getCSS("main.css"));
			}
			else if (requestParts[1].equals("/compose"))
			{
				out.println(getPreparedTemplateHTML("compose.html"));
			}
			else if (requestParts[1].equals("/inbox"))
			{
				BitMsgComms bitMsgComms = new BitMsgComms();
				ArrayList<Message> msgs = bitMsgComms.getAllInboxMessages();
				
				String template = getPreparedTemplateHTML("inbox.html");
				
				String inboxTable = "";
				
				SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm"); 
				
				inboxTable += "<table id=\"inboxTable\">";
				for (Message msg : msgs) 
				{
					inboxTable += "<tr ";
					if (msg.isUnread()) inboxTable += "class=\"unreadMessageListing\"";
					inboxTable += ">";
					
					inboxTable += "<td>";
					inboxTable += msg.getFromAddress();
					inboxTable += "</td>";
					
					inboxTable += "<td>";
					inboxTable += "<a href=\"/inbox/message/"+msg.getId()+"\">";
					inboxTable += msg.getSubject();
					inboxTable += "</a>";
					inboxTable += "</td>";
					
					inboxTable += "<td>";
					inboxTable += fullDateFormat.format(msg.getReceivedTime());
					inboxTable += "</td>";
					
					inboxTable += "</tr>";
				}
				inboxTable += "</table>";
				
				String output = template;
				output = output.replace("[[inboxTable]]", inboxTable);
				
				out.println(output);
			}
			else if (requestParts[1].equals("/sent"))
			{
				BitMsgComms bitMsgComms = new BitMsgComms();
				ArrayList<Message> msgs = bitMsgComms.getAllSentMessages();
				
				String output = getPreparedTemplateHTML("sent.html");
				
				String sentTable = "";
				
				SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm"); 
				
				sentTable += "<table id=\"sentTable\">";
				for (Message msg : msgs) 
				{
					sentTable += "<tr>";
					
					sentTable += "<td>";
					sentTable += msg.getToAddress();
					sentTable += "</td>";
					
					sentTable += "<td>";
					sentTable += "<a href=\"/sent/message/"+msg.getAckData()+"\">";
					sentTable += msg.getSubject();
					sentTable += "</a>";
					sentTable += "</td>";
					
					sentTable += "<td>";
					sentTable += fullDateFormat.format(msg.getReceivedTime());
					sentTable += "</td>";
					
					sentTable += "<td>";
					sentTable += msg.getHumanFriendlyStatus();
					sentTable += "</td>";
					
					sentTable += "</tr>";
				}
				sentTable += "</table>";
				
				output = output.replace("[[sentTable]]", sentTable);
				
				out.println(output);
				
			}
			else if (requestParts[1].startsWith("/inbox/message/"))
			{
				String msg_id = requestParts[1].replace("/inbox/message/", "");
				
				BitMsgComms bitMsgComms = new BitMsgComms();
				Message msg = bitMsgComms.getInboxMessageById(msg_id);
				
				String output = getPreparedTemplateHTML("inbox_msg.html");
				
				output = output.replace("[[subject]]", msg.getSubject());
				output = output.replace("[[fromAddress]]", msg.getFromAddress());
				output = output.replace("[[toAddress]]", msg.getToAddress());
				output = output.replace("[[message]]", msg.getMessage().replace("\n", "<br/>"));
				
				out.println(output);
				
			}
			else if (requestParts[1].startsWith("/sent/message/"))
			{
				String ackData = requestParts[1].replace("/sent/message/", "");
				
				BitMsgComms bitMsgComms = new BitMsgComms();
				Message msg = bitMsgComms.getSentMessageByAckData(ackData);
				
				String output = getPreparedTemplateHTML("sent_msg.html");
				
				output = output.replace("[[subject]]", msg.getSubject());
				output = output.replace("[[fromAddress]]", msg.getFromAddress());
				output = output.replace("[[toAddress]]", msg.getToAddress());
				output = output.replace("[[message]]", msg.getMessage().replace("\n", "<br/>"));
				output = output.replace("[[status]]", msg.getHumanFriendlyStatus());
				
				out.println(output);
				
			}
			else if (requestParts[1].equals("/test"))
			{
				out.println("Try: <a href=\"/test/add\">/test/add</a> or <a href=\"/test/hello\">/test/hello</a>");
			}
			else if (requestParts[1].equals("/test/add"))
			{
				BitMsgComms bitMsgComms = new BitMsgComms();
				out.println(bitMsgComms.add(1, 2));
			}
			else if (requestParts[1].equals("/test/hello"))
			{
				BitMsgComms bitMsgComms = new BitMsgComms();
				out.println(bitMsgComms.helloWorld("String1", "String2"));
			}
			else
			{
				out.println("Error. Request invalid.");
			}
			
			out.flush();
			out.close();
			in.close();
			socket.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (XmlRpcException e) 
		{
			e.printStackTrace();
		}
		
	}
}
